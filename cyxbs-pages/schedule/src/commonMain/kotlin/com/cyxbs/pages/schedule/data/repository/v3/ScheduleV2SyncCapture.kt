package com.cyxbs.pages.schedule.data.repository.v3

import com.cyxbs.pages.schedule.data.remote.v3.CategoryDelete
import com.cyxbs.pages.schedule.data.remote.v3.CategoryInput
import com.cyxbs.pages.schedule.data.remote.v3.CategoryMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.CategorySyncRequest
import com.cyxbs.pages.schedule.data.remote.v3.ConfirmedCategory
import com.cyxbs.pages.schedule.data.remote.v3.ConfirmedOccurrenceOverride
import com.cyxbs.pages.schedule.data.remote.v3.ConfirmedSchedule
import com.cyxbs.pages.schedule.data.remote.v3.MutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideDelete
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideInput
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideSyncRequest
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleDelete
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleInput
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleSyncRequest
import com.cyxbs.pages.schedule.data.remote.v3.SyncRequest
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryResource
import com.cyxbs.pages.schedule.domain.sync.v2.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideResource
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.PendingDelete
import com.cyxbs.pages.schedule.domain.sync.v2.PendingUpsert
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleResource
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleSyncState

/** capture 中记录的 pending 分支，仅用于把响应关联回发出时的本地 revision。 */
enum class UploadedPendingKind {
  UPSERT,
  DELETE,
}

/** Category pending 的请求关联信息。 */
data class UploadedCategoryPending(
  val identity: CategoryIdentity,
  val localRevision: Long,
  val kind: UploadedPendingKind,
)

/** Schedule pending 的请求关联信息。 */
data class UploadedSchedulePending(
  val identity: ScheduleIdentity,
  val localRevision: Long,
  val kind: UploadedPendingKind,
)

/** OccurrenceOverride pending 的请求关联信息。 */
data class UploadedOccurrenceOverridePending(
  val identity: OccurrenceOverrideIdentity,
  val localRevision: Long,
  val kind: UploadedPendingKind,
)

/** 一次不可变同步请求及其 compare-and-clear 上下文。 */
data class ScheduleV2SyncCapture(
  val request: SyncRequest,
  val categories: List<UploadedCategoryPending>,
  val schedules: List<UploadedSchedulePending>,
  val occurrenceOverrides: List<UploadedOccurrenceOverridePending>,
)

/** 一次不可变日常请求及其 compare-and-clear 上下文。 */
data class ScheduleV2MutationCapture(
  val request: MutationRequest,
  val categories: List<UploadedCategoryPending>,
  val schedules: List<UploadedSchedulePending>,
  val occurrenceOverrides: List<UploadedOccurrenceOverridePending>,
)

/**
 * 从当前双快照状态捕获同步或日常请求。
 *
 * 服务端按资源逐项处理，因此客户端不再保存批次分组。capture 只冻结本次实际上传的列表与 localRevision，
 * 响应回来时若同一资源已产生更新 revision，则保留较新的本地 pending 等待下一次同步。
 */
class ScheduleV2RequestPlanner {

  /** 捕获完整 inventory 与当前全部 pending。 */
  fun capture(
    syncRequestId: String,
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2SyncCapture {
    require(syncRequestId.isNotBlank()) { "syncRequestId must not be blank" }
    requireUniqueIdentities(categories.map { it.identity }, "Category")
    requireUniqueIdentities(schedules.map { it.identity }, "Schedule")
    requireUniqueIdentities(occurrenceOverrides.map { it.identity }, "OccurrenceOverride")

    val pending = capturePending(categories, schedules, occurrenceOverrides)
    return ScheduleV2SyncCapture(
      request = SyncRequest(
        syncRequestId = syncRequestId,
        categories = CategorySyncRequest(
          confirmed = categories.mapNotNull { state ->
            state.remoteSnapshot?.let { ConfirmedCategory(state.identity.id, it.version.toULong()) }
          },
          upserts = pending.categoryUpserts,
          deletes = pending.categoryDeletes,
        ),
        schedules = ScheduleSyncRequest(
          confirmed = schedules.mapNotNull { state ->
            state.remoteSnapshot?.let { ConfirmedSchedule(state.identity.id, it.version.toULong()) }
          },
          upserts = pending.scheduleUpserts,
          deletes = pending.scheduleDeletes,
        ),
        occurrenceOverrides = OccurrenceOverrideSyncRequest(
          confirmed = occurrenceOverrides.mapNotNull { state ->
            state.remoteVersion()?.let { version ->
              ConfirmedOccurrenceOverride(
                state.identity.scheduleId,
                state.identity.occurrenceDate,
                version.toULong(),
              )
            }
          },
          upserts = pending.overrideUpserts,
          deletes = pending.overrideDeletes,
        ),
      ),
      categories = pending.categories,
      schedules = pending.schedules,
      occurrenceOverrides = pending.overrides,
    )
  }

  /**
   * 捕获一次本地命令产生的 pending。
   *
   * 调用方应传入同一 localRevision 的状态；请求允许同时携带分类、日程和子日程，但每个资源独立返回结果。
   */
  fun captureMutation(
    requestId: String,
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2MutationCapture {
    require(requestId.isNotBlank()) { "requestId must not be blank" }
    require(categories.isNotEmpty() || schedules.isNotEmpty() || occurrenceOverrides.isNotEmpty()) {
      "daily mutation capture requires pending resources"
    }
    require(
      categories.all { it.pending != null } &&
          schedules.all { it.pending != null } &&
          occurrenceOverrides.all { it.pending != null },
    ) { "daily mutation capture only accepts states with pending" }

    val pending = capturePending(categories, schedules, occurrenceOverrides)
    return ScheduleV2MutationCapture(
      request = MutationRequest(
        requestId = requestId,
        categories = CategoryMutationRequest(pending.categoryUpserts, pending.categoryDeletes),
        schedules = ScheduleMutationRequest(pending.scheduleUpserts, pending.scheduleDeletes),
        occurrenceOverrides = OccurrenceOverrideMutationRequest(
          pending.overrideUpserts,
          pending.overrideDeletes,
        ),
      ),
      categories = pending.categories,
      schedules = pending.schedules,
      occurrenceOverrides = pending.overrides,
    )
  }

  /** 将三类 pending 依次投影为 wire 列表，并保持 capture 与请求分支的相对顺序。 */
  private fun capturePending(
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): PendingProjection {
    val projection = PendingProjection()
    categories.forEach { state ->
      when (val pending = state.pending) {
        is PendingUpsert -> {
          projection.categoryUpserts += state.projectUpsert(pending)
          projection.categories += UploadedCategoryPending(
            state.identity,
            pending.localRevision,
            UploadedPendingKind.UPSERT,
          )
        }

        is PendingDelete -> {
          projection.categoryDeletes += CategoryDelete(state.identity.id, pending.localModifiedAt)
          projection.categories += UploadedCategoryPending(
            state.identity,
            pending.localRevision,
            UploadedPendingKind.DELETE,
          )
        }

        null -> Unit
      }
    }
    schedules.forEach { state ->
      when (val pending = state.pending) {
        is PendingUpsert -> {
          projection.scheduleUpserts += state.projectUpsert(pending)
          projection.schedules += UploadedSchedulePending(
            state.identity,
            pending.localRevision,
            UploadedPendingKind.UPSERT,
          )
        }

        is PendingDelete -> {
          projection.scheduleDeletes += ScheduleDelete(state.identity.id, pending.localModifiedAt)
          projection.schedules += UploadedSchedulePending(
            state.identity,
            pending.localRevision,
            UploadedPendingKind.DELETE,
          )
        }

        null -> Unit
      }
    }
    occurrenceOverrides.forEach { state ->
      when (val pending = state.pending) {
        is PendingUpsert -> {
          projection.overrideUpserts += state.projectUpsert(pending)
          projection.overrides += UploadedOccurrenceOverridePending(
            state.identity,
            pending.localRevision,
            UploadedPendingKind.UPSERT,
          )
        }

        is PendingDelete -> {
          projection.overrideDeletes += OccurrenceOverrideDelete(
            state.identity.scheduleId,
            state.identity.occurrenceDate,
            requireNotNull(state.remoteSnapshot) {
              "OccurrenceOverride DELETE requires a live remote snapshot"
            }.version.toULong(),
            pending.localModifiedAt,
          )
          projection.overrides += UploadedOccurrenceOverridePending(
            state.identity,
            pending.localRevision,
            UploadedPendingKind.DELETE,
          )
        }

        null -> Unit
      }
    }
    return projection
  }

  private fun <T> requireUniqueIdentities(identities: List<T>, type: String) {
    require(identities.size == identities.toSet().size) { "$type states contain duplicate identities" }
  }

  /** 请求投影始终使用当前 remote version，CREATE R→U 时不改写 version=0 的本地 U。 */
  private fun CategorySyncState.projectUpsert(
    pending: PendingUpsert<CategoryIdentity, CategoryResource>,
  ): CategoryInput = pending.resource.toWire().let { wire ->
    remoteSnapshot?.let { wire.copy(version = it.version.toULong()) } ?: wire
  }

  /** Schedule 仅在 wire 投影当前 remote version，业务字段与 localRevision 保持 pending 原值。 */
  private fun ScheduleSyncState.projectUpsert(
    pending: PendingUpsert<ScheduleIdentity, ScheduleResource>,
  ): ScheduleInput = pending.resource.toWire().let { wire ->
    remoteSnapshot?.let { wire.copy(version = it.version.toULong()) } ?: wire
  }

  /** OccurrenceOverride 从 live/tombstone 统一版本序列投影，不在本地状态上执行 rebase。 */
  private fun OccurrenceOverrideSyncState.projectUpsert(
    pending: PendingUpsert<OccurrenceOverrideIdentity, OccurrenceOverrideResource>,
  ): OccurrenceOverrideInput = pending.resource.toWire().let { wire ->
    remoteVersion()?.let { wire.copy(version = it.toULong()) } ?: wire
  }
}

/** capturePending 的可变构建结果，仅在单次函数调用内存在。 */
private class PendingProjection {
  val categoryUpserts = mutableListOf<CategoryInput>()
  val categoryDeletes = mutableListOf<CategoryDelete>()
  val scheduleUpserts = mutableListOf<ScheduleInput>()
  val scheduleDeletes = mutableListOf<ScheduleDelete>()
  val overrideUpserts = mutableListOf<OccurrenceOverrideInput>()
  val overrideDeletes = mutableListOf<OccurrenceOverrideDelete>()
  val categories = mutableListOf<UploadedCategoryPending>()
  val schedules = mutableListOf<UploadedSchedulePending>()
  val overrides = mutableListOf<UploadedOccurrenceOverridePending>()
}
