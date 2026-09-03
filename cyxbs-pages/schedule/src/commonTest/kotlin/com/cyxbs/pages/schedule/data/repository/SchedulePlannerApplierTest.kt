package com.cyxbs.pages.schedule.data.repository

import com.cyxbs.pages.schedule.data.remote.CategoryInput
import com.cyxbs.pages.schedule.data.remote.CategorySyncResponse
import com.cyxbs.pages.schedule.data.remote.ConfirmedResult
import com.cyxbs.pages.schedule.data.remote.ConfirmedResultCode
import com.cyxbs.pages.schedule.data.remote.DeleteResult
import com.cyxbs.pages.schedule.data.remote.MutationResultCode
import com.cyxbs.pages.schedule.data.remote.OccurrenceAdjustmentInput
import com.cyxbs.pages.schedule.data.remote.OccurrenceAdjustmentSyncResponse
import com.cyxbs.pages.schedule.data.remote.ScheduleSyncResponse
import com.cyxbs.pages.schedule.data.remote.SyncResponse
import com.cyxbs.pages.schedule.data.remote.UpsertResult
import com.cyxbs.pages.schedule.domain.sync.AtomicField
import com.cyxbs.pages.schedule.domain.sync.CategoryIdentity
import com.cyxbs.pages.schedule.domain.sync.CategoryRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.PendingDelete
import com.cyxbs.pages.schedule.domain.sync.PendingUpsert
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 最终逐资源同步合同的 planner/applier 回归测试。 */
class SchedulePlannerApplierTest {
  private val planner = ScheduleRequestPlanner()
  private val applier = ScheduleResponseApplier()

  /** 同步请求同时携带远端 inventory 和本地 pending，但不携带 requestId。 */
  @Test
  fun captureContainsConfirmedAndPendingResources() {
    val remote = testCategoryResource(remoteId = 41L, version = 3)
    val state = CategorySyncState(
      identity = remote.identity,
      remoteSnapshot = CategoryRemoteSnapshot(remote),
      pending = PendingUpsert(remote.copy(name = AtomicField("本地修改", 20)), 4),
    )

    val capture = planner.capture(listOf(state), emptyList(), emptyList())

    assertEquals(listOf(41L), capture.request.categories.confirmed.map { it.id })
    assertEquals(listOf(41L), capture.request.categories.upserts.map { it.id })
    assertEquals(listOf(3uL), capture.request.categories.upserts.map { it.version })
  }

  /** 同请求新建分类和日程时，通过瞬时 categoryLocalId 建立引用。 */
  @Test
  fun newScheduleReferencesNewCategoryByTransientLocalId() {
    val category = testCategoryResource()
    val schedule = testScheduleResource(categoryLocalId = category.identity.id)
    val capture = planner.captureMutation(
      categories = listOf(testCategoryState(category, PendingUpsert(category, 1))),
      schedules = listOf(testScheduleState(schedule, PendingUpsert(schedule, 1), hasRemote = false)),
      occurrenceAdjustments = emptyList(),
    )

    assertEquals(category.identity.id, capture.request.categories.upserts.single().localId)
    assertEquals(category.identity.id, capture.request.schedules.upserts.single().categoryLocalId)
    assertNull(capture.request.schedules.upserts.single().categoryId.data)
  }

  /** 部分成功只清理成功项，拒绝项继续保留等待用户修正。 */
  @Test
  fun partialSuccessClearsOnlyAcceptedPending() = runTest {
    val firstRemote = testCategoryResource(remoteId = 41L, version = 3)
    val secondRemote = firstRemote.copy(
      identity = CategoryIdentity("019d0000-0000-7000-8000-000000000011"),
      remoteId = 42L,
    )
    val first = CategorySyncState(
      firstRemote.identity,
      CategoryRemoteSnapshot(firstRemote),
      PendingUpsert(firstRemote.copy(name = AtomicField("甲", 20)), 4),
    )
    val second = CategorySyncState(
      secondRemote.identity,
      CategoryRemoteSnapshot(secondRemote),
      PendingUpsert(secondRemote.copy(name = AtomicField("乙", 20)), 4),
    )
    val capture = planner.capture(listOf(first, second), emptyList(), emptyList())
    val accepted = capture.request.categories.upserts[0].copy(
      localId = null,
      id = 41L,
      version = 4uL,
    )
    val response = response(
      capture,
      categoryUpserts = listOf(
        UpsertResult(MutationResultCode.SUCCESS, resource = accepted),
        UpsertResult(MutationResultCode.REJECTED),
      ),
    )

    val result = assertIs<ScheduleApplyResult.Success>(
      applier.apply(capture, response, listOf(first, second), emptyList(), emptyList()),
    )

    assertNull(result.categories.single { it.identity == first.identity }.pending)
    assertEquals(4, result.categories.single { it.identity == second.identity }.pending?.localRevision)
  }

  /** 请求期间产生的更高 localRevision 不能被旧成功响应清掉。 */
  @Test
  fun acceptedRequestDoesNotClearNewerLocalChange() = runTest {
    val remote = testCategoryResource(remoteId = 41L, version = 3)
    val sent = CategorySyncState(
      remote.identity,
      CategoryRemoteSnapshot(remote),
      PendingUpsert(remote.copy(name = AtomicField("R", 20)), 4),
    )
    val capture = planner.capture(listOf(sent), emptyList(), emptyList())
    val newer = sent.replacePending(PendingUpsert(remote.copy(name = AtomicField("U", 30)), 5))
    val canonical = capture.request.categories.upserts.single().copy(
      localId = null,
      id = 41L,
      version = 4uL,
    )

    val result = assertIs<ScheduleApplyResult.Success>(
      applier.apply(
        capture,
        response(
          capture,
          categoryUpserts = listOf(UpsertResult(MutationResultCode.SUCCESS, resource = canonical)),
        ),
        listOf(newer),
        emptyList(),
        emptyList(),
      ),
    ).categories.single()

    assertEquals(4, result.remoteSnapshot?.version)
    assertEquals(5, result.pending?.localRevision)
    assertEquals("U", result.effectiveResource()?.name?.data)
  }

  /** 物理删除成功或远端已不存在都移除本地资源。 */
  @Test
  fun physicalDeleteRemovesLocalState() = runTest {
    val remote = testAdjustmentResource(remoteId = 71L, version = 5)
    val state = testAdjustmentState(
      remote,
      PendingDelete(remote.identity, localModifiedAt = 100, localRevision = 6),
    )
    val capture = planner.capture(emptyList(), emptyList(), listOf(state))
    val response = response(
      capture,
      adjustmentDeletes = listOf(DeleteResult(71L, MutationResultCode.SUCCESS)),
    )

    val result = assertIs<ScheduleApplyResult.Success>(
      applier.apply(capture, response, emptyList(), emptyList(), listOf(state)),
    )

    assertTrue(result.occurrenceAdjustments.isEmpty())
  }

  /** 数量不对应的响应无法安全关联，必须整次拒绝应用。 */
  @Test
  fun resultCountMismatchFailsClosed() = runTest {
    val local = testCategoryResource()
    val state = testCategoryState(local, PendingUpsert(local, 1))
    val capture = planner.capture(listOf(state), emptyList(), emptyList())

    val result = assertIs<ScheduleApplyResult.Failure>(
      applier.apply(capture, response(capture), listOf(state), emptyList(), emptyList()),
    )

    assertEquals(ScheduleApplyFailureReason.RESPONSE_CORRELATION, result.reason)
  }

  /** 构造与 capture 的 inventory 自动对齐的同步响应。 */
  private fun response(
    capture: ScheduleSyncCapture,
    categoryUpserts: List<UpsertResult<CategoryInput>> = emptyList(),
    adjustmentDeletes: List<DeleteResult<Long, OccurrenceAdjustmentInput>> = emptyList(),
  ): SyncResponse = SyncResponse(
    categories = CategorySyncResponse(
      confirmedResults = capture.request.categories.confirmed.map {
        ConfirmedResult(it.id, ConfirmedResultCode.CONFIRMED)
      },
      discoveredResults = emptyList(),
      upsertResults = categoryUpserts,
      deleteResults = emptyList(),
    ),
    schedules = ScheduleSyncResponse(
      confirmedResults = capture.request.schedules.confirmed.map {
        ConfirmedResult(it.id, ConfirmedResultCode.CONFIRMED)
      },
      discoveredResults = emptyList(),
      upsertResults = emptyList(),
      deleteResults = emptyList(),
    ),
    occurrenceAdjustments = OccurrenceAdjustmentSyncResponse(
      confirmedResults = capture.request.occurrenceAdjustments.confirmed.map {
        ConfirmedResult(it.id, ConfirmedResultCode.CONFIRMED)
      },
      discoveredResults = emptyList(),
      upsertResults = emptyList(),
      deleteResults = adjustmentDeletes,
    ),
  )
}
