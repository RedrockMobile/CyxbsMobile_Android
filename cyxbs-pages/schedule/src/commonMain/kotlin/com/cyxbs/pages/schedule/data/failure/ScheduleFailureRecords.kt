package com.cyxbs.pages.schedule.data.failure

import com.cyxbs.components.config.sp.AccountSettings
import com.cyxbs.pages.schedule.data.failure.SettingsScheduleFailureRecordStore.Companion.SETTINGS_CHUNK_CHARS
import com.cyxbs.pages.schedule.data.remote.v3.AtomicBatch
import com.cyxbs.pages.schedule.data.remote.v3.AtomicBatchResultCode
import com.cyxbs.pages.schedule.data.remote.v3.CategoryAtomicBlock
import com.cyxbs.pages.schedule.data.remote.v3.MutationResultCode
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideAtomicBlock
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleAtomicBlock
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
 * [sourceSchedule] 用于恢复编辑器，[sourceBatch] 保留失败时实际提交的分类、日程与单次覆盖闭包，便于用户修复和
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
  val sourceBatch: AtomicBatch,
)

/** Repository 使用的最小失败记录能力，测试可注入内存实现。 */
internal interface ScheduleFailureRecordSink {
  /** 观察指定账号的全部失败记录，按最近失败时间倒序。 */
  fun observe(accountId: String): StateFlow<List<ScheduleFailureRecord>>

  /** 写入或更新同一 Schedule 的失败记录。 */
  fun record(accountId: String, records: List<ScheduleFailureRecord>)

  /** 已有失败项再次编辑或修改单次覆盖后，用最新请求刷新记录，但不改变原失败原因。 */
  fun refreshSources(accountId: String, batch: AtomicBatch)

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

  override fun refreshSources(accountId: String, batch: AtomicBatch) {
    synchronized(lock) {
      val state = stateLocked(accountId)
      if (state.value.isEmpty()) return
      val affectedIds = batch.scheduleIds()
      val upserts = batch.schedules.upserts.associateBy { it.id }
      var changed = false
      val refreshed = state.value.map { record ->
        if (record.scheduleId !in affectedIds) return@map record
        changed = true
        record.copy(
          // Schedule upsert 刷新编辑器源数据；只改 occurrence 时仍保留可打开的原 Schedule。
          sourceSchedule = upserts[record.scheduleId] ?: record.sourceSchedule,
          sourceBatch = batch,
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
 * 为一个或多个失败原子批次创建按 Schedule 聚合的记录。
 *
 * Schedule upsert 直接使用请求源；delete/override 则从 [currentSchedules] 恢复当前有效源，保证记录仍可打开
 * 编辑器。只含 Category 且无法关联 Schedule 的失败不伪造条目。
 */
internal fun createScheduleFailureRecords(
  operation: ScheduleFailureOperation,
  batches: List<AtomicBatch>,
  currentSchedules: List<ScheduleSyncState>,
  failedAt: Long,
  reasonCode: String,
  message: String,
): List<ScheduleFailureRecord> {
  val currentById = currentSchedules.mapNotNull { state ->
    // pending DELETE 会让 effectiveResource() 为空，失败记录仍需回退到删除前的 remote 资源供用户恢复编辑。
    (state.effectiveResource() ?: state.remoteSnapshot?.resource)
      ?.let { state.identity.id to it.toWire() }
  }.toMap()
  return batches.flatMap { batch ->
    val ids = buildSet {
      batch.schedules.upserts.mapTo(this) { it.id }
      batch.schedules.deletes.mapTo(this) { it.id }
      batch.occurrenceOverrides.upserts.mapTo(this) { it.scheduleId }
      batch.occurrenceOverrides.deletes.mapTo(this) { it.scheduleId }
    }
    ids.mapNotNull { scheduleId ->
      val source = batch.schedules.upserts.firstOrNull { it.id == scheduleId }
        ?: currentById[scheduleId]
        ?: return@mapNotNull null
      ScheduleFailureRecord(
        scheduleId = scheduleId,
        failedAt = failedAt,
        operation = operation,
        reasonCode = reasonCode,
        message = message,
        sourceSchedule = source,
        sourceBatch = batch,
      )
    }
  }.distinctBy { it.scheduleId }
}

/** 日常 HTTP 方法映射为用户可读的失败操作类型。 */
internal fun ScheduleV2DailyMutationMethod.toFailureOperation(): ScheduleFailureOperation =
  when (this) {
    ScheduleV2DailyMutationMethod.CREATE -> ScheduleFailureOperation.CREATE
    ScheduleV2DailyMutationMethod.UPDATE -> ScheduleFailureOperation.UPDATE
    ScheduleV2DailyMutationMethod.DELETE -> ScheduleFailureOperation.DELETE
  }

/** HTTP 400 没有逐项结果，因此保留本次 Sync 的普通块与全部原子批次。 */
internal fun SyncRequest.allMutationBatches(): List<AtomicBatch> {
  val ordinary = AtomicBatch(
    batchId = "$syncRequestId-ordinary",
    categories = CategoryAtomicBlock(categories.upserts, categories.deletes),
    schedules = ScheduleAtomicBlock(schedules.upserts, schedules.deletes),
    occurrenceOverrides = OccurrenceOverrideAtomicBlock(
      occurrenceOverrides.upserts,
      occurrenceOverrides.deletes,
    ),
  )
  return buildList {
    if (ordinary.hasScheduleRelatedMutation()) add(ordinary)
    addAll(atomicBatches.filter { it.hasScheduleRelatedMutation() })
  }
}

/**
 * 从 HTTP 200 + REJECTED 中选出真正失败的请求成员。
 *
 * 普通结果按下标与输入对齐；原子批次只要总体或任一成员 REJECTED，就保留完整批次，避免丢失分类与
 * override 依赖闭包。成功成员不会被误记为失败。
 */
internal fun SyncRequest.rejectedMutationBatches(response: SyncResponse): List<AtomicBatch> {
  val rejectedScheduleUpserts = schedules.upserts.filterIndexed { index, _ ->
    response.schedules.upsertResults.getOrNull(index)?.code == MutationResultCode.REJECTED
  }
  val rejectedScheduleDeletes = schedules.deletes.filterIndexed { index, _ ->
    response.schedules.deleteResults.getOrNull(index)?.code == MutationResultCode.REJECTED
  }
  val rejectedOverrideUpserts = occurrenceOverrides.upserts.filterIndexed { index, _ ->
    response.occurrenceOverrides.upsertResults.getOrNull(index)?.code == MutationResultCode.REJECTED
  }
  val rejectedOverrideDeletes = occurrenceOverrides.deletes.filterIndexed { index, _ ->
    response.occurrenceOverrides.deleteResults.getOrNull(index)?.code == MutationResultCode.REJECTED
  }
  val ordinary = AtomicBatch(
    batchId = "$syncRequestId-rejected-ordinary",
    categories = CategoryAtomicBlock(emptyList(), emptyList()),
    schedules = ScheduleAtomicBlock(rejectedScheduleUpserts, rejectedScheduleDeletes),
    occurrenceOverrides = OccurrenceOverrideAtomicBlock(
      rejectedOverrideUpserts,
      rejectedOverrideDeletes,
    ),
  )
  val atomicById = atomicBatches.associateBy { it.batchId }
  val rejectedAtomic = response.atomicBatchResults.mapNotNull { result ->
    val rejected = result.code == AtomicBatchResultCode.REJECTED ||
        result.categories.upsertResults.any { it.code == AtomicBatchResultCode.REJECTED } ||
        result.categories.deleteResults.any { it.code == AtomicBatchResultCode.REJECTED } ||
        result.schedules.upsertResults.any { it.code == AtomicBatchResultCode.REJECTED } ||
        result.schedules.deleteResults.any { it.code == AtomicBatchResultCode.REJECTED } ||
        result.occurrenceOverrides.upsertResults.any { it.code == AtomicBatchResultCode.REJECTED } ||
        result.occurrenceOverrides.deleteResults.any { it.code == AtomicBatchResultCode.REJECTED }
    atomicById[result.batchId].takeIf { rejected }
  }
  return buildList {
    if (ordinary.hasScheduleRelatedMutation()) add(ordinary)
    addAll(rejectedAtomic.filter { it.hasScheduleRelatedMutation() })
  }
}

/** 返回一个批次会影响的 Schedule identity，供成功后移除失败记录。 */
internal fun AtomicBatch.scheduleIds(): Set<String> = buildSet {
  schedules.upserts.mapTo(this) { it.id }
  schedules.deletes.mapTo(this) { it.id }
  occurrenceOverrides.upserts.mapTo(this) { it.scheduleId }
  occurrenceOverrides.deletes.mapTo(this) { it.scheduleId }
}

private fun AtomicBatch.hasScheduleRelatedMutation(): Boolean =
  schedules.upserts.isNotEmpty() ||
      schedules.deletes.isNotEmpty() ||
      occurrenceOverrides.upserts.isNotEmpty() ||
      occurrenceOverrides.deletes.isNotEmpty()
