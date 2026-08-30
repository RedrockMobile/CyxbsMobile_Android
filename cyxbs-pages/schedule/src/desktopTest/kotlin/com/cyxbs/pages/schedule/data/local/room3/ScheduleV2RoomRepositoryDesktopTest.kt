package com.cyxbs.pages.schedule.data.local.room3

import com.cyxbs.components.account.api.AccountSession
import com.cyxbs.components.account.api.AccountState
import com.cyxbs.components.config.sp.PreferencesSettings
import com.cyxbs.components.utils.network.ApiWrapper
import com.cyxbs.pages.schedule.data.failure.ScheduleFailureOperation
import com.cyxbs.pages.schedule.data.failure.ScheduleFailureRecord
import com.cyxbs.pages.schedule.data.failure.ScheduleFailureRecordSink
import com.cyxbs.pages.schedule.data.failure.SettingsScheduleFailureRecordStore
import com.cyxbs.pages.schedule.data.failure.scheduleIds
import com.cyxbs.pages.schedule.data.remote.v3.AtomicBatch
import com.cyxbs.pages.schedule.data.remote.v3.AtomicBatchResult
import com.cyxbs.pages.schedule.data.remote.v3.AtomicBatchResultCode
import com.cyxbs.pages.schedule.data.remote.v3.AtomicField
import com.cyxbs.pages.schedule.data.remote.v3.CategoryAtomicBlock
import com.cyxbs.pages.schedule.data.remote.v3.CategoryAtomicDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.CategoryAtomicResultBlock
import com.cyxbs.pages.schedule.data.remote.v3.CategoryAtomicUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.CategoryCurrent
import com.cyxbs.pages.schedule.data.remote.v3.CategoryInput
import com.cyxbs.pages.schedule.data.remote.v3.CategorySyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.CategoryTombstone
import com.cyxbs.pages.schedule.data.remote.v3.CategoryUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.FieldPatch
import com.cyxbs.pages.schedule.data.remote.v3.MutationResultCode
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideAtomicBlock
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideAtomicDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideAtomicResultBlock
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideAtomicUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideCurrent
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideInput
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideSyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideTombstone
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceStatus
import com.cyxbs.pages.schedule.data.remote.v3.PatchMode
import com.cyxbs.pages.schedule.data.remote.v3.RecurrenceFrequency
import com.cyxbs.pages.schedule.data.remote.v3.RecurrenceInput
import com.cyxbs.pages.schedule.data.remote.v3.ReminderInput
import com.cyxbs.pages.schedule.data.remote.v3.ResultReason
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleAtomicBlock
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleAtomicDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleAtomicResultBlock
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleAtomicUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleCurrent
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleKind
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleSyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleTombstone
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleV2CallResult
import com.cyxbs.pages.schedule.data.remote.v3.ServerResourceMeta
import com.cyxbs.pages.schedule.data.remote.v3.SyncRequest
import com.cyxbs.pages.schedule.data.remote.v3.SyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.TimingInput
import com.cyxbs.pages.schedule.data.remote.v3.TimingKind
import com.cyxbs.pages.schedule.data.remote.v3.TodoState
import com.cyxbs.pages.schedule.domain.model.CategoryId
import com.cyxbs.pages.schedule.domain.model.Schedule
import com.cyxbs.pages.schedule.domain.model.ScheduleCategory
import com.cyxbs.pages.schedule.domain.model.ScheduleId
import com.cyxbs.pages.schedule.domain.model.ScheduleTiming
import com.cyxbs.pages.schedule.domain.model.ScheduleTodoState
import com.cyxbs.pages.schedule.domain.repository.ScheduleCommand
import com.cyxbs.pages.schedule.domain.repository.ScheduleRemoteError
import com.cyxbs.pages.schedule.domain.repository.ScheduleRepositoryStatus
import com.cyxbs.pages.schedule.domain.repository.ScheduleSyncResult
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

/** 新 Room repository 的低成本 Desktop 合同测试；网络替身只模拟单次 v3 gateway 调用。 */
class ScheduleV2RoomRepositoryDesktopTest {
  /** 失败源数据即使超过单个 Settings value 限制，也能跨 store 实例完整恢复并删除。 */
  @Test
  fun failureRecordPersistsInChunkedAccountSettings() {
    val settings = PreferencesSettings.get("ScheduleV2FailureRecordDesktopTest")
    settings.clear()
    try {
      val source = scheduleInput("持久化失败记录")
      val record = ScheduleFailureRecord(
        scheduleId = source.id,
        failedAt = 100L,
        operation = ScheduleFailureOperation.CREATE,
        reasonCode = "HTTP_400",
        message = "原因".repeat(1_200),
        sourceSchedule = source,
        sourceBatch = AtomicBatch(
          batchId = "failure-batch",
          categories = CategoryAtomicBlock(emptyList(), emptyList()),
          schedules = ScheduleAtomicBlock(listOf(source), emptyList()),
          occurrenceOverrides = OccurrenceOverrideAtomicBlock(emptyList(), emptyList()),
        ),
      )

      SettingsScheduleFailureRecordStore { settings }.record(ACCOUNT, listOf(record))
      val restored = SettingsScheduleFailureRecordStore { settings }.observe(ACCOUNT).value.single()

      assertEquals(record, restored)
      SettingsScheduleFailureRecordStore { settings }.remove(ACCOUNT, setOf(source.id))
      assertTrue(SettingsScheduleFailureRecordStore { settings }.observe(ACCOUNT).value.isEmpty())
    } finally {
      settings.clear()
    }
  }
  @Test
  fun initializeThenDailyCreatePersistsAndClearsPending() = runTest {
    withRepository { repository, gateway, database ->
      repository.initialize()
      assertEquals(ACCOUNT, repository.snapshot.value.accountId)

      val result = repository.execute(ScheduleCommand.Create(schedule("本地创建")))

      assertIs<ScheduleSyncResult.Success>(result)
      assertEquals(1, gateway.createCalls)
      assertEquals("本地创建", repository.snapshot.value.schedules.single().title)
      assertIs<ScheduleRepositoryStatus.Ready>(repository.snapshot.value.status)
      assertTrue(ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.single().localRevision == null)
    }
  }

  @Test
  fun dailyRejectedKeepsPendingAndCreatesFailureRecord() = runTest {
    withRepositoryAndFailures { repository, gateway, database, failureRecords ->
      gateway.createResponder = { batch ->
        ScheduleV2CallResult.Completed(
          ApiWrapper(
            rejectedBatchResult(batch, ResultReason.RESOURCE_CHANGED),
            20101,
            "rejected",
          ),
        )
      }
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Create(schedule("拒绝")))

      val failure = assertIs<ScheduleSyncResult.Failure>(result)
      assertIs<ScheduleRemoteError.MutationRejected>(failure.error)
      val status = assertIs<ScheduleRepositoryStatus.Unavailable>(repository.snapshot.value.status)
      assertEquals(1, status.pendingCount)
      assertEquals("拒绝", repository.snapshot.value.schedules.single().title)
      assertEquals(
        1L,ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.single().localRevision
      )
      assertEquals("拒绝", failureRecords.observe(ACCOUNT).value.single().sourceSchedule.title.data)
      assertEquals(
        ResultReason.RESOURCE_CHANGED.name,
        failureRecords.observe(ACCOUNT).value.single().reasonCode)
    }
  }

  @Test
  fun categoryMutationUsesSameDailyAggregateEndpoint() = runTest {
    withRepository { repository, gateway, database ->
      repository.initialize()

      val result = repository.execute(
        ScheduleCommand.CreateCategory(ScheduleCategory(CategoryId(SECOND_CATEGORY_ID), 0, "分类", null, 0)),
      )

      assertIs<ScheduleSyncResult.Success>(result)
      assertEquals(1, gateway.createCalls)
      assertEquals(null, ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT)
        .categories.first { it.categoryId == SECOND_CATEGORY_ID }.localRevision)
    }
  }

  @Test
  fun dailyTransportFailureKeepsLocallyPersistedPending() = runTest {
    withRepositoryAndFailures { repository, gateway, database, failureRecords ->
      gateway.createResponder = {
        ScheduleV2CallResult.TransportFailure(null, IllegalStateException("offline"))
      }
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Create(schedule("离线")))

      assertIs<ScheduleSyncResult.Failure>(result)
      assertIs<ScheduleRepositoryStatus.Unavailable>(repository.snapshot.value.status)
      assertEquals(1L, ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.single().localRevision)
      assertTrue(failureRecords.observe(ACCOUNT).value.isEmpty())
    }
  }

  @Test
  fun dailyInvalidRequestKeepsPendingAndCreatesFailureRecord() = runTest {
    withRepositoryAndFailures { repository, gateway, database, failureRecords ->
      gateway.createResponder = {
        ScheduleV2CallResult.RequestInvalid("invalid business payload")
      }
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Create(schedule("非法数据")))

      assertIs<ScheduleRemoteError.InvalidResponse>(assertIs<ScheduleSyncResult.Failure>(result).error)
      assertEquals(
        "非法数据",ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.single().pendingSnapshot?.title?.data)
      assertEquals(
        1, assertIs<ScheduleRepositoryStatus.Unavailable>(repository.snapshot.value.status).pendingCount
      )
      val record = failureRecords.observe(ACCOUNT).value.single()
      assertEquals("HTTP_400", record.reasonCode)
      assertEquals("invalid business payload", record.message)
    }
  }

  @Test
  fun categoryTransportFailureAlsoKeepsPending() = runTest {
    withRepository { repository, gateway, database ->
      gateway.createResponder = {
        ScheduleV2CallResult.TransportFailure(null, IllegalStateException("offline"))
      }
      repository.initialize()

      assertIs<ScheduleSyncResult.Failure>(
        repository.execute(ScheduleCommand.CreateCategory(ScheduleCategory(CategoryId(SECOND_CATEGORY_ID), 0, "分类", null, 0))),
      )

      assertIs<ScheduleRepositoryStatus.Unavailable>(repository.snapshot.value.status)
      assertEquals(1, gateway.createCalls)
      assertEquals(1L, ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT)
        .categories.first { it.categoryId == SECOND_CATEGORY_ID }.localRevision)
    }
  }

  @Test
  fun http200DecodeFailureMapsToInvalidResponse() = runTest {
    withRepository { repository, gateway, _ ->
      gateway.createResponder = {
        ScheduleV2CallResult.TransportFailure(200, IllegalArgumentException("invalid body"))
      }
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Create(schedule("坏响应")))

      assertIs<ScheduleRemoteError.InvalidResponse>(assertIs<ScheduleSyncResult.Failure>(result).error)
    }
  }

  @Test
  fun requestTimeoutMapsToTimeout() = runTest {
    withRepository { repository, gateway, _ ->
      gateway.createResponder = {
        ScheduleV2CallResult.TransportFailure(null, HttpRequestTimeoutException("https://schedule.test", 100L))
      }
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Create(schedule("超时")))

      assertIs<ScheduleRemoteError.Timeout>(assertIs<ScheduleSyncResult.Failure>(result).error)
    }
  }

  @Test
  fun newerLocalUpdateSurvivesEarlierDailyResponse() = runTest {
    withRepository { repository, gateway, database ->
      val firstStarted = CompletableDeferred<Unit>()
      val firstResponse = CompletableDeferred<ScheduleV2CallResult<AtomicBatchResult>>()
      gateway.createResponder = { batch ->
        if (gateway.createCalls == 1) {
          firstStarted.complete(Unit)
          firstResponse.await()
        } else {
          ScheduleV2CallResult.TransportFailure(null, IllegalStateException("keep U pending"))
        }
      }
      repository.initialize()

      val requestR = async { repository.execute(ScheduleCommand.Create(schedule("R"))) }
      firstStarted.await()
      assertIs<ScheduleSyncResult.Failure>(repository.execute(ScheduleCommand.Update(schedule("U"))))
      firstResponse.complete(
        ScheduleV2CallResult.Completed(
          ApiWrapper(
            appliedBatchResult(batch = gateway.firstCreatedBatch!!),
            10000,
            "ok",
          ),
        ),
      )

      assertIs<ScheduleSyncResult.Success>(requestR.await())
      assertEquals("U", repository.snapshot.value.schedules.single().title)
      assertEquals(2L, ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.single().localRevision)
    }
  }

  @Test
  fun fullSyncCategoryRejectedKeepsMatchingPendingAndSurfacesBusinessError() = runTest {
    withRepository { repository, gateway, database ->
      repository.initialize()
      gateway.createResponder = {
        ScheduleV2CallResult.TransportFailure(null, IllegalStateException("offline"))
      }
      assertIs<ScheduleSyncResult.Failure>(
        repository.execute(ScheduleCommand.CreateCategory(ScheduleCategory(CategoryId(SECOND_CATEGORY_ID), 0, "分类", null, 0))),
      )
      gateway.syncResponder = { request ->
        SyncResponse(
          request.syncRequestId,
          CategorySyncResponse(
            emptyList(),
            emptyList(),
            listOf(CategoryUpsertResult(SECOND_CATEGORY_ID, MutationResultCode.REJECTED, ResultReason.CATEGORY_NOT_FOUND)),
            emptyList(),
          ),
          ScheduleSyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
          OccurrenceOverrideSyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
          emptyList(),
        )
      }

      val result = repository.execute(ScheduleCommand.RequestSync)

      val failure = assertIs<ScheduleSyncResult.Failure>(result)
      assertEquals(ScheduleRemoteError.MutationRejected(
        com.cyxbs.pages.schedule.domain.repository.ScheduleMutationBusinessRejectionReason.CATEGORY_NOT_FOUND,
      ), failure.error)
      assertEquals(
        1L,ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT)
        .categories.first { it.categoryId == SECOND_CATEGORY_ID }.localRevision
      )
    }
  }

  /** 完整 Sync 的普通 Schedule REJECTED 同样保留本地 pending，并生成可编辑记录。 */
  @Test
  fun fullSyncScheduleRejectedKeepsPendingAndCreatesFailureRecord() = runTest {
    withRepositoryAndFailures { repository, gateway, database, failureRecords ->
      gateway.createResponder = {
        ScheduleV2CallResult.TransportFailure(null, IllegalStateException("offline"))
      }
      repository.initialize()
      assertIs<ScheduleSyncResult.Failure>(repository.execute(ScheduleCommand.Create(schedule("同步拒绝"))))
      gateway.syncResponder = { request ->
        SyncResponse(
          request.syncRequestId,
          CategorySyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
          ScheduleSyncResponse(
            emptyList(),
            emptyList(),
            listOf(
              ScheduleUpsertResult(
                SCHEDULE_ID,
                MutationResultCode.REJECTED,
                ResultReason.INVALID_REQUEST
              )
            ),
            emptyList(),
          ),
          OccurrenceOverrideSyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
          emptyList(),
        )
      }

      assertIs<ScheduleSyncResult.Failure>(repository.execute(ScheduleCommand.RequestSync))

      assertEquals(
        1L, ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT)
          .schedules.single().localRevision
      )
      val record = failureRecords.observe(ACCOUNT).value.single()
      assertEquals("同步拒绝", record.sourceSchedule.title.data)
      assertEquals(ScheduleFailureOperation.SYNC, record.operation)
    }
  }

  /** DELETE 失败时有效资源已被 pending 隐藏，记录仍应从 remote 快照恢复删除前内容。 */
  @Test
  fun dailyDeleteInvalidRequestKeepsPendingAndRecoversEditableSource() = runTest {
    withRepositoryAndFailures { repository, gateway, database, failureRecords ->
      repository.initialize()
      assertIs<ScheduleSyncResult.Success>(repository.execute(ScheduleCommand.Create(schedule("删除前标题"))))
      gateway.deleteResponder = { ScheduleV2CallResult.RequestInvalid("delete rejected") }

      assertIs<ScheduleSyncResult.Failure>(
        repository.execute(ScheduleCommand.Delete(ScheduleId(SCHEDULE_ID))),
      )

      val state = ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.single()
      assertTrue(state.pendingOperation != null)
      assertEquals(
        "删除前标题",
        failureRecords.observe(ACCOUNT).value.single().sourceSchedule.title.data
      )
      assertEquals(
        ScheduleFailureOperation.DELETE,
        failureRecords.observe(ACCOUNT).value.single().operation
      )
    }
  }

  /** 再次编辑后即使网络仍失败，失败页也必须恢复最新内容，而不是第一次失败时的旧标题。 */
  @Test
  fun failedRecordFollowsNewerLocalUpdateWhenRetryTransportFails() = runTest {
    withRepositoryAndFailures { repository, gateway, _, failureRecords ->
      gateway.createResponder = { ScheduleV2CallResult.RequestInvalid("create rejected") }
      repository.initialize()
      assertIs<ScheduleSyncResult.Failure>(repository.execute(ScheduleCommand.Create(schedule("旧标题"))))

      gateway.createResponder = {
        ScheduleV2CallResult.TransportFailure(null, IllegalStateException("still offline"))
      }
      assertIs<ScheduleSyncResult.Failure>(repository.execute(ScheduleCommand.Update(schedule("新标题"))))

      val record = failureRecords.observe(ACCOUNT).value.single()
      assertEquals("新标题", record.sourceSchedule.title.data)
      assertEquals("新标题", record.sourceBatch.schedules.upserts.single().title.data)
      assertEquals("HTTP_400", record.reasonCode)
    }
  }

  /** 用户删除失败日程后，即使删除请求仍断网，旧的可编辑失败记录也应移除并由 Room pending 继续重试。 */
  @Test
  fun deletingFailedLocalCreateRemovesFailureRecordWhenDeleteTransportFails() = runTest {
    withRepositoryAndFailures { repository, gateway, database, failureRecords ->
      gateway.createResponder = { ScheduleV2CallResult.RequestInvalid("create rejected") }
      repository.initialize()
      assertIs<ScheduleSyncResult.Failure>(repository.execute(ScheduleCommand.Create(schedule("待放弃"))))
      assertEquals(1, failureRecords.observe(ACCOUNT).value.size)
      gateway.deleteResponder = {
        ScheduleV2CallResult.TransportFailure(null, IllegalStateException("still offline"))
      }

      val result = repository.execute(ScheduleCommand.Delete(ScheduleId(SCHEDULE_ID)))

      assertIs<ScheduleSyncResult.Failure>(result)
      assertTrue(failureRecords.observe(ACCOUNT).value.isEmpty())
      assertTrue(repository.snapshot.value.schedules.isEmpty())
      assertTrue(
        ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.single().pendingOperation != null,
      )
      assertEquals(1, gateway.createCalls)
      assertEquals(1, gateway.deleteCalls)
    }
  }

  /** 用户修复失败日程后，只有 canonical 成功且该 identity 已无 pending 才移除失败记录。 */
  @Test
  fun successfulRetryClearsFailureRecord() = runTest {
    withRepositoryAndFailures { repository, gateway, _, failureRecords ->
      gateway.createResponder = { batch ->
        ScheduleV2CallResult.Completed(
          ApiWrapper(rejectedBatchResult(batch, ResultReason.INVALID_REQUEST), 20101, "rejected"),
        )
      }
      repository.initialize()
      assertIs<ScheduleSyncResult.Failure>(repository.execute(ScheduleCommand.Create(schedule("待修复"))))
      assertEquals(1, failureRecords.observe(ACCOUNT).value.size)

      gateway.createResponder = { batch ->
        ScheduleV2CallResult.Completed(ApiWrapper(appliedBatchResult(batch), 10000, "ok"))
      }
      assertIs<ScheduleSyncResult.Success>(repository.execute(ScheduleCommand.Update(schedule("已修复"))))

      assertEquals("已修复", repository.snapshot.value.schedules.single().title)
      assertTrue(failureRecords.observe(ACCOUNT).value.isEmpty())
    }
  }

  /** 分类删除复用既有聚合 DELETE 接口，不为 Category 引入第二套远端协议。 */
  @Test
  fun categoryDeleteUsesExistingDailyDeleteEndpoint() = runTest {
    withRepository { repository, gateway, database ->
      repository.initialize()

      val result = repository.execute(ScheduleCommand.DeleteCategory(CategoryId(CATEGORY_ID)))
      val batch = requireNotNull(gateway.lastDeletedBatch)

      assertIs<ScheduleSyncResult.Success>(result)
      assertEquals(1, gateway.deleteCalls)
      assertEquals(listOf(CATEGORY_ID), batch.categories.deletes.map { it.id })
      assertTrue(batch.categories.upserts.isEmpty())
      assertTrue(batch.schedules.upserts.isEmpty())
      assertTrue(batch.schedules.deletes.isEmpty())
      assertTrue(batch.occurrenceOverrides.upserts.isEmpty())
      assertTrue(batch.occurrenceOverrides.deletes.isEmpty())
      assertTrue(ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).categories.isEmpty())
    }
  }

  @Test
  fun ordinaryDeleteUsesDailyDeleteEndpoint() = runTest {
    withRepository { repository, gateway, database ->
      repository.initialize()
      assertIs<ScheduleSyncResult.Success>(repository.execute(ScheduleCommand.Create(schedule("待删除"))))

      val result = repository.execute(ScheduleCommand.Delete(ScheduleId(SCHEDULE_ID)))

      assertIs<ScheduleSyncResult.Success>(result)
      assertEquals(1, gateway.deleteCalls)
      assertTrue(ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.isEmpty())
    }
  }

  @Test
  fun scheduleDeleteCarriesOverrideInSameDailyBatch() = runTest {
    withRepository { repository, gateway, database ->
      seedRecurringScheduleAndOverride(database)
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Delete(ScheduleId(SCHEDULE_ID)))
      val state = ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT)

      assertIs<ScheduleSyncResult.Success>(result)
      assertEquals(1, gateway.deleteCalls)
      assertEquals(1, gateway.lastDeletedBatch?.schedules?.deletes?.size)
      assertEquals(1, gateway.lastDeletedBatch?.occurrenceOverrides?.deletes?.size)
      assertTrue(state.schedules.isEmpty())
      assertTrue(state.occurrenceOverrides.isEmpty())
    }
  }

  /** 为每个 case 创建真实 bundled SQLite，避免 fake 存储绕过 replaceAccountState 的全量替换语义。 */
  private suspend fun withRepository(
    block: suspend (RoomScheduleRepository, FakeGateway, ScheduleRoomDatabase) -> Unit,
  ) = withRepositoryAndFailures { repository, gateway, database, _ ->
    block(repository, gateway, database)
  }

  /** 需要断言失败记录的 case 额外暴露内存 sink，避免测试污染真实账号 Settings。 */
  private suspend fun withRepositoryAndFailures(
    block: suspend (RoomScheduleRepository, FakeGateway, ScheduleRoomDatabase, InMemoryFailureRecords) -> Unit,
  ) {
    val path = Files.createTempFile("schedule-v2-repository-", ".db")
    Files.deleteIfExists(path)
    val database = buildScheduleRoomDatabase(path.toString())
    try {
      seedRemoteCategory(database)
      val gateway = FakeGateway()
      val failureRecords = InMemoryFailureRecords()
      val repository = RoomScheduleRepositoryFactory(
        database = database,
        gatewayFactory = { gateway },
        nowMillis = { 100L },
        failureRecords = failureRecords,
      ).create(AccountSession(1, AccountState.Login(ACCOUNT))) as RoomScheduleRepository
      block(repository, gateway, database, failureRecords)
    } finally {
      database.closeScheduleRoomDatabase()
      Files.deleteIfExists(path)
      Files.deleteIfExists(path.resolveSibling("${path.fileName}-wal"))
      Files.deleteIfExists(path.resolveSibling("${path.fileName}-shm"))
    }
  }

  /** 仅用于验证 Repository 的记录时机，不复制 AccountSettings 的序列化实现。 */
  private class InMemoryFailureRecords : ScheduleFailureRecordSink {
    private val records = mutableMapOf<String, MutableStateFlow<List<ScheduleFailureRecord>>>()

    override fun observe(accountId: String): StateFlow<List<ScheduleFailureRecord>> =
      records.getOrPut(accountId) { MutableStateFlow(emptyList()) }

    override fun record(accountId: String, records: List<ScheduleFailureRecord>) {
      val state = this.records.getOrPut(accountId) { MutableStateFlow(emptyList()) }
      state.value =
        (state.value.associateBy { it.scheduleId } + records.associateBy { it.scheduleId })
          .values
          .sortedByDescending { it.failedAt }
    }

    override fun refreshSources(accountId: String, batch: AtomicBatch) {
      val state = records.getOrPut(accountId) { MutableStateFlow(emptyList()) }
      val affectedIds = batch.scheduleIds()
      val sources = batch.schedules.upserts.associateBy { it.id }
      state.value = state.value.map { record ->
        if (record.scheduleId !in affectedIds) record
        else record.copy(
          sourceSchedule = sources[record.scheduleId] ?: record.sourceSchedule,
          sourceBatch = batch,
        )
      }
    }

    override fun remove(accountId: String, scheduleIds: Set<String>) {
      val state = records.getOrPut(accountId) { MutableStateFlow(emptyList()) }
      state.value = state.value.filterNot { it.scheduleId in scheduleIds }
    }
  }

  /** 最小 gateway recorder：Sync 返回空 inventory，日常 create 默认回显 canonical Current。 */
  private inner class FakeGateway : ScheduleV2RepositoryGateway {
    var createCalls = 0
    var deleteCalls = 0
    var firstCreatedBatch: AtomicBatch? = null
    var lastDeletedBatch: AtomicBatch? = null
    var syncResponder: (SyncRequest) -> SyncResponse = { request -> emptySyncResponse(request.syncRequestId) }
    var createResponder: suspend (AtomicBatch) -> ScheduleV2CallResult<AtomicBatchResult> = { batch ->
      ScheduleV2CallResult.Completed(ApiWrapper(appliedBatchResult(batch), 10000, "ok"))
    }
    var deleteResponder: suspend (AtomicBatch) -> ScheduleV2CallResult<AtomicBatchResult> = createResponder

    override suspend fun sync(accountId: String, request: SyncRequest): ScheduleV2CallResult<SyncResponse> =
      ScheduleV2CallResult.Completed(ApiWrapper(syncResponder(request), 10000, "ok"))

    override suspend fun createSchedule(accountId: String, input: AtomicBatch): ScheduleV2CallResult<AtomicBatchResult> {
      createCalls += 1
      if (firstCreatedBatch == null) firstCreatedBatch = input
      return createResponder(input)
    }

    override suspend fun updateSchedule(accountId: String, input: AtomicBatch): ScheduleV2CallResult<AtomicBatchResult> {
      return createResponder(input)
    }

    override suspend fun deleteSchedule(accountId: String, input: AtomicBatch): ScheduleV2CallResult<AtomicBatchResult> {
      deleteCalls += 1
      lastDeletedBatch = input
      return deleteResponder(input)
    }
  }

  /** 构造与请求逐项对齐的成功结果，模拟服务端在同一事务中返回整批 canonical 状态。 */
  private fun appliedBatchResult(batch: AtomicBatch) = AtomicBatchResult(
    batchId = batch.batchId,
    code = AtomicBatchResultCode.APPLIED,
    categories = CategoryAtomicResultBlock(
      upsertResults = batch.categories.upserts.map {
        CategoryAtomicUpsertResult(it.id, AtomicBatchResultCode.APPLIED)
      },
      deleteResults = batch.categories.deletes.map {
        CategoryAtomicDeleteResult(it.id, AtomicBatchResultCode.APPLIED)
      },
      relatedUpserts = batch.categories.upserts.map {
        CategoryCurrent(it.copy(version = nextVersion(it.version)), ServerResourceMeta(1, 2))
      },
      relatedDeletes = batch.categories.deletes.map { CategoryTombstone(it.id, 2) },
    ),
    schedules = ScheduleAtomicResultBlock(
      upsertResults = batch.schedules.upserts.map {
        ScheduleAtomicUpsertResult(it.id, AtomicBatchResultCode.APPLIED)
      },
      deleteResults = batch.schedules.deletes.map {
        ScheduleAtomicDeleteResult(it.id, AtomicBatchResultCode.APPLIED)
      },
      relatedUpserts = batch.schedules.upserts.map {
        ScheduleCurrent(it.copy(version = nextVersion(it.version)), ServerResourceMeta(1, 2))
      },
      relatedDeletes = batch.schedules.deletes.map { ScheduleTombstone(it.id, 2) },
    ),
    occurrenceOverrides = OccurrenceOverrideAtomicResultBlock(
      upsertResults = batch.occurrenceOverrides.upserts.map {
        OccurrenceOverrideAtomicUpsertResult(
          it.scheduleId,
          it.occurrenceDate,
          AtomicBatchResultCode.APPLIED,
        )
      },
      deleteResults = batch.occurrenceOverrides.deletes.map {
        OccurrenceOverrideAtomicDeleteResult(
          it.scheduleId,
          it.occurrenceDate,
          AtomicBatchResultCode.APPLIED,
        )
      },
      relatedUpserts = batch.occurrenceOverrides.upserts.map {
        OccurrenceOverrideCurrent(it.copy(version = nextVersion(it.version)), ServerResourceMeta(1, 2))
      },
      relatedDeletes = batch.occurrenceOverrides.deletes.map {
        OccurrenceOverrideTombstone(it.scheduleId, it.occurrenceDate, 2)
      },
    ),
  )

  /** 构造完整对齐的业务拒绝结果；服务端不返回任何伪 canonical 快照。 */
  private fun rejectedBatchResult(batch: AtomicBatch, reason: ResultReason) = AtomicBatchResult(
    batchId = batch.batchId,
    code = AtomicBatchResultCode.REJECTED,
    reason = reason,
    categories = CategoryAtomicResultBlock(
      batch.categories.upserts.map { CategoryAtomicUpsertResult(it.id, AtomicBatchResultCode.REJECTED, reason) },
      batch.categories.deletes.map { CategoryAtomicDeleteResult(it.id, AtomicBatchResultCode.REJECTED, reason) },
      emptyList(),
      emptyList(),
    ),
    schedules = ScheduleAtomicResultBlock(
      batch.schedules.upserts.map { ScheduleAtomicUpsertResult(it.id, AtomicBatchResultCode.REJECTED, reason) },
      batch.schedules.deletes.map { ScheduleAtomicDeleteResult(it.id, AtomicBatchResultCode.REJECTED, reason) },
      emptyList(),
      emptyList(),
    ),
    occurrenceOverrides = OccurrenceOverrideAtomicResultBlock(
      batch.occurrenceOverrides.upserts.map {
        OccurrenceOverrideAtomicUpsertResult(it.scheduleId, it.occurrenceDate, AtomicBatchResultCode.REJECTED, reason)
      },
      batch.occurrenceOverrides.deletes.map {
        OccurrenceOverrideAtomicDeleteResult(it.scheduleId, it.occurrenceDate, AtomicBatchResultCode.REJECTED, reason)
      },
      emptyList(),
      emptyList(),
    ),
  )

  /** Create 从 version=0 进入 version=1；后续更新仅用于测试中模拟一次服务端递增。 */
  private fun nextVersion(version: ULong): ULong = if (version == 0uL) 1uL else version + 1uL

  /** 空响应严格带齐三类 block，满足 ResponseApplier 的关联合同。 */
  private fun emptySyncResponse(requestId: String) = SyncResponse(
    requestId,
    CategorySyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
    ScheduleSyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
    OccurrenceOverrideSyncResponse(emptyList(), emptyList(), emptyList(), emptyList()),
    emptyList(),
  )

  /** 生成可直接被 v3 reducer 投影的最小非重复 UI 日程。 */
  private fun schedule(title: String) = Schedule(
    id = ScheduleId(SCHEDULE_ID),
    revision = 0,
    title = title,
    description = "",
    categoryId = CategoryId(CATEGORY_ID),
    timing = ScheduleTiming.Unscheduled,
    recurrence = null,
    reminders = emptyList(),
    todoState = ScheduleTodoState.PENDING,
    createdAt = Instant.fromEpochMilliseconds(1),
    updatedAt = Instant.fromEpochMilliseconds(1),
  )

  /** 复用 reducer 生成后的完整 input，只在 R→U fixture 中提供旧响应的 canonical Current。 */
  private fun scheduleInput(title: String) = com.cyxbs.pages.schedule.data.remote.v3.ScheduleInput(
    id = SCHEDULE_ID,
    version = 0u,
    kind = ScheduleKind.TODO,
    title = com.cyxbs.pages.schedule.data.remote.v3.AtomicField(title, 100),
    description = com.cyxbs.pages.schedule.data.remote.v3.AtomicField("", 100),
    categoryId = AtomicField(CATEGORY_ID, 100),
    timing = AtomicField(
      TimingInput(TimingKind.UNSCHEDULED),
      100,
    ),
    recurrence = AtomicField(null, 100),
    reminders = AtomicField(emptyList(), 100),
    todoState = AtomicField(
      TodoState.OPEN,
      100,
    ),
    linkedToCourse = AtomicField(false, 100),
  )

  /** 预置服务端已确认分类，保持 reducer 的“每条日程必须归类”业务约束。 */
  private suspend fun seedRemoteCategory(database: ScheduleRoomDatabase) {
    ScheduleV2RoomStateStore(database).replaceAccountState(
      ACCOUNT,
      categories = listOf(
        ScheduleV2CategoryStateEntity(
          accountId = ACCOUNT,
          categoryId = CATEGORY_ID,
          remoteSnapshot = CategoryCurrent(
            CategoryInput(
              CATEGORY_ID,
              1u,
              AtomicField("默认分类", 1),
              AtomicField(null, 1),
              AtomicField(0, 1),
            ),
            ServerResourceMeta(1, 1),
          ),
          pendingOperation = null,
          pendingSnapshot = null,
          pendingLocalModifiedAt = null,
          localRevision = null,
          localBatchId = null,
        ),
      ),
      schedules = emptyList(),
      occurrenceOverrides = emptyList(),
    )
  }

  /** 预置有 remote Override 的重复日程，删除时 reducer 必须生成同一 atomic batch，而非调用日常 DELETE。 */
  private suspend fun seedRecurringScheduleAndOverride(database: ScheduleRoomDatabase) {
    val recurring = scheduleInput("重复日程").copy(
      version = 1u,
      timing = AtomicField(TimingInput(TimingKind.ALL_DAY, startAt = OCCURRENCE_DATE, endAt = OCCURRENCE_DATE + DAY_MILLIS), 1),
      recurrence = AtomicField(RecurrenceInput(RecurrenceFrequency.DAILY, 1, OCCURRENCE_DATE, weekdays = emptyList()), 1),
      todoState = AtomicField(TodoState.OPEN, 1),
    )
    ScheduleV2RoomStateStore(database).replaceAccountState(
      ACCOUNT,
      categories = ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).categories,
      schedules = listOf(
        ScheduleV2ScheduleStateEntity(
          ACCOUNT,
          SCHEDULE_ID,
          ScheduleCurrent(recurring, ServerResourceMeta(1, 1), OCCURRENCE_DATE),
          null,
          null,
          null,
          null,
          null,
        ),
      ),
      occurrenceOverrides = listOf(
        ScheduleV2OccurrenceOverrideStateEntity(
          ACCOUNT,
          SCHEDULE_ID,
          OCCURRENCE_DATE,
          OccurrenceOverrideCurrent(
            OccurrenceOverrideInput(
              scheduleId = SCHEDULE_ID,
              occurrenceDate = OCCURRENCE_DATE,
              version = 1u,
              status = AtomicField(OccurrenceStatus.ACTIVE, 1),
              timing = AtomicField(FieldPatch<TimingInput>(PatchMode.INHERIT), 1),
              title = AtomicField(FieldPatch<String>(PatchMode.INHERIT), 1),
              description = AtomicField(FieldPatch<String>(PatchMode.INHERIT), 1),
              categoryId = AtomicField(FieldPatch<String>(PatchMode.INHERIT), 1),
              reminders = AtomicField(FieldPatch<List<ReminderInput>>(PatchMode.INHERIT), 1),
            ),
            ServerResourceMeta(1, 1),
          ),
          null,
          null,
          null,
          null,
          null,
        ),
      ),
    )
  }

  private companion object {
    const val ACCOUNT = "repository-account"
    const val CATEGORY_ID = "category-1"
    const val SECOND_CATEGORY_ID = "category-2"
    const val SCHEDULE_ID = "0197f000-0000-7000-8000-000000000001"
    const val OCCURRENCE_DATE = 86_400_000L
    const val DAY_MILLIS = 86_400_000L
  }
}
