package com.cyxbs.pages.schedule.data.repository.v3

import com.cyxbs.pages.schedule.data.remote.v3.MutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.MutationResponse
import com.cyxbs.pages.schedule.domain.sync.v2.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.PendingDelete
import com.cyxbs.pages.schedule.domain.sync.v2.PendingUpsert
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleSyncState

/** 日常请求使用的 HTTP 方法；具体资源仍由 version 和操作分支区分增删改。 */
enum class ScheduleV2DailyMutationMethod {
  CREATE,
  UPDATE,
  DELETE,
}

/** 一次本地命令对应的不可变日常请求 capture。 */
sealed interface ScheduleV2DailyMutationCapture {
  /** 已捕获同一 localRevision 下的全部资源变更，可以立即发起请求。 */
  data class Ready(
    val method: ScheduleV2DailyMutationMethod,
    val request: MutationRequest,
    val capture: ScheduleV2MutationCapture,
  ) : ScheduleV2DailyMutationCapture

  /** reducer 没有产生 pending，无法形成非空请求。 */
  data class Failure(val message: String) : ScheduleV2DailyMutationCapture
}

/**
 * 把一次本地命令产生的 pending 收敛为一个日常请求。
 *
 * 同一命令创建或修改的 Category、Schedule、OccurrenceOverride 共用 localRevision，因此会一起上传；服务端对
 * 每个资源独立返回成功或拒绝，失败项继续保留在本地，成功项按 localRevision compare-and-clear。
 */
class ScheduleV2DailyMutationBridge(
  private val planner: ScheduleV2RequestPlanner = ScheduleV2RequestPlanner(),
  private val applier: ScheduleV2ResponseApplier = ScheduleV2ResponseApplier(),
) {

  /** 只捕获本次 localRevision，避免把旧的无关失败顺带绑定到当前请求。 */
  fun capture(
    requestId: String,
    localRevision: Long,
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2DailyMutationCapture {
    val selectedCategories = categories.filter { it.pending?.localRevision == localRevision }
    val selectedSchedules = schedules.filter { it.pending?.localRevision == localRevision }
    val selectedOverrides = occurrenceOverrides.filter { it.pending?.localRevision == localRevision }
    if (selectedCategories.isEmpty() && selectedSchedules.isEmpty() && selectedOverrides.isEmpty()) {
      return ScheduleV2DailyMutationCapture.Failure("local command produced no pending mutation")
    }
    val capture = planner.captureMutation(
      requestId = requestId,
      categories = selectedCategories,
      schedules = selectedSchedules,
      occurrenceOverrides = selectedOverrides,
    )
    return ScheduleV2DailyMutationCapture.Ready(
      method = selectMethod(selectedCategories, selectedSchedules, selectedOverrides),
      request = capture.request,
      capture = capture,
    )
  }

  /** 应用日常逐资源结果，并保留请求期间形成的较新本地修改。 */
  fun apply(
    captured: ScheduleV2DailyMutationCapture.Ready,
    result: MutationResponse,
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2ApplyResult = applier.applyMutation(
    capture = captured.capture,
    response = result,
    categories = categories,
    schedules = schedules,
    occurrenceOverrides = occurrenceOverrides,
  )

  /** 优先由本次主 Schedule 的 pending 类型选择路由，没有 Schedule 时再检查其余资源。 */
  private fun selectMethod(
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2DailyMutationMethod {
    if (schedules.any { it.pending is PendingDelete }) return ScheduleV2DailyMutationMethod.DELETE
    if (schedules.isNotEmpty()) {
      val createsSchedule = schedules.any { state ->
        state.remoteSnapshot == null &&
          (state.pending as? PendingUpsert<*, *>)?.resource?.version == 0L
      }
      return if (createsSchedule) ScheduleV2DailyMutationMethod.CREATE
      else ScheduleV2DailyMutationMethod.UPDATE
    }
    val pending = categories.mapNotNull { it.pending } + occurrenceOverrides.mapNotNull { it.pending }
    if (pending.all { it is PendingDelete }) return ScheduleV2DailyMutationMethod.DELETE
    if (pending.any { it is PendingUpsert<*, *> && it.resource.version == 0L }) {
      return ScheduleV2DailyMutationMethod.CREATE
    }
    return ScheduleV2DailyMutationMethod.UPDATE
  }
}
