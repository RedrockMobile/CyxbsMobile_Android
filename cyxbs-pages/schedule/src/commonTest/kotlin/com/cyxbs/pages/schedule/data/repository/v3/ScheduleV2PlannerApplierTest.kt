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
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideConfirmedResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideCurrent
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideSyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideTombstone
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleSyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.ServerResourceMeta as WireServerResourceMeta
import com.cyxbs.pages.schedule.data.remote.v3.SyncResponse
import com.cyxbs.pages.schedule.domain.sync.v2.AtomicField
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryResource
import com.cyxbs.pages.schedule.domain.sync.v2.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.v2.FieldPatch
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideResource
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceStatus
import com.cyxbs.pages.schedule.domain.sync.v2.PendingDelete
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
          tombstone = CategoryTombstone(100),
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

  @Test
  fun overrideDeleteCarriesLiveVersionAndStoresPayloadFreeTombstone() {
    val state = overrideDeleteState(version = 5, revision = 10)
    val capture = planner.capture("sync-override-delete", emptyList(), emptyList(), listOf(state))

    assertEquals(5uL, capture.request.occurrenceOverrides.confirmed.single().version)
    assertEquals(5uL, capture.request.occurrenceOverrides.deletes.single().version)

    val response = response(
      capture = capture,
      occurrenceDeletes = listOf(
        OccurrenceOverrideDeleteResult(
          scheduleId = state.identity.scheduleId,
          occurrenceDate = state.identity.occurrenceDate,
          result = MutationResultCode.DELETED,
          version = 6uL,
          tombstone = overrideTombstone(),
        ),
      ),
    )
    val result = assertIs<ScheduleV2ApplyResult.Success>(
      applier.apply(capture, response, emptyList(), emptyList(), listOf(state)),
    ).occurrenceOverrides.single()

    assertNull(result.remoteSnapshot)
    assertEquals(6, result.remoteTombstone?.version)
    assertNull(result.pending)
  }

  @Test
  fun deleteResponseKeepsNewerLocalUpsertAndNextCaptureUsesTombstoneVersion() {
    val stateR = overrideDeleteState(version = 5, revision = 10)
    val captureR = planner.capture("sync-override-r", emptyList(), emptyList(), listOf(stateR))
    val stateU = stateR.replacePending(
      PendingUpsert(
        occurrenceResource(stateR.identity, version = 5, title = "U"),
        localRevision = 11,
      ),
    )
    val responseR = response(
      capture = captureR,
      occurrenceDeletes = listOf(
        OccurrenceOverrideDeleteResult(
          scheduleId = stateR.identity.scheduleId,
          occurrenceDate = stateR.identity.occurrenceDate,
          result = MutationResultCode.DELETED,
          version = 6uL,
          tombstone = overrideTombstone(),
        ),
      ),
    )

    val merged = assertIs<ScheduleV2ApplyResult.Success>(
      applier.apply(captureR, responseR, emptyList(), emptyList(), listOf(stateU)),
    ).occurrenceOverrides.single()
    val nextCapture = planner.capture("sync-override-u", emptyList(), emptyList(), listOf(merged))

    assertEquals(6, merged.remoteTombstone?.version)
    assertEquals(11, merged.pending?.localRevision)
    assertEquals(6uL, nextCapture.request.occurrenceOverrides.confirmed.single().version)
    assertEquals(6uL, nextCapture.request.occurrenceOverrides.upserts.single().version)
    assertEquals("U", nextCapture.request.occurrenceOverrides.upserts.single().title.data.value)
  }

  @Test
  fun sameVersionLiveAndOverrideTombstoneConflictFailsClosed() {
    val state = overrideDeleteState(version = 5, revision = 10)
    val capture = planner.capture("sync-override-conflict", emptyList(), emptyList(), listOf(state))
    val response = response(
      capture = capture,
      occurrenceConfirmed = listOf(
        OccurrenceOverrideConfirmedResult(
          scheduleId = state.identity.scheduleId,
          occurrenceDate = state.identity.occurrenceDate,
          result = ConfirmedResultCode.DELETED,
          version = 5uL,
          tombstone = overrideTombstone(),
        ),
      ),
      occurrenceDeletes = listOf(
        OccurrenceOverrideDeleteResult(
          scheduleId = state.identity.scheduleId,
          occurrenceDate = state.identity.occurrenceDate,
          result = MutationResultCode.REJECTED,
          version = 5uL,
          current = state.remoteSnapshot!!.toWire(),
        ),
      ),
    )

    val result = assertIs<ScheduleV2ApplyResult.Failure>(
      applier.apply(capture, response, emptyList(), emptyList(), listOf(state)),
    )
    assertEquals(ScheduleV2ApplyFailureReason.SAME_VERSION_CONFLICT, result.reason)
  }

  private fun response(
    capture: ScheduleV2SyncCapture,
    discovered: List<CategoryCurrent> = emptyList(),
    categoryUpserts: List<CategoryUpsertResult> = emptyList(),
    occurrenceConfirmed: List<OccurrenceOverrideConfirmedResult>? = null,
    occurrenceDeletes: List<OccurrenceOverrideDeleteResult> = emptyList(),
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
    occurrenceOverrides = OccurrenceOverrideSyncResponse(
      confirmedResults = occurrenceConfirmed ?: capture.request.occurrenceOverrides.confirmed.map {
        OccurrenceOverrideConfirmedResult(
          scheduleId = it.scheduleId,
          occurrenceDate = it.occurrenceDate,
          result = ConfirmedResultCode.CONFIRMED,
          version = it.version,
        )
      },
      discoveredResults = emptyList(),
      upsertResults = emptyList(),
      deleteResults = occurrenceDeletes,
    ),
  )

  /** 构造带版本删除的远端 Override；旧业务补丁用于验证 tombstone 不会继续保存它。 */
  private fun overrideDeleteState(version: Long, revision: Long): OccurrenceOverrideSyncState {
    val identity = OccurrenceOverrideIdentity("schedule-override", 172_800_000)
    val resource = occurrenceResource(identity, version, title = "旧单次标题")
    return OccurrenceOverrideSyncState(
      identity = identity,
      remoteSnapshot = OccurrenceOverrideRemoteSnapshot(
        resource = resource,
        meta = ServerResourceMeta(createdAt = 1, remoteModifiedAt = version),
      ),
      pending = PendingDelete(identity, localModifiedAt = 100, localRevision = revision),
    )
  }

  /** 完整构造新 Override；恢复后重建时未设置的业务字段必须明确回到 INHERIT。 */
  private fun occurrenceResource(
    identity: OccurrenceOverrideIdentity,
    version: Long,
    title: String,
  ): OccurrenceOverrideResource = OccurrenceOverrideResource(
    identity = identity,
    version = version,
    status = AtomicField(OccurrenceStatus.ACTIVE, 10),
    timing = AtomicField(FieldPatch.Inherit, 11),
    title = AtomicField(FieldPatch.Replace(title), 12),
    description = AtomicField(FieldPatch.Inherit, 13),
    categoryId = AtomicField(FieldPatch.Inherit, 14),
    reminder = AtomicField(FieldPatch.Inherit, 15),
  )

  /** 服务端还原结果只保留正版本和删除时间；identity 由外层结果提供。 */
  private fun overrideTombstone(): OccurrenceOverrideTombstone = OccurrenceOverrideTombstone(
    deletedAt = 200,
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
