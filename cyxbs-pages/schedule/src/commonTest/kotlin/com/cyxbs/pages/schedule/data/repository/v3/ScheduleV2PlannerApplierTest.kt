package com.cyxbs.pages.schedule.data.repository.v3

import com.cyxbs.pages.schedule.data.remote.v3.AtomicField as WireAtomicField
import com.cyxbs.pages.schedule.data.remote.v3.CategoryConfirmedResult
import com.cyxbs.pages.schedule.data.remote.v3.CategoryCurrent
import com.cyxbs.pages.schedule.data.remote.v3.CategoryInput
import com.cyxbs.pages.schedule.data.remote.v3.CategorySyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.CategoryTombstone
import com.cyxbs.pages.schedule.data.remote.v3.CategoryUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.ConfirmedResultCode
import com.cyxbs.pages.schedule.data.remote.v3.MutationResultCode
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideSyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleSyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.ServerResourceMeta as WireServerResourceMeta
import com.cyxbs.pages.schedule.data.remote.v3.SyncResponse
import com.cyxbs.pages.schedule.domain.sync.v2.AtomicField
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryResource
import com.cyxbs.pages.schedule.domain.sync.v2.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.v2.PendingUpsert
import com.cyxbs.pages.schedule.domain.sync.v2.ServerResourceMeta
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** planner/applier 的逐资源协议合同测试。 */
class ScheduleV2PlannerApplierTest {
  private val planner = ScheduleV2RequestPlanner()
  private val applier = ScheduleV2ResponseApplier()

  @Test
  fun captureContainsConfirmedAndEveryPendingWithoutBatch() {
    val first = categoryState("first", remoteVersion = 3, pendingRevision = 4)
    val second = categoryState("second", remoteVersion = 5, pendingRevision = 6)

    val capture = planner.capture("sync-1", listOf(first, second), emptyList(), emptyList())

    assertEquals(listOf("first", "second"), capture.request.categories.confirmed.map { it.id })
    assertEquals(listOf("first", "second"), capture.request.categories.upserts.map { it.id })
    assertEquals(2, capture.categories.size)
  }

  @Test
  fun partialSuccessClearsOnlyAcceptedPending() {
    val accepted = categoryState("accepted", 3, 4)
    val rejected = categoryState("rejected", 3, 4)
    val capture = planner.capture("sync-partial", listOf(accepted, rejected), emptyList(), emptyList())
    val response = response(
      capture,
      categoryUpserts = listOf(
        CategoryUpsertResult(
          id = "accepted",
          result = MutationResultCode.APPLIED,
          current = wireCategoryCurrent("accepted", 4, "accepted-server"),
        ),
        CategoryUpsertResult(
          id = "rejected",
          result = MutationResultCode.REJECTED,
        ),
      ),
    )

    val result = assertIs<ScheduleV2ApplyResult.Success>(
      applier.apply(capture, response, listOf(accepted, rejected), emptyList(), emptyList()),
    )

    assertNull(result.categories.single { it.identity.id == "accepted" }.pending)
    assertEquals(4, result.categories.single { it.identity.id == "rejected" }.pending?.localRevision)
  }

  @Test
  fun acceptedRDoesNotClearNewerU() {
    val stateR = categoryState("category", 7, 10)
    val capture = planner.capture("sync-r-u", listOf(stateR), emptyList(), emptyList())
    val stateU = stateR.replacePending(
      PendingUpsert(
        pendingResource(stateR).copy(name = AtomicField("U", 99)),
        localRevision = 11,
      ),
    )
    val response = response(
      capture,
      categoryUpserts = listOf(
        CategoryUpsertResult(
          id = "category",
          result = MutationResultCode.APPLIED,
          current = wireCategoryCurrent("category", 8, "server-R"),
        ),
      ),
    )

    val result = assertIs<ScheduleV2ApplyResult.Success>(
      applier.apply(capture, response, listOf(stateU), emptyList(), emptyList()),
    ).categories.single()

    assertEquals(8, result.remoteSnapshot?.version)
    assertEquals(11, result.pending?.localRevision)
    assertEquals("U", result.effectiveResource()?.name?.data)
  }

  @Test
  fun structuralHttpFailureKeepsUploadedPending() {
    val state = categoryState("category", 3, 4)
    val capture = planner.capture("sync-http-400", listOf(state), emptyList(), emptyList())

    val result = assertIs<ScheduleV2ApplyResult.Success>(
      applier.discardUploaded(capture, listOf(state), emptyList(), emptyList()),
    )

    assertEquals(4, result.categories.single().pending?.localRevision)
  }

  @Test
  fun tombstoneDeletesRemoteAndPending() {
    val state = categoryState("category", 3, 4)
    val capture = planner.capture("sync-delete", listOf(state), emptyList(), emptyList())
    val response = response(
      capture,
      categoryUpserts = listOf(
        CategoryUpsertResult(
          id = "category",
          result = MutationResultCode.RESOURCE_DELETED,
          tombstone = CategoryTombstone("category", 100),
        ),
      ),
    )

    val result = assertIs<ScheduleV2ApplyResult.Success>(
      applier.apply(capture, response, listOf(state), emptyList(), emptyList()),
    )
    assertTrue(result.categories.isEmpty())
  }

  @Test
  fun discoveredSameVersionConflictFailsClosed() {
    val capture = planner.capture("sync-conflict", emptyList(), emptyList(), emptyList())
    val response = response(
      capture,
      discovered = listOf(
        wireCategoryCurrent("category", 4, "first"),
        wireCategoryCurrent("category", 4, "second"),
      ),
    )

    val result = assertIs<ScheduleV2ApplyResult.Failure>(
      applier.apply(capture, response, emptyList(), emptyList(), emptyList()),
    )
    assertEquals(ScheduleV2ApplyFailureReason.SAME_VERSION_CONFLICT, result.reason)
  }

  @Test
  fun resultCountOrIdentityMismatchFailsClosed() {
    val state = categoryState("category", 3, 4)
    val capture = planner.capture("sync-malformed", listOf(state), emptyList(), emptyList())

    val missing = assertIs<ScheduleV2ApplyResult.Failure>(
      applier.apply(capture, response(capture), listOf(state), emptyList(), emptyList()),
    )
    assertEquals(ScheduleV2ApplyFailureReason.RESPONSE_CORRELATION, missing.reason)

    val wrongIdentity = assertIs<ScheduleV2ApplyResult.Failure>(
      applier.apply(
        capture,
        response(
          capture,
          categoryUpserts = listOf(CategoryUpsertResult("other", MutationResultCode.REJECTED)),
        ),
        listOf(state),
        emptyList(),
        emptyList(),
      ),
    )
    assertEquals(ScheduleV2ApplyFailureReason.RESPONSE_CORRELATION, wrongIdentity.reason)
  }

  private fun response(
    capture: ScheduleV2SyncCapture,
    discovered: List<CategoryCurrent> = emptyList(),
    categoryUpserts: List<CategoryUpsertResult> = emptyList(),
  ): SyncResponse = SyncResponse(
    syncRequestId = capture.request.syncRequestId,
    categories = CategorySyncResponse(
      confirmedResults = capture.request.categories.confirmed.map {
        CategoryConfirmedResult(it.id, ConfirmedResultCode.CONFIRMED, it.version)
      },
      discoveredResults = discovered,
      upsertResults = categoryUpserts,
      deleteResults = emptyList(),
    ),
    schedules = ScheduleSyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
    occurrenceOverrides = OccurrenceOverrideSyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
  )

  private fun categoryState(id: String, remoteVersion: Long, pendingRevision: Long): CategorySyncState {
    val remote = categoryRemote(id, remoteVersion)
    return CategorySyncState(
      identity = remote.identity,
      remoteSnapshot = remote,
      pending = PendingUpsert(
        resource = remote.resource.copy(name = AtomicField("pending-$id", 10)),
        localRevision = pendingRevision,
      ),
    )
  }

  @Suppress("UNCHECKED_CAST")
  private fun pendingResource(state: CategorySyncState): CategoryResource =
    (state.pending as PendingUpsert<CategoryIdentity, CategoryResource>).resource

  private fun categoryRemote(id: String, version: Long): CategoryRemoteSnapshot = CategoryRemoteSnapshot(
    resource = CategoryResource(
      identity = CategoryIdentity(id),
      version = version,
      name = AtomicField("remote-$id", 1),
      color = AtomicField("#000000", 2),
      sortOrder = AtomicField(0, 3),
    ),
    meta = ServerResourceMeta(createdAt = 1, remoteModifiedAt = version),
  )

  private fun wireCategoryCurrent(id: String, version: Long, name: String): CategoryCurrent = CategoryCurrent(
    resource = CategoryInput(
      id = id,
      version = version.toULong(),
      name = WireAtomicField(name, version),
      color = WireAtomicField("#000000", 2),
      sortOrder = WireAtomicField(0, 3),
    ),
    meta = WireServerResourceMeta(createdAt = 1, remoteModifiedAt = version),
  )
}
