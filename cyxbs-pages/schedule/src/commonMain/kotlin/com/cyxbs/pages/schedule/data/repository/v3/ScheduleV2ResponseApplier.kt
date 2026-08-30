package com.cyxbs.pages.schedule.data.repository.v3

import com.cyxbs.pages.schedule.data.remote.v3.CategoryConfirmedResult
import com.cyxbs.pages.schedule.data.remote.v3.CategoryDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.CategoryUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.ConfirmedResultCode
import com.cyxbs.pages.schedule.data.remote.v3.MutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.MutationResultCode
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideConfirmedResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleConfirmedResult
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.SyncResponse
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.PendingChange
import com.cyxbs.pages.schedule.domain.sync.v2.PendingDelete
import com.cyxbs.pages.schedule.domain.sync.v2.PendingUpsert
import com.cyxbs.pages.schedule.domain.sync.v2.ResourceIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.RemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.SyncResource

/** fail-closed 拒绝整次应用时的最小原因分类。 */
enum class ScheduleV2ApplyFailureReason {
  REQUEST_ID_MISMATCH,
  RESPONSE_CORRELATION,
  SAME_VERSION_CONFLICT,
  REMOTE_VERSION_REGRESSION,
  INVALID_PAYLOAD,
  INVALID_LOCAL_STATE,
}

/** 响应应用结果；任何结构歧义都返回 Failure，调用方必须保持原状态。 */
sealed interface ScheduleV2ApplyResult {
  data class Success(
    val categories: List<CategorySyncState>,
    val schedules: List<ScheduleSyncState>,
    val occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ) : ScheduleV2ApplyResult

  data class Failure(
    val reason: ScheduleV2ApplyFailureReason,
    val message: String,
  ) : ScheduleV2ApplyResult
}

/**
 * 把同步或日常逐资源响应投影回本地双快照状态。
 *
 * 同一请求允许部分成功：成功项更新 remote 并按 localRevision 清 pending，REJECTED 项保留 pending；请求期间
 * 形成的新 revision 只接受 remote 更新而不会被旧响应清除。任何响应错位都会整次 fail-closed。
 */
class ScheduleV2ResponseApplier {

  /** 应用完整同步响应，包括 confirmed 核对、discovered 资源和本次 mutation 结果。 */
  fun apply(
    capture: ScheduleV2SyncCapture,
    response: SyncResponse,
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2ApplyResult = guardedApply {
    abortUnless(
      response.syncRequestId == capture.request.syncRequestId,
      ScheduleV2ApplyFailureReason.REQUEST_ID_MISMATCH,
      "syncRequestId does not match capture",
    )
    validateConfirmed(capture, response)
    val states = LocalStates(categories, schedules, occurrenceOverrides)
    val clears = correlateResults(
      capture.categories,
      capture.schedules,
      capture.occurrenceOverrides,
      response.categories.upsertResults,
      response.categories.deleteResults,
      response.schedules.upsertResults,
      response.schedules.deleteResults,
      response.occurrenceOverrides.upsertResults,
      response.occurrenceOverrides.deleteResults,
    )
    val candidates = collectSyncCandidates(response)
    applyResolved(states, candidates, clears)
  }

  /** 应用日常请求响应；三个资源块都与 capture 中的对应输入严格按下标对齐。 */
  fun applyMutation(
    capture: ScheduleV2MutationCapture,
    response: MutationResponse,
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2ApplyResult = guardedApply {
    abortUnless(
      response.requestId == capture.request.requestId,
      ScheduleV2ApplyFailureReason.REQUEST_ID_MISMATCH,
      "requestId does not match capture",
    )
    val states = LocalStates(categories, schedules, occurrenceOverrides)
    val clears = correlateResults(
      capture.categories,
      capture.schedules,
      capture.occurrenceOverrides,
      response.categories.upsertResults,
      response.categories.deleteResults,
      response.schedules.upsertResults,
      response.schedules.deleteResults,
      response.occurrenceOverrides.upsertResults,
      response.occurrenceOverrides.deleteResults,
    )
    applyResolved(states, collectMutationCandidates(response), clears)
  }

  /**
   * HTTP 400 没有任何可证明的逐项结论，所有 pending 必须原样保留。
   *
   * 保留该入口供 repository 统一处理结构错误，但它不再伪造 REJECTED 响应或清理本地数据。
   */
  fun discardUploaded(
    capture: ScheduleV2SyncCapture,
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2ApplyResult {
    check(capture.request.syncRequestId.isNotBlank())
    return ScheduleV2ApplyResult.Success(categories, schedules, occurrenceOverrides)
  }

  /** 将结构校验异常统一转换为可诊断的 Failure，调用方不会提交半套状态。 */
  private inline fun guardedApply(block: () -> ScheduleV2ApplyResult.Success): ScheduleV2ApplyResult = try {
    block()
  } catch (failure: ApplyAbort) {
    ScheduleV2ApplyResult.Failure(failure.reason, failure.message ?: "Schedule v2 apply failed")
  } catch (failure: IllegalArgumentException) {
    ScheduleV2ApplyResult.Failure(
      ScheduleV2ApplyFailureReason.INVALID_PAYLOAD,
      failure.message ?: "Schedule v2 response contains invalid payload",
    )
  }

  /** 校验 confirmedResults 的数量、identity 以及不同结果码必须携带的 canonical 数据。 */
  private fun validateConfirmed(capture: ScheduleV2SyncCapture, response: SyncResponse) {
    abortUnless(
      response.categories.confirmedResults.size == capture.request.categories.confirmed.size &&
        response.schedules.confirmedResults.size == capture.request.schedules.confirmed.size &&
        response.occurrenceOverrides.confirmedResults.size ==
        capture.request.occurrenceOverrides.confirmed.size,
      ScheduleV2ApplyFailureReason.RESPONSE_CORRELATION,
      "confirmed result counts do not match request",
    )
    response.categories.confirmedResults.forEachIndexed { index, result ->
      abortUnless(
        result.id == capture.request.categories.confirmed[index].id,
        ScheduleV2ApplyFailureReason.RESPONSE_CORRELATION,
        "Category confirmed result identity mismatch",
      )
      validateConfirmedPayload(result)
    }
    response.schedules.confirmedResults.forEachIndexed { index, result ->
      abortUnless(
        result.id == capture.request.schedules.confirmed[index].id,
        ScheduleV2ApplyFailureReason.RESPONSE_CORRELATION,
        "Schedule confirmed result identity mismatch",
      )
      validateConfirmedPayload(result)
    }
    response.occurrenceOverrides.confirmedResults.forEachIndexed { index, result ->
      val request = capture.request.occurrenceOverrides.confirmed[index]
      abortUnless(
        result.scheduleId == request.scheduleId && result.occurrenceDate == request.occurrenceDate,
        ScheduleV2ApplyFailureReason.RESPONSE_CORRELATION,
        "OccurrenceOverride confirmed result identity mismatch",
      )
      validateConfirmedPayload(result)
    }
  }

  private fun validateConfirmedPayload(result: CategoryConfirmedResult) {
    when (result.result) {
      ConfirmedResultCode.CONFIRMED -> requireNotNull(result.version)
      ConfirmedResultCode.CHANGED -> requireNotNull(result.current)
      ConfirmedResultCode.DELETED -> Unit
    }
  }

  private fun validateConfirmedPayload(result: ScheduleConfirmedResult) {
    when (result.result) {
      ConfirmedResultCode.CONFIRMED -> requireNotNull(result.version)
      ConfirmedResultCode.CHANGED -> requireNotNull(result.current)
      ConfirmedResultCode.DELETED -> Unit
    }
  }

  private fun validateConfirmedPayload(result: OccurrenceOverrideConfirmedResult) {
    when (result.result) {
      ConfirmedResultCode.CONFIRMED -> requireNotNull(result.version)
      ConfirmedResultCode.CHANGED -> requireNotNull(result.current)
      ConfirmedResultCode.DELETED -> Unit
    }
  }

  /** 关联六组逐项结果；只有非 REJECTED 结果才具备清除 capture pending 的确定结论。 */
  private fun correlateResults(
    categories: List<UploadedCategoryPending>,
    schedules: List<UploadedSchedulePending>,
    overrides: List<UploadedOccurrenceOverridePending>,
    categoryUpsertResults: List<CategoryUpsertResult>,
    categoryDeleteResults: List<CategoryDeleteResult>,
    scheduleUpsertResults: List<ScheduleUpsertResult>,
    scheduleDeleteResults: List<ScheduleDeleteResult>,
    overrideUpsertResults: List<OccurrenceOverrideUpsertResult>,
    overrideDeleteResults: List<OccurrenceOverrideDeleteResult>,
  ): PendingClears {
    val categoryUpserts = categories.filter { it.kind == UploadedPendingKind.UPSERT }
    val categoryDeletes = categories.filter { it.kind == UploadedPendingKind.DELETE }
    val scheduleUpserts = schedules.filter { it.kind == UploadedPendingKind.UPSERT }
    val scheduleDeletes = schedules.filter { it.kind == UploadedPendingKind.DELETE }
    val overrideUpserts = overrides.filter { it.kind == UploadedPendingKind.UPSERT }
    val overrideDeletes = overrides.filter { it.kind == UploadedPendingKind.DELETE }
    abortUnless(
      categoryUpserts.size == categoryUpsertResults.size &&
        categoryDeletes.size == categoryDeleteResults.size &&
        scheduleUpserts.size == scheduleUpsertResults.size &&
        scheduleDeletes.size == scheduleDeleteResults.size &&
        overrideUpserts.size == overrideUpsertResults.size &&
        overrideDeletes.size == overrideDeleteResults.size,
      ScheduleV2ApplyFailureReason.RESPONSE_CORRELATION,
      "mutation result counts do not match capture",
    )

    val categoryClears = linkedMapOf<CategoryIdentity, UploadedCategoryPending>()
    categoryUpsertResults.forEachIndexed { index, result ->
      val uploaded = categoryUpserts[index]
      requireIdentity(result.id == uploaded.identity.id, "Category upsert")
      if (result.result.canClearPending()) categoryClears[uploaded.identity] = uploaded
    }
    categoryDeleteResults.forEachIndexed { index, result ->
      val uploaded = categoryDeletes[index]
      requireIdentity(result.id == uploaded.identity.id, "Category delete")
      if (result.result.canClearPending()) categoryClears[uploaded.identity] = uploaded
    }

    val scheduleClears = linkedMapOf<ScheduleIdentity, UploadedSchedulePending>()
    scheduleUpsertResults.forEachIndexed { index, result ->
      val uploaded = scheduleUpserts[index]
      requireIdentity(result.id == uploaded.identity.id, "Schedule upsert")
      if (result.result.canClearPending()) scheduleClears[uploaded.identity] = uploaded
    }
    scheduleDeleteResults.forEachIndexed { index, result ->
      val uploaded = scheduleDeletes[index]
      requireIdentity(result.id == uploaded.identity.id, "Schedule delete")
      if (result.result.canClearPending()) scheduleClears[uploaded.identity] = uploaded
    }

    val overrideClears = linkedMapOf<OccurrenceOverrideIdentity, UploadedOccurrenceOverridePending>()
    overrideUpsertResults.forEachIndexed { index, result ->
      val uploaded = overrideUpserts[index]
      requireIdentity(
        result.scheduleId == uploaded.identity.scheduleId &&
          result.occurrenceDate == uploaded.identity.occurrenceDate,
        "OccurrenceOverride upsert",
      )
      if (result.result.canClearPending()) overrideClears[uploaded.identity] = uploaded
    }
    overrideDeleteResults.forEachIndexed { index, result ->
      val uploaded = overrideDeletes[index]
      requireIdentity(
        result.scheduleId == uploaded.identity.scheduleId &&
          result.occurrenceDate == uploaded.identity.occurrenceDate,
        "OccurrenceOverride delete",
      )
      if (result.result.canClearPending()) overrideClears[uploaded.identity] = uploaded
    }
    return PendingClears(categoryClears, scheduleClears, overrideClears)
  }

  private fun requireIdentity(value: Boolean, label: String) = abortUnless(
    value,
    ScheduleV2ApplyFailureReason.RESPONSE_CORRELATION,
    "$label result identity mismatch",
  )

  /** 收集同步响应中的所有 canonical live/tombstone 事实。 */
  private fun collectSyncCandidates(response: SyncResponse): RemoteCandidates {
    val candidates = RemoteCandidates()
    response.categories.confirmedResults.forEach { result ->
      when (result.result) {
        ConfirmedResultCode.CONFIRMED -> Unit
        ConfirmedResultCode.CHANGED -> candidates.add(requireNotNull(result.current).toDomain())
        ConfirmedResultCode.DELETED -> candidates.delete(CategoryIdentity(result.id))
      }
    }
    response.schedules.confirmedResults.forEach { result ->
      when (result.result) {
        ConfirmedResultCode.CONFIRMED -> Unit
        ConfirmedResultCode.CHANGED -> candidates.add(requireNotNull(result.current).toDomain())
        ConfirmedResultCode.DELETED -> candidates.delete(ScheduleIdentity(result.id))
      }
    }
    response.occurrenceOverrides.confirmedResults.forEach { result ->
      val identity = OccurrenceOverrideIdentity(result.scheduleId, result.occurrenceDate)
      when (result.result) {
        ConfirmedResultCode.CONFIRMED -> Unit
        ConfirmedResultCode.CHANGED -> candidates.add(requireNotNull(result.current).toDomain())
        ConfirmedResultCode.DELETED -> candidates.delete(identity)
      }
    }
    response.categories.discoveredResults.forEach { candidates.add(it.toDomain()) }
    response.schedules.discoveredResults.forEach { candidates.add(it.toDomain()) }
    response.occurrenceOverrides.discoveredResults.forEach { candidates.add(it.toDomain()) }
    collectMutationCandidates(
      candidates,
      response.categories.upsertResults,
      response.categories.deleteResults,
      response.schedules.upsertResults,
      response.schedules.deleteResults,
      response.occurrenceOverrides.upsertResults,
      response.occurrenceOverrides.deleteResults,
    )
    return candidates
  }

  /** 收集日常响应中的 canonical live/tombstone 事实。 */
  private fun collectMutationCandidates(response: MutationResponse): RemoteCandidates =
    RemoteCandidates().also { candidates ->
      collectMutationCandidates(
        candidates,
        response.categories.upsertResults,
        response.categories.deleteResults,
        response.schedules.upsertResults,
        response.schedules.deleteResults,
        response.occurrenceOverrides.upsertResults,
        response.occurrenceOverrides.deleteResults,
      )
    }

  private fun collectMutationCandidates(
    candidates: RemoteCandidates,
    categoryUpserts: List<CategoryUpsertResult>,
    categoryDeletes: List<CategoryDeleteResult>,
    scheduleUpserts: List<ScheduleUpsertResult>,
    scheduleDeletes: List<ScheduleDeleteResult>,
    overrideUpserts: List<OccurrenceOverrideUpsertResult>,
    overrideDeletes: List<OccurrenceOverrideDeleteResult>,
  ) {
    categoryUpserts.forEach {
      it.current?.let { value -> candidates.add(value.toDomain()) }
      it.tombstone?.let { value -> candidates.delete(CategoryIdentity(value.id)) }
    }
    categoryDeletes.forEach {
      it.current?.let { value -> candidates.add(value.toDomain()) }
      it.tombstone?.let { value -> candidates.delete(CategoryIdentity(value.id)) }
    }
    scheduleUpserts.forEach {
      it.current?.let { value -> candidates.add(value.toDomain()) }
      it.tombstone?.let { value -> candidates.delete(ScheduleIdentity(value.id)) }
    }
    scheduleDeletes.forEach {
      it.current?.let { value -> candidates.add(value.toDomain()) }
      it.tombstone?.let { value -> candidates.delete(ScheduleIdentity(value.id)) }
    }
    overrideUpserts.forEach {
      it.current?.let { value -> candidates.add(value.toDomain()) }
      it.tombstone?.let { value ->
        candidates.delete(OccurrenceOverrideIdentity(value.scheduleId, value.occurrenceDate))
      }
    }
    overrideDeletes.forEach {
      it.current?.let { value -> candidates.add(value.toDomain()) }
      it.tombstone?.let { value ->
        candidates.delete(OccurrenceOverrideIdentity(value.scheduleId, value.occurrenceDate))
      }
    }
  }

  /** 解析同 identity 的候选并原子地生成三类新状态。 */
  private fun applyResolved(
    states: LocalStates,
    candidates: RemoteCandidates,
    clears: PendingClears,
  ): ScheduleV2ApplyResult.Success {
    val categoryUpdates = resolveCategories(candidates, states.categories)
    val scheduleUpdates = resolveSchedules(candidates, states.schedules)
    val overrideUpdates = resolveOverrides(candidates, states.overrides)
    return try {
      ScheduleV2ApplyResult.Success(
        categories = (states.categories.keys + categoryUpdates.keys).mapNotNull { identity ->
          val old = states.categories[identity]
          val remote = if (categoryUpdates.containsKey(identity)) categoryUpdates[identity]
          else old?.remoteSnapshot
          val pending = pendingAfterRemoteUpdate(
            old?.pending,
            categoryUpdates.containsKey(identity) && remote == null,
            old.matches(clears.categories[identity]),
          )
          if (remote == null && pending == null) null else CategorySyncState(identity, remote, pending)
        },
        schedules = (states.schedules.keys + scheduleUpdates.keys).mapNotNull { identity ->
          val old = states.schedules[identity]
          val remote = if (scheduleUpdates.containsKey(identity)) scheduleUpdates[identity]
          else old?.remoteSnapshot
          val pending = pendingAfterRemoteUpdate(
            old?.pending,
            scheduleUpdates.containsKey(identity) && remote == null,
            old.matches(clears.schedules[identity]),
          )
          if (remote == null && pending == null) null else ScheduleSyncState(identity, remote, pending)
        },
        occurrenceOverrides = (states.overrides.keys + overrideUpdates.keys).mapNotNull { identity ->
          val old = states.overrides[identity]
          val remote = if (overrideUpdates.containsKey(identity)) overrideUpdates[identity]
          else old?.remoteSnapshot
          val pending = pendingAfterRemoteUpdate(
            old?.pending,
            overrideUpdates.containsKey(identity) && remote == null,
            old.matches(clears.overrides[identity]),
          )
          if (remote == null && pending == null) null
          else OccurrenceOverrideSyncState(identity, remote, pending)
        },
      )
    } catch (failure: IllegalArgumentException) {
      throw ApplyAbort(
        ScheduleV2ApplyFailureReason.INVALID_LOCAL_STATE,
        failure.message ?: "response would create an invalid local state",
      )
    }
  }

  private fun resolveCategories(
    candidates: RemoteCandidates,
    states: Map<CategoryIdentity, CategorySyncState>,
  ): Map<CategoryIdentity, CategoryRemoteSnapshot?> = buildMap {
    (candidates.categoryLive.keys + candidates.categoryDeleted).forEach { identity ->
      put(identity, if (identity in candidates.categoryDeleted) null else {
        selectRemote(identity.id, candidates.categoryLive.getValue(identity), states[identity]?.remoteSnapshot)
      })
    }
  }

  private fun resolveSchedules(
    candidates: RemoteCandidates,
    states: Map<ScheduleIdentity, ScheduleSyncState>,
  ): Map<ScheduleIdentity, ScheduleRemoteSnapshot?> = buildMap {
    (candidates.scheduleLive.keys + candidates.scheduleDeleted).forEach { identity ->
      put(identity, if (identity in candidates.scheduleDeleted) null else {
        selectRemote(identity.id, candidates.scheduleLive.getValue(identity), states[identity]?.remoteSnapshot)
      })
    }
  }

  private fun resolveOverrides(
    candidates: RemoteCandidates,
    states: Map<OccurrenceOverrideIdentity, OccurrenceOverrideSyncState>,
  ): Map<OccurrenceOverrideIdentity, OccurrenceOverrideRemoteSnapshot?> = buildMap {
    (candidates.overrideLive.keys + candidates.overrideDeleted).forEach { identity ->
      put(identity, if (identity in candidates.overrideDeleted) null else {
        selectRemote(
          "${identity.scheduleId}@${identity.occurrenceDate}",
          candidates.overrideLive.getValue(identity),
          states[identity]?.remoteSnapshot,
        )
      })
    }
  }

  /** 选择最高版本 canonical，并拒绝同版本不同内容或版本倒退。 */
  private fun <T : RemoteSnapshot<*, *>> selectRemote(label: String, values: List<T>, existing: T?): T {
    val selected = values.maxBy { it.version }
    abortUnless(
      values.filter { it.version == selected.version }.all { it == selected },
      ScheduleV2ApplyFailureReason.SAME_VERSION_CONFLICT,
      "$label has different complete payloads at the same highest version",
    )
    abortUnless(
      existing == null || selected.version >= existing.version,
      ScheduleV2ApplyFailureReason.REMOTE_VERSION_REGRESSION,
      "$label remote version regressed from ${existing?.version} to ${selected.version}",
    )
    abortUnless(
      existing == null || existing.version != selected.version || existing == selected,
      ScheduleV2ApplyFailureReason.SAME_VERSION_CONFLICT,
      "$label conflicts with the existing payload at version ${selected.version}",
    )
    return selected
  }
}

/** 当前本地三类状态的去重索引。 */
private class LocalStates(
  categories: List<CategorySyncState>,
  schedules: List<ScheduleSyncState>,
  occurrenceOverrides: List<OccurrenceOverrideSyncState>,
) {
  val categories = uniqueStates(categories.map { it.identity to it }, "Category")
  val schedules = uniqueStates(schedules.map { it.identity to it }, "Schedule")
  val overrides = uniqueStates(occurrenceOverrides.map { it.identity to it }, "OccurrenceOverride")

  private fun <K, V> uniqueStates(values: List<Pair<K, V>>, type: String): Map<K, V> {
    val result = linkedMapOf<K, V>()
    values.forEach { (identity, value) ->
      abortUnless(
        result.put(identity, value) == null,
        ScheduleV2ApplyFailureReason.INVALID_LOCAL_STATE,
        "$type states contain duplicate identities",
      )
    }
    return result
  }
}

/** 响应确认成功且 capture revision 仍是当前 pending 时才清理 pending。 */
private fun <I : ResourceIdentity, R : SyncResource<I>> pendingAfterRemoteUpdate(
  pending: PendingChange<I, R>?,
  remoteDeleted: Boolean,
  uploadedMatches: Boolean,
): PendingChange<I, R>? = when {
  remoteDeleted -> null
  uploadedMatches -> null
  else -> pending
}

private fun MutationResultCode.canClearPending(): Boolean = this != MutationResultCode.REJECTED

private fun CategorySyncState?.matches(uploaded: UploadedCategoryPending?): Boolean {
  val current = this?.pending ?: return false
  uploaded ?: return false
  return current.localRevision == uploaded.localRevision && current.matches(uploaded.kind)
}

private fun ScheduleSyncState?.matches(uploaded: UploadedSchedulePending?): Boolean {
  val current = this?.pending ?: return false
  uploaded ?: return false
  return current.localRevision == uploaded.localRevision && current.matches(uploaded.kind)
}

private fun OccurrenceOverrideSyncState?.matches(uploaded: UploadedOccurrenceOverridePending?): Boolean {
  val current = this?.pending ?: return false
  uploaded ?: return false
  return current.localRevision == uploaded.localRevision && current.matches(uploaded.kind)
}

private fun PendingChange<*, *>.matches(kind: UploadedPendingKind): Boolean =
  (kind == UploadedPendingKind.UPSERT && this is PendingUpsert) ||
    (kind == UploadedPendingKind.DELETE && this is PendingDelete)

private class RemoteCandidates {
  val categoryLive = linkedMapOf<CategoryIdentity, MutableList<CategoryRemoteSnapshot>>()
  val categoryDeleted = linkedSetOf<CategoryIdentity>()
  val scheduleLive = linkedMapOf<ScheduleIdentity, MutableList<ScheduleRemoteSnapshot>>()
  val scheduleDeleted = linkedSetOf<ScheduleIdentity>()
  val overrideLive = linkedMapOf<OccurrenceOverrideIdentity, MutableList<OccurrenceOverrideRemoteSnapshot>>()
  val overrideDeleted = linkedSetOf<OccurrenceOverrideIdentity>()

  fun add(value: CategoryRemoteSnapshot) {
    categoryLive.getOrPut(value.identity) { mutableListOf() } += value
  }

  fun add(value: ScheduleRemoteSnapshot) {
    scheduleLive.getOrPut(value.identity) { mutableListOf() } += value
  }

  fun add(value: OccurrenceOverrideRemoteSnapshot) {
    overrideLive.getOrPut(value.identity) { mutableListOf() } += value
  }

  fun delete(identity: CategoryIdentity) {
    categoryDeleted += identity
  }

  fun delete(identity: ScheduleIdentity) {
    scheduleDeleted += identity
  }

  fun delete(identity: OccurrenceOverrideIdentity) {
    overrideDeleted += identity
  }
}

private data class PendingClears(
  val categories: Map<CategoryIdentity, UploadedCategoryPending>,
  val schedules: Map<ScheduleIdentity, UploadedSchedulePending>,
  val overrides: Map<OccurrenceOverrideIdentity, UploadedOccurrenceOverridePending>,
)

private class ApplyAbort(
  val reason: ScheduleV2ApplyFailureReason,
  message: String,
) : IllegalStateException(message)

private fun abortUnless(
  condition: Boolean,
  reason: ScheduleV2ApplyFailureReason,
  message: String,
) {
  if (!condition) throw ApplyAbort(reason, message)
}
