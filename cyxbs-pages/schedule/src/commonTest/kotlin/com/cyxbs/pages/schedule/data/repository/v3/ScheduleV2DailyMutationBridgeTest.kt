package com.cyxbs.pages.schedule.data.repository.v3

import com.cyxbs.pages.schedule.data.remote.v3.CategoryMutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.MutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.MutationResultCode
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideMutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.ResultReason
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleMutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleUpsertResult
import com.cyxbs.pages.schedule.domain.sync.v2.AtomicField
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryResource
import com.cyxbs.pages.schedule.domain.sync.v2.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.v2.PendingDelete
import com.cyxbs.pages.schedule.domain.sync.v2.PendingUpsert
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleKind
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleResource
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.ServerResourceMeta
import com.cyxbs.pages.schedule.domain.sync.v2.TimingInput
import com.cyxbs.pages.schedule.domain.sync.v2.TimingKind
import com.cyxbs.pages.schedule.domain.sync.v2.TodoState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/** 日常请求只验证一次本地命令产生的逐资源变更，不再保留原子批次概念。 */
class ScheduleV2DailyMutationBridgeTest {
  private val bridge = ScheduleV2DailyMutationBridge()

  @Test
  fun captureOnlyIncludesCurrentRevisionAndUsesCreateRoute() {
    val category = categoryState(revision = 1)
    val schedule = scheduleState(version = 0, revision = 2)

    val captured = assertIs<ScheduleV2DailyMutationCapture.Ready>(
      bridge.capture("daily-create", 2, listOf(category), listOf(schedule), emptyList()),
    )

    assertEquals(ScheduleV2DailyMutationMethod.CREATE, captured.method)
    assertEquals("daily-create", captured.request.requestId)
    assertEquals(emptyList(), captured.request.categories.upserts)
    assertEquals(listOf(SCHEDULE_ID), captured.request.schedules.upserts.map { it.id })
  }

  @Test
  fun sameRevisionCategoryAndScheduleAreUploadedTogether() {
    val category = categoryState(revision = 7)
    val remote = scheduleResource(version = 3, title = "remote")
    val schedule = ScheduleSyncState(
      identity = remote.identity,
      remoteSnapshot = ScheduleRemoteSnapshot(remote, ServerResourceMeta(1, 2)),
      pending = PendingUpsert(remote.copy(title = AtomicField("updated", 7)), localRevision = 7),
    )

    val captured = assertIs<ScheduleV2DailyMutationCapture.Ready>(
      bridge.capture("daily-update", 7, listOf(category), listOf(schedule), emptyList()),
    )

    assertEquals(ScheduleV2DailyMutationMethod.UPDATE, captured.method)
    assertEquals(listOf(CATEGORY_ID), captured.request.categories.upserts.map { it.id })
    assertEquals(listOf(SCHEDULE_ID), captured.request.schedules.upserts.map { it.id })
  }

  @Test
  fun rejectedResourceKeepsPending() {
    val schedule = scheduleState(version = 0, revision = 2)
    val captured = assertIs<ScheduleV2DailyMutationCapture.Ready>(
      bridge.capture("daily-rejected", 2, emptyList(), listOf(schedule), emptyList()),
    )
    val response = emptyResponse(
      captured.request.requestId,
      scheduleUpserts = listOf(
        ScheduleUpsertResult(
          id = SCHEDULE_ID,
          result = MutationResultCode.REJECTED,
          reason = ResultReason.INVALID_REQUEST,
          info = "标题不能为空",
        ),
      ),
    )

    val applied = assertIs<ScheduleV2ApplyResult.Success>(
      bridge.apply(captured, response, emptyList(), listOf(schedule), emptyList()),
    )

    assertEquals(2, assertNotNull(applied.schedules.single().pending).localRevision)
  }

  @Test
  fun parentDeleteUsesDeleteRoute() {
    val remote = scheduleResource(version = 7)
    val schedule = ScheduleSyncState(
      remote.identity,
      ScheduleRemoteSnapshot(remote, ServerResourceMeta(1, 2)),
      PendingDelete(remote.identity, localModifiedAt = 10, localRevision = 6),
    )

    val captured = assertIs<ScheduleV2DailyMutationCapture.Ready>(
      bridge.capture("daily-delete", 6, emptyList(), listOf(schedule), emptyList()),
    )

    assertEquals(ScheduleV2DailyMutationMethod.DELETE, captured.method)
    assertEquals(listOf(SCHEDULE_ID), captured.request.schedules.deletes.map { it.id })
  }

  private fun emptyResponse(
    requestId: String,
    scheduleUpserts: List<ScheduleUpsertResult> = emptyList(),
  ) = MutationResponse(
    requestId = requestId,
    categories = CategoryMutationResponse(emptyList(), emptyList()),
    schedules = ScheduleMutationResponse(scheduleUpserts, emptyList()),
    occurrenceOverrides = OccurrenceOverrideMutationResponse(emptyList(), emptyList()),
  )

  private fun categoryState(revision: Long): CategorySyncState {
    val resource = CategoryResource(
      CategoryIdentity(CATEGORY_ID),
      version = 0,
      name = AtomicField("分类", 1),
      color = AtomicField(null, 1),
      sortOrder = AtomicField(0, 1),
    )
    return CategorySyncState(resource.identity, null, PendingUpsert(resource, revision))
  }

  private fun scheduleState(version: Long, revision: Long): ScheduleSyncState {
    val resource = scheduleResource(version)
    return ScheduleSyncState(resource.identity, null, PendingUpsert(resource, revision))
  }

  private fun scheduleResource(version: Long, title: String = "日程"): ScheduleResource = ScheduleResource(
    identity = ScheduleIdentity(SCHEDULE_ID),
    version = version,
    kind = ScheduleKind.TODO,
    title = AtomicField(title, 1),
    description = AtomicField("", 1),
    categoryId = AtomicField(CATEGORY_ID, 1),
    timing = AtomicField(TimingInput(TimingKind.UNSCHEDULED), 1),
    recurrence = AtomicField(null, 1),
    reminder = AtomicField(null, 1),
    todoState = AtomicField(TodoState.OPEN, 1),
    linkedToCourse = AtomicField(false, 1),
  )

  private companion object {
    const val CATEGORY_ID = "0197f000-0000-7000-8000-000000000010"
    const val SCHEDULE_ID = "0197f000-0000-7000-8000-000000000001"
  }
}
