package com.cyxbs.pages.schedule.data.failure

import com.cyxbs.components.config.sp.AccountSettings
import com.cyxbs.pages.schedule.data.failure.SettingsScheduleFailureRecordStore.Companion.SETTINGS_CHUNK_CHARS
import com.cyxbs.pages.schedule.data.remote.v3.CategoryMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.MutationResultCode
import com.cyxbs.pages.schedule.data.remote.v3.MutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.MutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.ResultReason
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleInput
import com.cyxbs.pages.schedule.data.remote.v3.SyncRequest
import com.cyxbs.pages.schedule.data.remote.v3.SyncResponse
import com.cyxbs.pages.schedule.data.repository.v3.ScheduleV2DailyMutationMethod
import com.cyxbs.pages.schedule.data.repository.v3.toWire
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleSyncState
import com.russhwolf.settings.Settings
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** 用户可修复的确定性远端失败类型；网络中断不会创建记录。 */
@Serializable
internal enum class ScheduleFailureOperation {
  CREATE,
  UPDATE,
  DELETE,
  SYNC,
}

/**
 * 一条日程失败记录。
 *
 * [sourceSchedule] 用于恢复编辑器，[sourceRequest] 保留失败时实际提交的分类、日程与单次覆盖，便于用户修复和
 * 后续诊断。记录不保存账号、token 或 HTTP header；账号隔离由 [AccountSettings] 提供。
 */
@Serializable
internal data class ScheduleFailureRecord(
  val scheduleId: String,
  val failedAt: Long,
  val operation: ScheduleFailureOperation,
  val reasonCode: String,
  val message: String,
  val sourceSchedule: ScheduleInput,
  val sourceRequest: MutationRequest,
)

/** Repository 使用的最小失败记录能力，测试可注入内存实现。 */
internal interface ScheduleFailureRecordSink {
  /** 观察指定账号的全部失败记录，按最近失败时间倒序。 */
  fun observe(accountId: String): StateFlow<List<ScheduleFailureRecord>>

  /** 写入或更新同一 Schedule 的失败记录。 */
  fun record(accountId: String, records: List<ScheduleFailureRecord>)

  /** 已有失败项再次编辑或修改单次覆盖后，用最新请求刷新记录，但不改变原失败原因。 */
  fun refreshSources(accountId: String, request: MutationRequest)

  /** 服务端确认成功或用户在本地删除日程后，删除对应 Schedule 的失败记录。 */
  fun remove(accountId: String, scheduleIds: Set<String>)
}

/**
 * AccountSettings 失败记录实现。
 *
 * 每个账号只保存一份有序记录集合。单条 JSON 按 [SETTINGS_CHUNK_CHARS] 切分，避免 Desktop Settings 的
 * 单值上限；写入新集合后再更新 count，读取到坏块时保留其他可解码记录。
 */
internal class SettingsScheduleFailureRecordStore(
  private val settingsProvider: (String) -> Settings = { AccountSettings.get(it) },
) : ScheduleFailureRecordSink {
  private val lock = SynchronizedObject()
  private val states = mutableMapOf<String, MutableStateFlow<List<ScheduleFailureRecord>>>()

  override fun observe(accountId: String): StateFlow<List<ScheduleFailureRecord>> =
    synchronized(lock) { stateLocked(accountId) }

  override fun record(accountId: String, records: List<ScheduleFailureRecord>) {
    if (records.isEmpty()) return
    synchronized(lock) {
      val state = stateLocked(accountId)
      val merged = (state.value.associateBy { it.scheduleId } +
          records.associateBy { it.scheduleId })
        .values
        .sortedByDescending { it.failedAt }
      persistLocked(accountId, merged)
      state.value = merged
    }
  }

  override fun refreshSources(accountId: String, request: MutationRequest) {
    synchronized(lock) {
      val state = stateLocked(accountId)
      if (state.value.isEmpty()) return
      val affectedIds = request.scheduleIds()
      val upserts = request.schedules.upserts.associateBy { it.id }
      var changed = false
      val refreshed = state.value.map { record ->
        if (record.scheduleId !in affectedIds) return@map record
        changed = true
        record.copy(
          // Schedule upsert 刷新编辑器源数据；只改 occurrence 时仍保留可打开的原 Schedule。
          sourceSchedule = upserts[record.scheduleId] ?: record.sourceSchedule,
          sourceRequest = request.onlySchedule(record.scheduleId),
        )
      }
      if (changed) {
        persistLocked(accountId, refreshed)
        state.value = refreshed
      }
    }
  }

  override fun remove(accountId: String, scheduleIds: Set<String>) {
    if (scheduleIds.isEmpty()) return
    synchronized(lock) {
      val state = stateLocked(accountId)
      val retained = state.value.filterNot { it.scheduleId in scheduleIds }
      if (retained.size == state.value.size) return
      persistLocked(accountId, retained)
      state.value = retained
    }
  }

  /** 测试和明确账号清理使用；正常成功链路应调用 [remove]。 */
  internal fun clear(accountId: String) {
    synchronized(lock) {
      persistLocked(accountId, emptyList())
      stateLocked(accountId).value = emptyList()
    }
  }

  private fun stateLocked(accountId: String): MutableStateFlow<List<ScheduleFailureRecord>> =
    states.getOrPut(accountId) { MutableStateFlow(readLocked(accountId)) }

  private fun readLocked(accountId: String): List<ScheduleFailureRecord> {
    val settings = settingsProvider(accountId)
    val count = settings.getInt(RECORD_COUNT_KEY, 0).coerceAtLeast(0)
    return buildList {
      repeat(count) { index ->
        val payload = readChunked(settings, recordKey(index)) ?: return@repeat
        runCatching { json.decodeFromString<ScheduleFailureRecord>(payload) }
          .getOrNull()
          ?.let(::add)
      }
    }.distinctBy { it.scheduleId }.sortedByDescending { it.failedAt }
  }

  private fun persistLocked(accountId: String, records: List<ScheduleFailureRecord>) {
    val settings = settingsProvider(accountId)
    val oldCount = settings.getInt(RECORD_COUNT_KEY, 0).coerceAtLeast(0)
    records.forEachIndexed { index, record ->
      writeChunked(settings, recordKey(index), json.encodeToString(record))
    }
    settings.putInt(RECORD_COUNT_KEY, records.size)
    for (index in records.size until oldCount) removeChunked(settings, recordKey(index))
  }

  private fun writeChunked(settings: Settings, baseKey: String, value: String) {
    val oldCount = settings.getInt(chunkCountKey(baseKey), 0).coerceAtLeast(0)
    val chunks = value.chunked(SETTINGS_CHUNK_CHARS)
    chunks.forEachIndexed { index, chunk -> settings.putString(chunkKey(baseKey, index), chunk) }
    settings.putInt(chunkCountKey(baseKey), chunks.size)
    for (index in chunks.size until oldCount) settings.remove(chunkKey(baseKey, index))
  }

  private fun readChunked(settings: Settings, baseKey: String): String? {
    val count = settings.getInt(chunkCountKey(baseKey), 0)
    if (count <= 0) return null
    return buildString {
      repeat(count) { index ->
        val chunk = settings.getStringOrNull(chunkKey(baseKey, index)) ?: return null
        append(chunk)
      }
    }
  }

  private fun removeChunked(settings: Settings, baseKey: String) {
    val count = settings.getInt(chunkCountKey(baseKey), 0).coerceAtLeast(0)
    repeat(count) { settings.remove(chunkKey(baseKey, it)) }
    settings.remove(chunkCountKey(baseKey))
  }

  private companion object {
    const val RECORD_COUNT_KEY = "schedule_v2_failure_record_count"
    const val SETTINGS_CHUNK_CHARS = 1_800
    val json = Json {
      ignoreUnknownKeys = true
      encodeDefaults = true
    }

    fun recordKey(index: Int) = "schedule_v2_failure_record_$index"
    fun chunkCountKey(baseKey: String) = "${baseKey}_chunk_count"
    fun chunkKey(baseKey: String, index: Int) = "${baseKey}_chunk_$index"
  }
}

/** 进程共享失败记录入口；Repository 写入与清单页面观察的是同一份账号状态。 */
internal object ScheduleFailureRecords :
  ScheduleFailureRecordSink by SettingsScheduleFailureRecordStore()

/**
 * 为结构级失败创建按 Schedule 聚合的记录。
 *
 * HTTP 400 没有逐项结论，因此请求内所有 Schedule/Override 都保留同一失败原因；只含 Category 的请求没有
 * 可打开的日程编辑器，不伪造记录。
 */
internal fun createScheduleFailureRecords(
  operation: ScheduleFailureOperation,
  request: MutationRequest,
  currentSchedules: List<ScheduleSyncState>,
  failedAt: Long,
  reasonCode: String,
  message: String,
): List<ScheduleFailureRecord> = createFailureRecords(
  operation = operation,
  request = request,
  currentSchedules = currentSchedules,
  failedAt = failedAt,
  failures = request.scheduleIds().associateWith { FailureDetail(reasonCode, message) },
)

/** Sync 的逐项 REJECTED 只生成对应 Schedule 的失败记录，不影响同请求中已经成功的资源。 */
internal fun createRejectedScheduleFailureRecords(
  operation: ScheduleFailureOperation,
  request: SyncRequest,
  response: SyncResponse,
  currentSchedules: List<ScheduleSyncState>,
  failedAt: Long,
): List<ScheduleFailureRecord> = createRejectedScheduleFailureRecords(
  operation = operation,
  request = request.toMutationRequest(),
  scheduleUpsertResults = response.schedules.upsertResults.map { ResultDetail(it.result, it.reason, it.info) },
  scheduleDeleteResults = response.schedules.deleteResults.map { ResultDetail(it.result, it.reason, it.info) },
  overrideUpsertResults = response.occurrenceOverrides.upsertResults.map {
    ResultDetail(it.result, it.reason, it.info)
  },
  overrideDeleteResults = response.occurrenceOverrides.deleteResults.map {
    ResultDetail(it.result, it.reason, it.info)
  },
  currentSchedules = currentSchedules,
  failedAt = failedAt,
)

/** 日常逐资源响应的 REJECTED 记录；成功项不会出现在失败页。 */
internal fun createRejectedScheduleFailureRecords(
  operation: ScheduleFailureOperation,
  request: MutationRequest,
  response: MutationResponse,
  currentSchedules: List<ScheduleSyncState>,
  failedAt: Long,
): List<ScheduleFailureRecord> = createRejectedScheduleFailureRecords(
  operation = operation,
  request = request,
  scheduleUpsertResults = response.schedules.upsertResults.map { ResultDetail(it.result, it.reason, it.info) },
  scheduleDeleteResults = response.schedules.deleteResults.map { ResultDetail(it.result, it.reason, it.info) },
  overrideUpsertResults = response.occurrenceOverrides.upsertResults.map {
    ResultDetail(it.result, it.reason, it.info)
  },
  overrideDeleteResults = response.occurrenceOverrides.deleteResults.map {
    ResultDetail(it.result, it.reason, it.info)
  },
  currentSchedules = currentSchedules,
  failedAt = failedAt,
)

/** 日常 HTTP 方法映射为用户可读的失败操作类型。 */
internal fun ScheduleV2DailyMutationMethod.toFailureOperation(): ScheduleFailureOperation =
  when (this) {
    ScheduleV2DailyMutationMethod.CREATE -> ScheduleFailureOperation.CREATE
    ScheduleV2DailyMutationMethod.UPDATE -> ScheduleFailureOperation.UPDATE
    ScheduleV2DailyMutationMethod.DELETE -> ScheduleFailureOperation.DELETE
  }

/** 去掉 inventory，把 Sync 中待提交部分转换为失败记录和日常重试共用的请求形态。 */
internal fun SyncRequest.toMutationRequest(): MutationRequest = MutationRequest(
  requestId = syncRequestId,
  categories = CategoryMutationRequest(categories.upserts, categories.deletes),
  schedules = ScheduleMutationRequest(schedules.upserts, schedules.deletes),
  occurrenceOverrides = OccurrenceOverrideMutationRequest(
    occurrenceOverrides.upserts,
    occurrenceOverrides.deletes,
  ),
)

/** 返回一个请求会影响的 Schedule identity，供刷新源数据和成功后移除失败记录。 */
internal fun MutationRequest.scheduleIds(): Set<String> = buildSet {
  schedules.upserts.mapTo(this) { it.id }
  schedules.deletes.mapTo(this) { it.id }
  occurrenceOverrides.upserts.mapTo(this) { it.scheduleId }
  occurrenceOverrides.deletes.mapTo(this) { it.scheduleId }
}

private fun createRejectedScheduleFailureRecords(
  operation: ScheduleFailureOperation,
  request: MutationRequest,
  scheduleUpsertResults: List<ResultDetail>,
  scheduleDeleteResults: List<ResultDetail>,
  overrideUpsertResults: List<ResultDetail>,
  overrideDeleteResults: List<ResultDetail>,
  currentSchedules: List<ScheduleSyncState>,
  failedAt: Long,
): List<ScheduleFailureRecord> {
  val failures = linkedMapOf<String, FailureDetail>()
  request.schedules.upserts.forEachIndexed { index, input ->
    scheduleUpsertResults.getOrNull(index)?.rejection()?.let { failures[input.id] = failures[input.id] ?: it }
  }
  request.schedules.deletes.forEachIndexed { index, input ->
    scheduleDeleteResults.getOrNull(index)?.rejection()?.let { failures[input.id] = failures[input.id] ?: it }
  }
  request.occurrenceOverrides.upserts.forEachIndexed { index, input ->
    overrideUpsertResults.getOrNull(index)?.rejection()?.let {
      failures[input.scheduleId] = failures[input.scheduleId] ?: it
    }
  }
  request.occurrenceOverrides.deletes.forEachIndexed { index, input ->
    overrideDeleteResults.getOrNull(index)?.rejection()?.let {
      failures[input.scheduleId] = failures[input.scheduleId] ?: it
    }
  }
  return createFailureRecords(operation, request, currentSchedules, failedAt, failures)
}

private fun createFailureRecords(
  operation: ScheduleFailureOperation,
  request: MutationRequest,
  currentSchedules: List<ScheduleSyncState>,
  failedAt: Long,
  failures: Map<String, FailureDetail>,
): List<ScheduleFailureRecord> {
  val currentById = currentSchedules.mapNotNull { state ->
    // pending DELETE 会隐藏 effectiveResource，仍回退到删除前 remote 供用户恢复编辑。
    (state.effectiveResource() ?: state.remoteSnapshot?.resource)
      ?.let { state.identity.id to it.toWire() }
  }.toMap()
  return failures.mapNotNull { (scheduleId, failure) ->
    val source = request.schedules.upserts.firstOrNull { it.id == scheduleId }
      ?: currentById[scheduleId]
      ?: return@mapNotNull null
    ScheduleFailureRecord(
      scheduleId = scheduleId,
      failedAt = failedAt,
      operation = operation,
      reasonCode = failure.reasonCode,
      message = failure.message,
      sourceSchedule = source,
      sourceRequest = request.onlySchedule(scheduleId),
    )
  }
}

/** 每条记录只保存自身相关的 Schedule/Override，避免把其他用户内容复制到无关失败项。 */
private fun MutationRequest.onlySchedule(scheduleId: String): MutationRequest = copy(
  categories = CategoryMutationRequest(emptyList(), emptyList()),
  schedules = ScheduleMutationRequest(
    upserts = schedules.upserts.filter { it.id == scheduleId },
    deletes = schedules.deletes.filter { it.id == scheduleId },
  ),
  occurrenceOverrides = OccurrenceOverrideMutationRequest(
    upserts = occurrenceOverrides.upserts.filter { it.scheduleId == scheduleId },
    deletes = occurrenceOverrides.deletes.filter { it.scheduleId == scheduleId },
  ),
)

private fun ResultDetail.rejection(): FailureDetail? {
  if (result != MutationResultCode.REJECTED) return null
  val code = reason?.name ?: "REJECTED"
  return FailureDetail(code, info?.takeIf { it.isNotBlank() } ?: "服务端拒绝该日程变更：$code")
}

private data class ResultDetail(
  val result: MutationResultCode,
  val reason: ResultReason?,
  val info: String?,
)

private data class FailureDetail(
  val reasonCode: String,
  val message: String,
)
