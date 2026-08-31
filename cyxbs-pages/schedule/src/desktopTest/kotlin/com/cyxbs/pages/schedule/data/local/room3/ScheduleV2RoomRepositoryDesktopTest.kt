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
import com.cyxbs.pages.schedule.data.remote.v3.AtomicField
import com.cyxbs.pages.schedule.data.remote.v3.CategoryConfirmedResult
import com.cyxbs.pages.schedule.data.remote.v3.CategoryCurrent
import com.cyxbs.pages.schedule.data.remote.v3.CategoryDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.CategoryInput
import com.cyxbs.pages.schedule.data.remote.v3.CategoryMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.CategoryMutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.CategorySyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.CategoryTombstone
import com.cyxbs.pages.schedule.data.remote.v3.CategoryUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.ConfirmedResultCode
import com.cyxbs.pages.schedule.data.remote.v3.FieldPatch
import com.cyxbs.pages.schedule.data.remote.v3.MutationResultCode
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideCurrent
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideConfirmedResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideInput
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideMutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideSyncResponse
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideTombstone
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideUpsertResult
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceStatus
import com.cyxbs.pages.schedule.data.remote.v3.PatchMode
import com.cyxbs.pages.schedule.data.remote.v3.RecurrenceFrequency
import com.cyxbs.pages.schedule.data.remote.v3.RecurrenceInput
import com.cyxbs.pages.schedule.data.remote.v3.ReminderInput
import com.cyxbs.pages.schedule.data.remote.v3.ResultReason
import com.cyxbs.pages.schedule.data.remote.v3.MutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.MutationResponse
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleCurrent
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleConfirmedResult
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleDeleteResult
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleKind
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleMutationRequest
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleMutationResponse
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
import kotlin.test.assertNull
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
        sourceRequest = MutationRequest(
          requestId = "failure-request",
          categories = CategoryMutationRequest(emptyList(), emptyList()),
          schedules = ScheduleMutationRequest(listOf(source), emptyList()),
          occurrenceOverrides = OccurrenceOverrideMutationRequest(emptyList(), emptyList()),
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

  /** 账号清空会同时删除 Room 全状态、revision 元数据、失败记录并发布空快照。 */
  @Test
  fun clearLocalAccountDataRemovesAllLocalState() = runTest {
    withRepositoryAndFailures { repository, _, database, failureRecords ->
      repository.initialize()
      val source = scheduleInput("待清空")
      repository.execute(ScheduleCommand.Create(schedule("待清空")))
      seedRecurringScheduleAndOverride(database)
      failureRecords.record(
        ACCOUNT,
        listOf(
          ScheduleFailureRecord(
            scheduleId = source.id,
            failedAt = 100L,
            operation = ScheduleFailureOperation.CREATE,
            reasonCode = "TEST",
            message = "测试失败",
            sourceSchedule = source,
            sourceRequest = MutationRequest(
              requestId = "clear-local-test",
              categories = CategoryMutationRequest(emptyList(), emptyList()),
              schedules = ScheduleMutationRequest(listOf(source), emptyList()),
              occurrenceOverrides = OccurrenceOverrideMutationRequest(emptyList(), emptyList()),
            ),
          ),
        ),
      )

      val stateStore = ScheduleV2RoomStateStore(database)
      val stateBeforeClear = stateStore.readAccountState(ACCOUNT)
      assertTrue(stateBeforeClear.categories.isNotEmpty())
      assertTrue(stateBeforeClear.schedules.isNotEmpty())
      assertTrue(stateBeforeClear.occurrenceOverrides.isNotEmpty())
      assertTrue(failureRecords.observe(ACCOUNT).value.isNotEmpty())

      repository.clearLocalAccountData(ACCOUNT)

      val state = stateStore.readAccountState(ACCOUNT)
      assertTrue(state.categories.isEmpty())
      assertTrue(state.schedules.isEmpty())
      assertTrue(state.occurrenceOverrides.isEmpty())
      assertTrue(repository.snapshot.value.categories.isEmpty())
      assertTrue(repository.snapshot.value.schedules.isEmpty())
      assertTrue(failureRecords.observe(ACCOUNT).value.isEmpty())
      assertEquals(1L, stateStore.allocateLocalRevision(ACCOUNT))
    }
  }

  /** 未分组日程必须经过 Room、日常请求和 canonical 回包完整往返，不能停在 LOCAL rejected。 */
  @Test
  fun uncategorizedCreateIsSentAndRoundTripsThroughRoom() = runTest {
    withRepository { repository, gateway, database ->
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Create(schedule("未分组", categoryId = null)))

      assertIs<ScheduleSyncResult.Success>(result)
      assertNull(gateway.firstCreatedRequest?.schedules?.upserts?.single()?.categoryId?.data)
      assertNull(repository.snapshot.value.schedules.single().categoryId)
      val stored = ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT).schedules.single()
      assertNull(stored.remoteSnapshot?.resource?.categoryId?.data)
      assertNull(stored.localRevision)
    }
  }

  @Test
  fun dailyRejectedKeepsPendingAndCreatesFailureRecord() = runTest {
    withRepositoryAndFailures { repository, gateway, database, failureRecords ->
      gateway.createResponder = { request ->
        ScheduleV2CallResult.Completed(
          ApiWrapper(
            rejectedMutationResult(request, ResultReason.RESOURCE_CHANGED),
            10000,
            "rejected",
          ),
        )
      }
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Create(schedule("拒绝")))

      val failure = assertIs<ScheduleSyncResult.Failure>(result)
      assertIs<ScheduleRemoteError.MutationRejected>(failure.error)
      val status = assertIs<ScheduleRepositoryStatus.Ready>(repository.snapshot.value.status)
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

  /** 首次使用新分类保存日程时，两种资源必须进入同一个普通请求并分别收敛 canonical 状态。 */
  @Test
  fun saveScheduleWithNewCategorySendsAndCommitsBothResources() = runTest {
    withRepository { repository, gateway, database ->
      repository.initialize()
      val category = ScheduleCategory(CategoryId(SECOND_CATEGORY_ID), 0, "新分类", null, 1)
      val schedule = schedule("同次保存", category.id)

      val result = repository.execute(ScheduleCommand.SaveScheduleWithNewCategory(category, schedule))

      assertIs<ScheduleSyncResult.Success>(result)
      val request = requireNotNull(gateway.firstCreatedRequest)
      assertEquals(listOf(SECOND_CATEGORY_ID), request.categories.upserts.map { it.id })
      assertEquals(listOf(SCHEDULE_ID), request.schedules.upserts.map { it.id })
      val state = ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT)
      assertNull(state.categories.first { it.categoryId == SECOND_CATEGORY_ID }.localRevision)
      assertNull(state.schedules.single().localRevision)
      assertEquals(SECOND_CATEGORY_ID, state.schedules.single().remoteSnapshot?.resource?.categoryId?.data)
    }
  }

  /**
   * 同次请求允许资源级部分成功：已创建分类立即确认，失败日程保留 pending，失败页只记录对应主日程。
   */
  @Test
  fun saveScheduleWithNewCategoryKeepsOnlyRejectedSchedulePending() = runTest {
    withRepositoryAndFailures { repository, gateway, database, failureRecords ->
      gateway.createResponder = { request ->
        val applied = appliedMutationResult(request)
        ScheduleV2CallResult.Completed(ApiWrapper(
          applied.copy(
            schedules = ScheduleMutationResponse(
              upsertResults = request.schedules.upserts.map {
                ScheduleUpsertResult(
                  id = it.id,
                  result = MutationResultCode.REJECTED,
                  reason = ResultReason.INVALID_REQUEST,
                  info = "schedule.title is invalid",
                )
              },
              deleteResults = emptyList(),
            ),
          ),
          10000,
          "partially applied",
        ))
      }
      repository.initialize()
      val category = ScheduleCategory(CategoryId(SECOND_CATEGORY_ID), 0, "可用分类", null, 1)
      val schedule = schedule("被拒绝日程", category.id)

      val result = repository.execute(ScheduleCommand.SaveScheduleWithNewCategory(category, schedule))

      assertIs<ScheduleSyncResult.Failure>(result)
      val state = ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT)
      assertNull(state.categories.first { it.categoryId == SECOND_CATEGORY_ID }.localRevision)
      assertEquals(1L, state.schedules.single().localRevision)
      val failure = failureRecords.observe(ACCOUNT).value.single()
      assertEquals(SCHEDULE_ID, failure.scheduleId)
      assertEquals("INVALID_REQUEST", failure.reasonCode)
      assertEquals("schedule.title is invalid", failure.message)
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
      val firstResponse = CompletableDeferred<ScheduleV2CallResult<MutationResponse>>()
      gateway.createResponder = {
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
            appliedMutationResult(request = gateway.firstCreatedRequest!!),
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
        val response = emptySyncResponse(request)
        response.copy(
          categories = response.categories.copy(
            upsertResults = listOf(
              CategoryUpsertResult(
                SECOND_CATEGORY_ID,
                MutationResultCode.REJECTED,
                ResultReason.CATEGORY_NOT_FOUND,
              ),
            ),
          ),
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
        val response = emptySyncResponse(request)
        response.copy(
          schedules = response.schedules.copy(
            upsertResults = listOf(
              ScheduleUpsertResult(
                SCHEDULE_ID,
                MutationResultCode.REJECTED,
                ResultReason.INVALID_REQUEST
              )
            ),
          ),
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
      assertEquals("新标题", record.sourceRequest.schedules.upserts.single().title.data)
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
      gateway.createResponder = { request ->
        ScheduleV2CallResult.Completed(
          ApiWrapper(rejectedMutationResult(request, ResultReason.INVALID_REQUEST), 10000, "rejected"),
        )
      }
      repository.initialize()
      assertIs<ScheduleSyncResult.Failure>(repository.execute(ScheduleCommand.Create(schedule("待修复"))))
      assertEquals(1, failureRecords.observe(ACCOUNT).value.size)

      gateway.createResponder = { request ->
        ScheduleV2CallResult.Completed(ApiWrapper(appliedMutationResult(request), 10000, "ok"))
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
      val request = requireNotNull(gateway.lastDeletedRequest)

      assertIs<ScheduleSyncResult.Success>(result)
      assertEquals(1, gateway.deleteCalls)
      assertEquals(listOf(CATEGORY_ID), request.categories.deletes.map { it.id })
      assertTrue(request.categories.upserts.isEmpty())
      assertTrue(request.schedules.upserts.isEmpty())
      assertTrue(request.schedules.deletes.isEmpty())
      assertTrue(request.occurrenceOverrides.upserts.isEmpty())
      assertTrue(request.occurrenceOverrides.deletes.isEmpty())
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
  fun scheduleDeleteCarriesOverrideInSameDailyRequest() = runTest {
    withRepository { repository, gateway, database ->
      seedRecurringScheduleAndOverride(database)
      repository.initialize()

      val result = repository.execute(ScheduleCommand.Delete(ScheduleId(SCHEDULE_ID)))
      val state = ScheduleV2RoomStateStore(database).readAccountState(ACCOUNT)

      assertIs<ScheduleSyncResult.Success>(result)
      assertEquals(1, gateway.deleteCalls)
      assertEquals(1, gateway.lastDeletedRequest?.schedules?.deletes?.size)
      assertEquals(1, gateway.lastDeletedRequest?.occurrenceOverrides?.deletes?.size)
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

    override fun refreshSources(accountId: String, request: MutationRequest) {
      val state = records.getOrPut(accountId) { MutableStateFlow(emptyList()) }
      val affectedIds = request.scheduleIds()
      val sources = request.schedules.upserts.associateBy { it.id }
      state.value = state.value.map { record ->
        if (record.scheduleId !in affectedIds) record
        else record.copy(
          sourceSchedule = sources[record.scheduleId] ?: record.sourceSchedule,
          sourceRequest = request,
        )
      }
    }

    override fun remove(accountId: String, scheduleIds: Set<String>) {
      val state = records.getOrPut(accountId) { MutableStateFlow(emptyList()) }
      state.value = state.value.filterNot { it.scheduleId in scheduleIds }
    }

    override fun clear(accountId: String) {
      records.getOrPut(accountId) { MutableStateFlow(emptyList()) }.value = emptyList()
    }
  }

  /** 最小 gateway recorder：Sync 返回空 inventory，日常 create 默认回显 canonical Current。 */
  private inner class FakeGateway : ScheduleV2RepositoryGateway {
    var createCalls = 0
    var deleteCalls = 0
    var firstCreatedRequest: MutationRequest? = null
    var lastDeletedRequest: MutationRequest? = null
    var syncResponder: (SyncRequest) -> SyncResponse = { request -> emptySyncResponse(request) }
    var createResponder: suspend (MutationRequest) -> ScheduleV2CallResult<MutationResponse> = { request ->
      ScheduleV2CallResult.Completed(ApiWrapper(appliedMutationResult(request), 10000, "ok"))
    }
    var deleteResponder: suspend (MutationRequest) -> ScheduleV2CallResult<MutationResponse> = createResponder

    override suspend fun sync(accountId: String, request: SyncRequest): ScheduleV2CallResult<SyncResponse> =
      ScheduleV2CallResult.Completed(ApiWrapper(syncResponder(request), 10000, "ok"))

    override suspend fun createSchedule(accountId: String, input: MutationRequest): ScheduleV2CallResult<MutationResponse> {
      createCalls += 1
      if (firstCreatedRequest == null) firstCreatedRequest = input
      return createResponder(input)
    }

    override suspend fun updateSchedule(accountId: String, input: MutationRequest): ScheduleV2CallResult<MutationResponse> {
      return createResponder(input)
    }

    override suspend fun deleteSchedule(accountId: String, input: MutationRequest): ScheduleV2CallResult<MutationResponse> {
      deleteCalls += 1
      lastDeletedRequest = input
      return deleteResponder(input)
    }
  }

  /** 构造与请求逐项对齐的成功结果，模拟服务端独立处理每个资源。 */
  private fun appliedMutationResult(request: MutationRequest) = MutationResponse(
    requestId = request.requestId,
    categories = CategoryMutationResponse(
      upsertResults = request.categories.upserts.map {
        CategoryUpsertResult(
          id = it.id,
          result = if (it.version == 0uL) MutationResultCode.CREATED else MutationResultCode.APPLIED,
          current = CategoryCurrent(it.copy(version = nextVersion(it.version)), ServerResourceMeta(1, 2)),
        )
      },
      deleteResults = request.categories.deletes.map {
        CategoryDeleteResult(it.id, MutationResultCode.DELETED, tombstone = CategoryTombstone(it.id, 2))
      },
    ),
    schedules = ScheduleMutationResponse(
      upsertResults = request.schedules.upserts.map {
        ScheduleUpsertResult(
          id = it.id,
          result = if (it.version == 0uL) MutationResultCode.CREATED else MutationResultCode.APPLIED,
          current = ScheduleCurrent(it.copy(version = nextVersion(it.version)), ServerResourceMeta(1, 2)),
        )
      },
      deleteResults = request.schedules.deletes.map {
        ScheduleDeleteResult(it.id, MutationResultCode.DELETED, tombstone = ScheduleTombstone(it.id, 2))
      },
    ),
    occurrenceOverrides = OccurrenceOverrideMutationResponse(
      upsertResults = request.occurrenceOverrides.upserts.map {
        OccurrenceOverrideUpsertResult(
          scheduleId = it.scheduleId,
          occurrenceDate = it.occurrenceDate,
          result = if (it.version == 0uL) MutationResultCode.CREATED else MutationResultCode.APPLIED,
          current = OccurrenceOverrideCurrent(
            it.copy(version = nextVersion(it.version)),
            ServerResourceMeta(1, 2),
          ),
        )
      },
      deleteResults = request.occurrenceOverrides.deletes.map {
        OccurrenceOverrideDeleteResult(
          scheduleId = it.scheduleId,
          occurrenceDate = it.occurrenceDate,
          result = MutationResultCode.DELETED,
          tombstone = OccurrenceOverrideTombstone(it.scheduleId, it.occurrenceDate, 2),
        )
      },
    ),
  )

  /** 构造完整对齐的业务拒绝结果；服务端不返回任何伪 canonical 快照。 */
  private fun rejectedMutationResult(request: MutationRequest, reason: ResultReason) = MutationResponse(
    requestId = request.requestId,
    categories = CategoryMutationResponse(
      request.categories.upserts.map { CategoryUpsertResult(it.id, MutationResultCode.REJECTED, reason) },
      request.categories.deletes.map { CategoryDeleteResult(it.id, MutationResultCode.REJECTED, reason) },
    ),
    schedules = ScheduleMutationResponse(
      request.schedules.upserts.map { ScheduleUpsertResult(it.id, MutationResultCode.REJECTED, reason) },
      request.schedules.deletes.map { ScheduleDeleteResult(it.id, MutationResultCode.REJECTED, reason) },
    ),
    occurrenceOverrides = OccurrenceOverrideMutationResponse(
      request.occurrenceOverrides.upserts.map {
        OccurrenceOverrideUpsertResult(it.scheduleId, it.occurrenceDate, MutationResultCode.REJECTED, reason)
      },
      request.occurrenceOverrides.deletes.map {
        OccurrenceOverrideDeleteResult(it.scheduleId, it.occurrenceDate, MutationResultCode.REJECTED, reason)
      },
    ),
  )

  /** Create 从 version=0 进入 version=1；后续更新仅用于测试中模拟一次服务端递增。 */
  private fun nextVersion(version: ULong): ULong = if (version == 0uL) 1uL else version + 1uL

  /** 空响应严格带齐三类 block，满足 ResponseApplier 的关联合同。 */
  private fun emptySyncResponse(request: SyncRequest) = SyncResponse(
    request.syncRequestId,
    CategorySyncResponse(
      request.categories.confirmed.map {
        CategoryConfirmedResult(it.id, ConfirmedResultCode.CONFIRMED, it.version)
      },
      emptyList(),
      emptyList(),
      emptyList(),
    ),
    ScheduleSyncResponse(
      request.schedules.confirmed.map {
        ScheduleConfirmedResult(it.id, ConfirmedResultCode.CONFIRMED, it.version)
      },
      emptyList(),
      emptyList(),
      emptyList(),
    ),
    OccurrenceOverrideSyncResponse(
      request.occurrenceOverrides.confirmed.map {
        OccurrenceOverrideConfirmedResult(
          it.scheduleId,
          it.occurrenceDate,
          ConfirmedResultCode.CONFIRMED,
          it.version,
        )
      },
      emptyList(),
      emptyList(),
      emptyList(),
    ),
  )

  /** 生成可直接被 v3 reducer 投影的最小非重复 UI 日程。 */
  private fun schedule(
    title: String,
    categoryId: CategoryId? = CategoryId(CATEGORY_ID),
  ) = Schedule(
    id = ScheduleId(SCHEDULE_ID),
    revision = 0,
    title = title,
    description = "",
    categoryId = categoryId,
    timing = ScheduleTiming.Unscheduled,
    recurrence = null,
    reminder = null,
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
    reminder = AtomicField(null, 100),
    todoState = AtomicField(
      TodoState.OPEN,
      100,
    ),
    linkedToCourse = AtomicField(false, 100),
  )

  /** 预置服务端已确认分类，供默认有分组的测试日程引用。 */
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
        ),
      ),
      schedules = emptyList(),
      occurrenceOverrides = emptyList(),
    )
  }

  /** 预置有 remote Override 的重复日程，删除时 reducer 必须在同一次普通请求中携带两个删除项。 */
  private suspend fun seedRecurringScheduleAndOverride(database: ScheduleRoomDatabase) {
    val recurring = scheduleInput("重复日程").copy(
      version = 1u,
      timing = AtomicField(TimingInput(TimingKind.ALL_DAY, date = OCCURRENCE_DATE), 1),
      recurrence = AtomicField(RecurrenceInput(
        RecurrenceFrequency.DAILY,
        1,
        OCCURRENCE_DATE,
        weekdays = emptyList(),
        monthDays = emptyList(),
        months = emptyList(),
      ), 1),
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
              reminder = AtomicField(FieldPatch<ReminderInput>(PatchMode.INHERIT), 1),
            ),
            ServerResourceMeta(1, 1),
          ),
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
