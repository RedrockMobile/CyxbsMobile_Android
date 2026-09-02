package com.cyxbs.pages.schedule.data.repository.v3

import com.cyxbs.components.config.time.Date
import com.cyxbs.components.config.time.MinuteTimeDate
import com.cyxbs.components.config.time.toLocalDate
import com.cyxbs.components.config.time.toLocalDateTime
import com.cyxbs.pages.schedule.domain.model.CategoryId
import com.cyxbs.pages.schedule.domain.model.FieldPatch as UiFieldPatch
import com.cyxbs.pages.schedule.domain.model.IsoWeekDay
import com.cyxbs.pages.schedule.domain.model.OccurrencePatch
import com.cyxbs.pages.schedule.domain.model.OccurrenceStatus as UiOccurrenceStatus
import com.cyxbs.pages.schedule.domain.model.RecurrenceEnd
import com.cyxbs.pages.schedule.domain.model.RecurrenceFrequency as UiRecurrenceFrequency
import com.cyxbs.pages.schedule.domain.model.RecurrenceId
import com.cyxbs.pages.schedule.domain.model.RecurrenceRule
import com.cyxbs.pages.schedule.domain.model.ReminderChannel
import com.cyxbs.pages.schedule.domain.model.ReminderId
import com.cyxbs.pages.schedule.domain.model.Schedule
import com.cyxbs.pages.schedule.domain.model.ScheduleCategory
import com.cyxbs.pages.schedule.domain.model.ScheduleTodoState
import com.cyxbs.pages.schedule.domain.model.ScheduleId
import com.cyxbs.pages.schedule.domain.model.ScheduleOccurrenceException
import com.cyxbs.pages.schedule.domain.model.ScheduleReminder
import com.cyxbs.pages.schedule.domain.model.ScheduleTiming
import com.cyxbs.pages.schedule.domain.recurrence.RecurrenceEngine
import com.cyxbs.pages.schedule.domain.repository.ScheduleCommand
import com.cyxbs.pages.schedule.domain.sync.v2.AtomicField
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryResource
import com.cyxbs.pages.schedule.domain.sync.v2.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.v2.TodoState
import com.cyxbs.pages.schedule.domain.sync.v2.FieldPatch
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideResource
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceStatus
import com.cyxbs.pages.schedule.domain.sync.v2.PendingDelete
import com.cyxbs.pages.schedule.domain.sync.v2.PendingUpsert
import com.cyxbs.pages.schedule.domain.sync.v2.RecurrenceFrequency
import com.cyxbs.pages.schedule.domain.sync.v2.ReminderInput
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleIdentity
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleKind
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleResource
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.ServerResourceMeta
import com.cyxbs.pages.schedule.domain.sync.v2.TimingInput
import com.cyxbs.pages.schedule.domain.sync.v2.TimingKind
import com.cyxbs.pages.schedule.domain.sync.v2.Weekday
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

/** UI 命令到 v2 typed pending 的纯 reducer 合同测试。 */
class ScheduleV2LocalCommandReducerTest {
  private val reducer = ScheduleV2LocalCommandReducer()

  @Test
  fun createScheduleUsesVersionZeroAndMarksEveryAtomicFieldNow() {
    val now = 10_000L
    val result = reduce(
      command = ScheduleCommand.Create(schedule()),
      now = now,
      revision = 1,
    ).applied()

    val pending = assertIs<PendingUpsert<*, *>>(result.schedules.single().pending)
    val resource = assertIs<ScheduleResource>(pending.resource)
    assertEquals(0, resource.version)
    assertEquals(1, pending.localRevision)
    assertEquals(
      List(8) { now },
      listOf(
        resource.title.modifiedAt,
        resource.description.modifiedAt,
        resource.categoryId.modifiedAt,
        resource.timing.modifiedAt,
        resource.recurrence.modifiedAt,
        resource.reminder.modifiedAt,
        resource.todoState.modifiedAt,
        resource.linkedToCourse.modifiedAt,
      ),
    )
    assertEquals(TimingKind.TIMED, resource.timing.data.kind)
    assertEquals(60 * 60 * 1_000L, resource.timing.data.endAt!! - resource.timing.data.startAt!!)
    assertEquals(ReminderInput(15), resource.reminder.data)
  }

  /** 未分组是可同步的正式状态，创建时应直接生成 categoryId.data=null 的 pending。 */
  @Test
  fun createUncategorizedScheduleCreatesNullableCategoryPending() {
    val result = reduce(
      command = ScheduleCommand.Create(schedule(categoryId = null)),
      now = 10_001,
      revision = 2,
    ).applied()

    val resource = result.schedules.single().pendingResource()
    assertEquals(AtomicField<String?>(null, 10_001), resource.categoryId)
  }

  /** 已确认日程切换到未分组时只推进分类原子的时间，其余字段保持原时间。 */
  @Test
  fun updateScheduleCanClearCategory() {
    val remote = scheduleResource(version = 3, timestamp = 100)
    val state = ScheduleSyncState(
      remote.identity,
      ScheduleRemoteSnapshot(remote, ServerResourceMeta(1, 2)),
    )

    val resource = reduce(
      schedules = listOf(state),
      command = ScheduleCommand.Update(schedule(categoryId = null)),
      now = 200,
      revision = 3,
    ).applied().schedules.single().pendingResource()

    assertEquals(AtomicField<String?>(null, 200), resource.categoryId)
    assertEquals(100, resource.title.modifiedAt)
  }

  /** 惰性默认分类与日程使用同一 localRevision，日常 capture 会将两者放进一次请求。 */
  @Test
  fun saveScheduleWithNewCategoryUsesOneLocalRevision() {
    val category = ScheduleCategory(CategoryId(CATEGORY_ID), 0, "学习", null, 0)
    val completeSchedule = schedule().copy(
      recurrence = RecurrenceRule(UiRecurrenceFrequency.DAILY, end = RecurrenceEnd.Count(3)),
      linkedToCourse = true,
    )
    val result = reduce(
      command = ScheduleCommand.SaveScheduleWithNewCategory(category, completeSchedule),
      now = 20_000,
      revision = 7,
    ).applied()

    val categoryPending = assertIs<PendingUpsert<*, *>>(result.categories.single().pending)
    val schedulePending = assertIs<PendingUpsert<*, *>>(result.schedules.single().pending)
    assertEquals(7, categoryPending.localRevision)
    assertEquals(7, schedulePending.localRevision)
    assertEquals(
      CategoryIdentity(CATEGORY_ID),
      assertIs<CategoryResource>(categoryPending.resource).identity,
    )
    assertEquals(
      CATEGORY_ID,
      assertIs<ScheduleResource>(schedulePending.resource).categoryId.data,
    )
    val scheduleResource = assertIs<ScheduleResource>(schedulePending.resource)
    assertEquals(RecurrenceFrequency.DAILY, scheduleResource.recurrence.data?.frequency)
    assertEquals(ReminderInput(15), scheduleResource.reminder.data)
    assertEquals(true, scheduleResource.linkedToCourse.data)
  }

  /** 分类创建不能用首尾空白或英文字母大小写绕过同名限制。 */
  @Test
  fun createCategoryRejectsDuplicateNormalizedName() {
    val created = reduce(
      command = ScheduleCommand.CreateCategory(
        ScheduleCategory(CategoryId(CATEGORY_ID), 0, "Study", null, 0),
      ),
      revision = 1,
    ).applied()

    assertEquals(
      ScheduleV2LocalCommandResult.Rejected(ScheduleV2LocalCommandRejectionReason.INVALID_STATE),
      reduce(
        categories = created.categories,
        command = ScheduleCommand.CreateCategory(
          ScheduleCategory(
            CategoryId("019d0000-0000-7000-8000-000000000099"),
            0,
            " study ",
            null,
            1,
          ),
        ),
        revision = 2,
      ),
    )
  }

  @Test
  fun updateOnlyChangesTitleTimestampAndOverwritesOlderPendingRevision() {
    val remote = scheduleResource(version = 7, title = "remote", timestamp = 100)
    val currentR = remote.copy(title = AtomicField("R", 110))
    val state = ScheduleSyncState(
      identity = remote.identity,
      remoteSnapshot = ScheduleRemoteSnapshot(remote, ServerResourceMeta(1, 2)),
      pending = PendingUpsert(currentR, localRevision = 10),
    )
    val commandSchedule = schedule(title = "U", oldRevision = 9_999)

    val result = reduce(
      schedules = listOf(state),
      command = ScheduleCommand.Update(commandSchedule),
      now = 200,
      revision = 11,
    ).applied()

    val pending = assertIs<PendingUpsert<*, *>>(result.schedules.single().pending)
    val resource = assertIs<ScheduleResource>(pending.resource)
    assertEquals(7, resource.version)
    assertEquals(11, pending.localRevision)
    assertEquals(AtomicField("U", 200), resource.title)
    assertEquals(currentR.description, resource.description)
    assertEquals(currentR.categoryId, resource.categoryId)
    assertEquals(currentR.timing, resource.timing)
    assertEquals(currentR.recurrence, resource.recurrence)
    assertEquals(currentR.reminder, resource.reminder)
    assertEquals(currentR.todoState, resource.todoState)
    assertEquals(currentR.linkedToCourse, resource.linkedToCourse)
  }

  /** 取消重复后再换日期重新启用重复，必须以当前日期建立新的 occurrence identity 日期轴。 */
  @Test
  fun reEnablingRecurrenceUsesCurrentTimingAfterClear() {
    val historicalAnchor = Date(2026, 7, 20)
    val currentDate = Date(2026, 7, 21)
    val remote = scheduleResource(version = 7, timestamp = 100)
    val state = ScheduleSyncState(
      identity = remote.identity,
      remoteSnapshot = ScheduleRemoteSnapshot(
        remote,
        ServerResourceMeta(1, 2),
        firstRecurrenceAnchorDate = historicalAnchor.utcDaySlot(),
      ),
    )
    val update = schedule(
      timing = ScheduleTiming.Timed(
        MinuteTimeDate(currentDate, 11, 0),
        durationMinutes = 30,
        timeZoneId = "Asia/Shanghai",
      ),
      recurrence = RecurrenceRule(UiRecurrenceFrequency.WEEKLY),
    ).copy(recurrenceAnchorDate = historicalAnchor)

    val result = reduce(
      schedules = listOf(state),
      command = ScheduleCommand.Update(update),
      now = 200,
      revision = 2,
    ).applied()
    val resource = result.schedules.single().pendingResource()

    assertEquals(currentDate.utcDaySlot(), resource.recurrence.data?.anchorDate)
    assertIs<ScheduleV2SnapshotProjection.Success>(
      ScheduleV2SnapshotProjector().project(
        accountId = "2020214988",
        timeZone = TimeZone.of("Asia/Shanghai"),
        categories = emptyList(),
        schedules = result.schedules,
        occurrenceOverrides = emptyList(),
      ),
    )
  }

  /** 清空备注、分类和提醒是三个明确的字段值变化，不得误改标题、时间和完成态。 */
  @Test
  fun updateCanClearNullableFieldsWithoutTouchingOtherAtoms() {
    val remote = scheduleResource(version = 7, timestamp = 100)
    val state = ScheduleSyncState(
      remote.identity,
      ScheduleRemoteSnapshot(remote, ServerResourceMeta(1, 2)),
    )
    val cleared = schedule(categoryId = null, reminder = null).copy(description = "")

    val resource = reduce(
      schedules = listOf(state),
      command = ScheduleCommand.Update(cleared),
      now = 200,
      revision = 2,
    ).applied().schedules.single().pendingResource()

    assertEquals(AtomicField("", 200), resource.description)
    assertEquals(AtomicField<String?>(null, 200), resource.categoryId)
    assertEquals(AtomicField<ReminderInput?>(null, 200), resource.reminder)
    assertEquals(remote.title, resource.title)
    assertEquals(remote.timing, resource.timing)
    assertEquals(remote.todoState, resource.todoState)
  }

  /** 编辑已完成普通清单的内容时，完成态不是表单脏字段，必须保留原值和时间戳。 */
  @Test
  fun editingCompletedScheduleKeepsCompletionState() {
    val remote = scheduleResource(version = 7, timestamp = 100).copy(
      todoState = AtomicField(TodoState.COMPLETED, 120),
    )
    val state = ScheduleSyncState(
      remote.identity,
      ScheduleRemoteSnapshot(remote, ServerResourceMeta(1, 2)),
    )
    val edited = schedule(title = "新标题").copy(
      description = "新备注",
      todoState = ScheduleTodoState.COMPLETED,
    )

    val resource = reduce(
      schedules = listOf(state),
      command = ScheduleCommand.Update(edited),
      now = 200,
      revision = 2,
    ).applied().schedules.single().pendingResource()

    assertEquals(AtomicField<TodoState?>(TodoState.COMPLETED, 120), resource.todoState)
    assertEquals(AtomicField("新标题", 200), resource.title)
    assertEquals(AtomicField("新备注", 200), resource.description)
  }

  /**
   * 整系列修改墙上时间后，wire 中按日期保存的单次移动例外应使用父系列新时刻恢复 identity。
   * 该测试覆盖 reducer、双快照投影和重复展开的真实链路，防止把旧 UI identity 直接复用而产生重复实例。
   */
  @Test
  fun parentTimeShiftRehydratesMovedOverrideWithNewIdentityTime() {
    val recurrence = RecurrenceRule(UiRecurrenceFrequency.DAILY, end = RecurrenceEnd.Count(3))
    val parent = schedule(categoryId = null, recurrence = recurrence, reminder = null)
    val created = reduce(
      command = ScheduleCommand.Create(parent),
      revision = 1,
    ).applied()
    val movedTiming = ScheduleTiming.Timed(
      MinuteTimeDate(2026, 7, 30, 15, 0),
      durationMinutes = 60,
      timeZoneId = "Asia/Shanghai",
    )
    val withMovedOccurrence = reduce(
      schedules = created.schedules,
      command = ScheduleCommand.UpsertOccurrenceException(exception(
        recurrenceId = RecurrenceId(
          MinuteTimeDate(2026, 7, 21, 9, 0),
          "Asia/Shanghai",
          allDay = false,
        ),
        patch = OccurrencePatch(timing = UiFieldPatch.Replace(movedTiming)),
      )),
      revision = 2,
    ).applied()
    val shiftedParent = parent.copy(
      timing = ScheduleTiming.Timed(
        MinuteTimeDate(2026, 7, 20, 10, 0),
        durationMinutes = 60,
        timeZoneId = "Asia/Shanghai",
      ),
    )
    val shifted = reduce(
      schedules = withMovedOccurrence.schedules,
      occurrenceOverrides = withMovedOccurrence.occurrenceOverrides,
      command = ScheduleCommand.Update(shiftedParent),
      revision = 3,
    ).applied()

    val projection = assertIs<ScheduleV2SnapshotProjection.Success>(
      ScheduleV2SnapshotProjector().project(
        accountId = "test-account",
        timeZone = TimeZone.of("Asia/Shanghai"),
        categories = shifted.categories,
        schedules = shifted.schedules,
        occurrenceOverrides = shifted.occurrenceOverrides,
      ),
    ).snapshot
    val restoredException = projection.exceptions.single()
    assertEquals(
      MinuteTimeDate(2026, 7, 21, 10, 0),
      restoredException.recurrenceId.originalDateTime,
    )

    val occurrences = RecurrenceEngine.expandInRange(
      schedule = projection.schedules.single(),
      exceptions = projection.exceptions,
      rangeStartInclusive = MinuteTimeDate(2026, 7, 20, 0, 0),
      rangeEndExclusive = MinuteTimeDate(2026, 8, 1, 0, 0),
    )
    assertEquals(3, occurrences.size)
    assertEquals(3, occurrences.map { it.recurrenceId }.toSet().size)
    assertTrue(occurrences.single { it.recurrenceId == restoredException.recurrenceId }.timing == movedTiming)
  }

  @Test
  fun staleLocalRevisionIsRejectedInsteadOfThrowing() {
    val resource = scheduleResource(version = 7, title = "R", timestamp = 100)
    val state = ScheduleSyncState(
      identity = resource.identity,
      remoteSnapshot = ScheduleRemoteSnapshot(resource, ServerResourceMeta(1, 2)),
      pending = PendingUpsert(resource, localRevision = 10),
    )

    val result = reduce(
      schedules = listOf(state),
      command = ScheduleCommand.Update(schedule(title = "U")),
      now = 200,
      revision = 10,
    )

    assertEquals(
      ScheduleV2LocalCommandResult.Rejected(ScheduleV2LocalCommandRejectionReason.INVALID_STATE),
      result,
    )
  }

  @Test
  fun deleteCreatesVersionlessPendingDelete() {
    val resource = scheduleResource(version = 7)
    val state = ScheduleSyncState(
      identity = resource.identity,
      remoteSnapshot = ScheduleRemoteSnapshot(resource, ServerResourceMeta(1, 2)),
    )

    val result = reduce(
      schedules = listOf(state),
      command = ScheduleCommand.Delete(ScheduleId(SCHEDULE_ID)),
      now = 300,
      revision = 4,
    ).applied()

    val pending = assertIs<PendingDelete<*, *>>(result.schedules.single().pending)
    assertEquals(ScheduleIdentity(SCHEDULE_ID), pending.identity)
    assertEquals(300, pending.localModifiedAt)
    assertEquals(4, pending.localRevision)
  }

  @Test
  fun deleteScheduleWithLiveOverridesUsesOneRevision() {
    val parent = scheduleResource(version = 7)
    val parentState = ScheduleSyncState(
      identity = parent.identity,
      remoteSnapshot = ScheduleRemoteSnapshot(parent, ServerResourceMeta(1, 2)),
    )
    val firstId = RecurrenceId(MinuteTimeDate(2026, 7, 23, 9, 30), "Asia/Shanghai", false)
    val secondId = RecurrenceId(MinuteTimeDate(2026, 7, 24, 9, 30), "Asia/Shanghai", false)
    val thirdId = RecurrenceId(MinuteTimeDate(2026, 7, 25, 9, 30), "Asia/Shanghai", false)
    val first = reduce(
      command = ScheduleCommand.UpsertOccurrenceException(exception(firstId, OccurrencePatch())),
      revision = 1,
    ).applied()
    val pendingCreateChildren = reduce(
      occurrenceOverrides = first.occurrenceOverrides,
      command = ScheduleCommand.UpsertOccurrenceException(exception(secondId, OccurrencePatch())),
      revision = 2,
    ).applied().occurrenceOverrides
    val thirdCreate = reduce(
      command = ScheduleCommand.UpsertOccurrenceException(exception(thirdId, OccurrencePatch())),
      revision = 1,
    ).applied().occurrenceOverrides.single()
    val thirdResource = assertIs<PendingUpsert<*, *>>(thirdCreate.pending).resource
    val remoteThirdResource = assertIs<OccurrenceOverrideResource>(thirdResource).copy(version = 5)
    val remotelyLiveDeletingChild = OccurrenceOverrideSyncState(
      identity = remoteThirdResource.identity,
      remoteSnapshot = OccurrenceOverrideRemoteSnapshot(
        remoteThirdResource,
        ServerResourceMeta(createdAt = 1, remoteModifiedAt = 2),
      ),
      pending = PendingDelete(
        identity = remoteThirdResource.identity,
        localModifiedAt = 250,
        localRevision = 2,
      ),
    )
    val children = pendingCreateChildren + remotelyLiveDeletingChild

    val deleted = reduce(
      schedules = listOf(parentState),
      occurrenceOverrides = children,
      command = ScheduleCommand.Delete(ScheduleId(SCHEDULE_ID)),
      now = 300,
      revision = 3,
    ).applied()

    val parentDelete = assertIs<PendingDelete<*, *>>(deleted.schedules.single().pending)
    assertEquals(3, parentDelete.localRevision)
    assertEquals(setOf(3L), deleted.occurrenceOverrides.map { state ->
      assertIs<PendingDelete<*, *>>(state.pending).localRevision
    }.toSet())

    val capture = ScheduleV2RequestPlanner().capture(
      syncRequestId = "sync-delete-series",
      categories = emptyList(),
      schedules = deleted.schedules,
      occurrenceOverrides = deleted.occurrenceOverrides,
    )
    assertEquals(1, capture.request.schedules.deletes.size)
    assertEquals(1, capture.request.occurrenceOverrides.deletes.size)
  }

  @Test
  fun dailyAndWeeklyRecurrenceUseUtcDateSlots() {
    val daily = schedule(
      recurrence = RecurrenceRule(
        frequency = UiRecurrenceFrequency.DAILY,
        interval = 2,
        end = RecurrenceEnd.Until(Date(2026, 7, 30)),
      ),
    )
    val weekly = schedule(
      id = SCHEDULE_ID_2,
      timing = ScheduleTiming.AllDay(Date(2026, 7, 21)),
      recurrence = RecurrenceRule(
        frequency = UiRecurrenceFrequency.WEEKLY,
        byWeekDays = setOf(IsoWeekDay.TUESDAY, IsoWeekDay.WEDNESDAY),
        end = RecurrenceEnd.Count(6),
      ),
    )
    val weeklyWithoutExplicitDays = schedule(
      timing = ScheduleTiming.AllDay(Date(2026, 7, 21)),
      recurrence = RecurrenceRule(frequency = UiRecurrenceFrequency.WEEKLY),
    )

    val dailyResource = reduce(
      command = ScheduleCommand.Create(daily),
      revision = 1,
    ).applied().schedules.single().pendingResource()
    val weeklyResource = reduce(
      command = ScheduleCommand.Create(weekly),
      revision = 2,
    ).applied().schedules.single().pendingResource()
    val defaultWeeklyResource = reduce(
      command = ScheduleCommand.Create(weeklyWithoutExplicitDays),
      revision = 3,
    ).applied().schedules.single().pendingResource()

    assertEquals(RecurrenceFrequency.DAILY, dailyResource.recurrence.data?.frequency)
    assertEquals(Date(2026, 7, 20).utcDaySlot(), dailyResource.recurrence.data?.anchorDate)
    assertEquals(Date(2026, 7, 30).utcDaySlot(), dailyResource.recurrence.data?.untilDate)
    assertEquals(RecurrenceFrequency.WEEKLY, weeklyResource.recurrence.data?.frequency)
    assertEquals(Date(2026, 7, 21).utcDaySlot(), weeklyResource.recurrence.data?.anchorDate)
    assertEquals(setOf(Weekday.TU, Weekday.WE), weeklyResource.recurrence.data?.weekdays)
    assertEquals(6, weeklyResource.recurrence.data?.count)
    assertEquals(setOf(Weekday.TU), defaultWeeklyResource.recurrence.data?.weekdays)
  }

  @Test
  fun weeklyRecurrenceAllowsExplicitDaysMissingAnchor() {
    val resource = reduce(
      command = ScheduleCommand.Create(schedule(
        timing = ScheduleTiming.AllDay(Date(2026, 7, 21)),
        recurrence = RecurrenceRule(
          frequency = UiRecurrenceFrequency.WEEKLY,
          byWeekDays = setOf(IsoWeekDay.MONDAY),
        ),
      )),
      revision = 1,
    ).applied().schedules.single().pendingResource()

    assertEquals(setOf(Weekday.MO), resource.recurrence.data?.weekdays)
  }

  /** 月、年重复需要完整映射日期条件，不能再按旧协议拒绝。 */
  @Test
  fun monthlyAndYearlyRecurrenceMapMonthConstraints() {
    val monthly = schedule(recurrence = RecurrenceRule(
      frequency = UiRecurrenceFrequency.MONTHLY,
      byMonthDays = setOf(5, 20),
    ))
    val yearly = schedule(id = SCHEDULE_ID_2, recurrence = RecurrenceRule(
      frequency = UiRecurrenceFrequency.YEARLY,
      byMonthDays = setOf(8),
      byMonths = setOf(3, 9),
    ))

    val monthlyResource = reduce(
      command = ScheduleCommand.Create(monthly),
      revision = 1,
    ).applied().schedules.single().pendingResource()
    val yearlyResource = reduce(
      command = ScheduleCommand.Create(yearly),
      revision = 2,
    ).applied().schedules.single().pendingResource()

    assertEquals(RecurrenceFrequency.MONTHLY, monthlyResource.recurrence.data?.frequency)
    assertEquals(setOf(5, 20), monthlyResource.recurrence.data?.monthDays)
    assertEquals(emptySet(), monthlyResource.recurrence.data?.months)
    assertEquals(RecurrenceFrequency.YEARLY, yearlyResource.recurrence.data?.frequency)
    assertEquals(setOf(8), yearlyResource.recurrence.data?.monthDays)
    assertEquals(setOf(3, 9), yearlyResource.recurrence.data?.months)
  }

  @Test
  fun occurrenceUsesOriginalDateIdentityAndSixAtoms() {
    val recurrenceId = RecurrenceId(
      originalDateTime = MinuteTimeDate(2026, 7, 23, 9, 30),
      timeZoneId = "Asia/Shanghai",
      allDay = false,
    )
    val exception = ScheduleOccurrenceException(
      scheduleId = ScheduleId(SCHEDULE_ID),
      recurrenceId = recurrenceId,
      revision = 8_888,
      status = UiOccurrenceStatus.COMPLETED,
      patch = OccurrencePatch(
        timing = UiFieldPatch.Replace(ScheduleTiming.Timed(
          MinuteTimeDate(2026, 7, 23, 10, 0), 90, "Asia/Shanghai",
        )),
        title = UiFieldPatch.Replace("单次标题"),
        description = UiFieldPatch.Clear,
        categoryId = UiFieldPatch.Replace(CategoryId("category-2")),
        reminder = UiFieldPatch.Replace(deviceReminder()),
      ),
      createdAt = Instant.fromEpochMilliseconds(1),
      updatedAt = Instant.fromEpochMilliseconds(2),
    )

    val applied = reduce(
      command = ScheduleCommand.UpsertOccurrenceException(exception),
      now = 400,
      revision = 5,
    ).applied()
    val state = applied.occurrenceOverrides.single()
    val pending = assertIs<PendingUpsert<*, *>>(state.pending)
    val resource = assertIs<OccurrenceOverrideResource>(pending.resource)

    assertEquals(Date(2026, 7, 23).utcDaySlot(), resource.identity.occurrenceDate)
    assertEquals(SCHEDULE_ID, resource.identity.scheduleId)
    assertEquals(0, resource.version)
    assertEquals(OccurrenceStatus.COMPLETED, resource.status.data)
    assertIs<FieldPatch.Replace<TimingInput>>(resource.timing.data)
    assertEquals(FieldPatch.Replace("单次标题"), resource.title.data)
    assertEquals(FieldPatch.Clear, resource.description.data)
    assertEquals(FieldPatch.Replace("category-2"), resource.categoryId.data)
    assertEquals(FieldPatch.Replace(ReminderInput(15)), resource.reminder.data)
    assertEquals(List(6) { 400L }, listOf(
      resource.status.modifiedAt,
      resource.timing.modifiedAt,
      resource.title.modifiedAt,
      resource.description.modifiedAt,
      resource.categoryId.modifiedAt,
      resource.reminder.modifiedAt,
    ))

    val deleted = reduce(
      occurrenceOverrides = applied.occurrenceOverrides,
      command = ScheduleCommand.DeleteOccurrenceException(ScheduleId(SCHEDULE_ID), recurrenceId),
      now = 401,
      revision = 6,
    ).applied()
    assertTrue(deleted.occurrenceOverrides.isEmpty())
  }

  @Test
  fun categoryCommandsAndCompletionKeepUnchangedFieldTimes() {
    val category = ScheduleCategory(CategoryId(CATEGORY_ID), 900, "学习", null, 1)
    val created = reduce(
      command = ScheduleCommand.CreateCategory(category),
      now = 500,
      revision = 1,
    ).applied()
    val createdCategory = created.categories.single().pendingCategoryResource()
    assertEquals(0, createdCategory.version)
    assertEquals(null, createdCategory.color.data)
    assertEquals(listOf(500L, 500L, 500L), listOf(
      createdCategory.name.modifiedAt,
      createdCategory.color.modifiedAt,
      createdCategory.sortOrder.modifiedAt,
    ))

    val updated = reduce(
      categories = created.categories,
      command = ScheduleCommand.UpdateCategory(category.copy(name = "课程")),
      now = 501,
      revision = 2,
    ).applied()
    val updatedCategory = updated.categories.single().pendingCategoryResource()
    assertEquals(501, updatedCategory.name.modifiedAt)
    assertEquals(500, updatedCategory.color.modifiedAt)
    assertEquals(500, updatedCategory.sortOrder.modifiedAt)

    val recolored = reduce(
      categories = updated.categories,
      command = ScheduleCommand.UpdateCategory(category.copy(name = "课程", color = "color-json")),
      now = 502,
      revision = 3,
    ).applied()
    val recoloredCategory = recolored.categories.single().pendingCategoryResource()
    assertEquals("课程", recoloredCategory.name.data)
    assertEquals("color-json", recoloredCategory.color.data)
    assertEquals(1, recolored.categories.size)

    val deleted = reduce(
      categories = recolored.categories,
      command = ScheduleCommand.DeleteCategory(CategoryId(CATEGORY_ID)),
      now = 503,
      revision = 4,
    ).applied()
    assertIs<PendingDelete<*, *>>(deleted.categories.single().pending)

    val scheduleResource = scheduleResource(version = 4, timestamp = 600)
    val scheduleState = ScheduleSyncState(
      scheduleResource.identity,
      ScheduleRemoteSnapshot(scheduleResource, ServerResourceMeta(1, 2)),
    )
    val completed = reduce(
      schedules = listOf(scheduleState),
      command = ScheduleCommand.CompleteNonRepeating(ScheduleId(SCHEDULE_ID), completed = true),
      now = 601,
      revision = 4,
    ).applied().schedules.single().pendingResource()
    assertEquals(AtomicField<TodoState?>(TodoState.COMPLETED, 601), completed.todoState)
    assertEquals(600, completed.title.modifiedAt)
  }

  /** 一次拖拽把所有变化写入同一 revision，并保持名称、颜色等未修改原子的时间。 */
  @Test
  fun reorderCategoriesUsesOneLocalRevision() {
    val first = ScheduleCategory(CategoryId(CATEGORY_ID), 0, "学习", null, 0)
    val second = ScheduleCategory(CategoryId("category-2"), 0, "生活", "color-json", 1)
    val withFirst = reduce(
      command = ScheduleCommand.CreateCategory(first),
      now = 500,
      revision = 1,
    ).applied()
    val withBoth = reduce(
      categories = withFirst.categories,
      command = ScheduleCommand.CreateCategory(second),
      now = 501,
      revision = 2,
    ).applied()

    val reordered = reduce(
      categories = withBoth.categories,
      command = ScheduleCommand.ReorderCategories(listOf(second, first)),
      now = 502,
      revision = 3,
    ).applied()

    val resources = reordered.categories.associate { state ->
      state.identity.id to state.pendingCategoryResource()
    }
    assertEquals(1, resources.getValue(CATEGORY_ID).sortOrder.data)
    assertEquals(0, resources.getValue("category-2").sortOrder.data)
    assertEquals(500, resources.getValue(CATEGORY_ID).name.modifiedAt)
    assertEquals(501, resources.getValue("category-2").color.modifiedAt)
    assertEquals(setOf(3L), reordered.categories.mapNotNull { it.pending?.localRevision }.toSet())
  }

  @Test
  fun deleteCategoryReferencedByEffectiveScheduleIsRejectedLocally() {
    val category = reduce(
      command = ScheduleCommand.CreateCategory(
        ScheduleCategory(CategoryId(CATEGORY_ID), 900, "学习", null, 1),
      ),
      revision = 1,
    ).applied().categories
    val schedule = scheduleResource(version = 4)
    val scheduleState = ScheduleSyncState(
      schedule.identity,
      ScheduleRemoteSnapshot(schedule, ServerResourceMeta(1, 2)),
    )

    val result = reduce(
      categories = category,
      schedules = listOf(scheduleState),
      command = ScheduleCommand.DeleteCategory(CategoryId(CATEGORY_ID)),
      revision = 2,
    )

    assertEquals(
      ScheduleV2LocalCommandResult.Rejected(ScheduleV2LocalCommandRejectionReason.INVALID_STATE),
      result,
    )

    val deletingSchedule = scheduleState.replacePending(
      PendingDelete(
        identity = schedule.identity,
        localModifiedAt = 1_000,
        localRevision = 1,
      ),
    )
    val hiddenButRemoteStillLive = reduce(
      categories = category,
      schedules = listOf(deletingSchedule),
      command = ScheduleCommand.DeleteCategory(CategoryId(CATEGORY_ID)),
      revision = 2,
    )
    assertEquals(
      ScheduleV2LocalCommandResult.Rejected(ScheduleV2LocalCommandRejectionReason.INVALID_STATE),
      hiddenButRemoteStillLive,
    )
  }

  /** 单次 occurrence 的分类覆盖同样属于引用，不能让分类删除请求走到服务端再失败。 */
  @Test
  fun deleteCategoryReferencedByOccurrenceOverrideIsRejectedLocally() {
    val category = reduce(
      command = ScheduleCommand.CreateCategory(
        ScheduleCategory(CategoryId(CATEGORY_ID), 0, "学习", null, 0),
      ),
      revision = 1,
    ).applied().categories
    val recurrenceId = RecurrenceId(
      MinuteTimeDate(2026, 7, 23, 9, 0), "Asia/Shanghai", false,
    )
    val override = reduce(
      command = ScheduleCommand.UpsertOccurrenceException(exception(
        recurrenceId,
        OccurrencePatch(categoryId = UiFieldPatch.Replace(CategoryId(CATEGORY_ID))),
      )),
      revision = 2,
    ).applied().occurrenceOverrides

    assertEquals(
      ScheduleV2LocalCommandResult.Rejected(ScheduleV2LocalCommandRejectionReason.INVALID_STATE),
      reduce(
        categories = category,
        occurrenceOverrides = override,
        command = ScheduleCommand.DeleteCategory(CategoryId(CATEGORY_ID)),
        revision = 3,
      ),
    )
  }

  @Test
  fun splitSeriesUsesOneBatchAndMovesFutureOverridesToNewIdentity() {
    val recurrence = RecurrenceRule(UiRecurrenceFrequency.DAILY)
    val parent = schedule(recurrence = recurrence)
    val created = reduce(
      command = ScheduleCommand.Create(parent),
      revision = 1,
    ).applied()
    val futureId = RecurrenceId(
      MinuteTimeDate(2026, 7, 23, 9, 0), "Asia/Shanghai", false,
    )
    val futureCreated = reduce(
      schedules = created.schedules,
      command = ScheduleCommand.UpsertOccurrenceException(exception(
        futureId,
        OccurrencePatch(title = UiFieldPatch.Replace("未来例外")),
      )),
      revision = 2,
    ).applied()
    val pendingFuture = futureCreated.occurrenceOverrides.single()
    val futureResource = assertIs<PendingUpsert<*, *>>(pendingFuture.pending)
      .resource.let { assertIs<OccurrenceOverrideResource>(it) }
    val remoteFuture = pendingFuture.copy(
      remoteSnapshot = OccurrenceOverrideRemoteSnapshot(
        futureResource.copy(version = 4),
        ServerResourceMeta(createdAt = 10, remoteModifiedAt = 20),
      ),
      pending = null,
    )
    val boundary = RecurrenceId(
      MinuteTimeDate(2026, 7, 22, 9, 0), "Asia/Shanghai", false,
    )
    val previous = parent.copy(
      recurrence = recurrence.copy(end = RecurrenceEnd.Until(Date(2026, 7, 21))),
    )
    val following = parent.copy(
      id = ScheduleId(SCHEDULE_ID_2),
      revision = 0,
      timing = ScheduleTiming.Timed(
        MinuteTimeDate(2026, 7, 22, 9, 0), 60, "Asia/Shanghai",
      ),
      recurrenceAnchorDate = Date(2026, 7, 22),
    )

    val result = reduce(
      schedules = futureCreated.schedules,
      occurrenceOverrides = listOf(remoteFuture),
      command = ScheduleCommand.SplitSeries(previous, following, boundary),
      revision = 3,
    ).applied()

    assertEquals(2, result.schedules.size)
    assertEquals(setOf(3L), result.schedules.map { it.pending?.localRevision }.toSet())
    val oldOverride = result.occurrenceOverrides.first { it.identity.scheduleId == SCHEDULE_ID }
    assertEquals(3, assertIs<PendingDelete<*, *>>(oldOverride.pending).localRevision)
    val newOverride = result.occurrenceOverrides.first { it.identity.scheduleId == SCHEDULE_ID_2 }
    val newPending = assertIs<PendingUpsert<*, *>>(newOverride.pending)
    assertEquals(3, newPending.localRevision)
    assertEquals(0, assertIs<OccurrenceOverrideResource>(newPending.resource).version)
  }

  @Test
  fun deleteThisAndFollowingTruncatesParentAndDropsLocalOnlyFutureOverride() {
    val recurrence = RecurrenceRule(UiRecurrenceFrequency.DAILY)
    val parent = schedule(recurrence = recurrence)
    val created = reduce(command = ScheduleCommand.Create(parent), revision = 1).applied()
    val futureId = RecurrenceId(
      MinuteTimeDate(2026, 7, 23, 9, 0), "Asia/Shanghai", false,
    )
    val withFuture = reduce(
      schedules = created.schedules,
      command = ScheduleCommand.UpsertOccurrenceException(exception(futureId, OccurrencePatch())),
      revision = 2,
    ).applied()
    val boundary = RecurrenceId(
      MinuteTimeDate(2026, 7, 22, 9, 0), "Asia/Shanghai", false,
    )
    val previous = parent.copy(
      recurrence = recurrence.copy(end = RecurrenceEnd.Until(Date(2026, 7, 21))),
    )

    val result = reduce(
      schedules = withFuture.schedules,
      occurrenceOverrides = withFuture.occurrenceOverrides,
      command = ScheduleCommand.DeleteThisAndFollowing(previous, boundary),
      revision = 3,
    ).applied()

    assertEquals(3, result.schedules.single().pending?.localRevision)
    assertEquals(emptyList(), result.occurrenceOverrides)
  }

  @Test
  fun unsupportedProtocolBoundariesAreRejected() {
    val recurrenceId = RecurrenceId(MinuteTimeDate(2026, 7, 23, 9, 30), "Asia/Shanghai", false)
    val unsupported = listOf(
      ScheduleCommand.Create(schedule(recurrence = RecurrenceRule(
        UiRecurrenceFrequency.DAILY,
        byMonthDays = setOf(20),
      ))),
      ScheduleCommand.Create(schedule(
        reminder = deviceReminder().copy(channel = ReminderChannel.PUSH),
      )),
      ScheduleCommand.Create(schedule(
        timing = ScheduleTiming.Unscheduled,
        recurrence = RecurrenceRule(UiRecurrenceFrequency.DAILY),
      )),
      ScheduleCommand.UpsertOccurrenceException(exception(
        recurrenceId,
        OccurrencePatch(title = UiFieldPatch.Clear),
      )),
    )

    unsupported.forEachIndexed { index, command ->
      assertEquals(
        ScheduleV2LocalCommandResult.Rejected(ScheduleV2LocalCommandRejectionReason.UNSUPPORTED),
        reduce(command = command, revision = index + 1L),
        "unsupported command index=$index",
      )
    }
    assertEquals(ScheduleV2LocalCommandResult.NoOp, reduce(command = ScheduleCommand.RequestSync))
  }

  private fun reduce(
    categories: List<CategorySyncState> = emptyList(),
    schedules: List<ScheduleSyncState> = emptyList(),
    occurrenceOverrides: List<OccurrenceOverrideSyncState> = emptyList(),
    command: ScheduleCommand,
    now: Long = 1_000,
    revision: Long = 1,
  ): ScheduleV2LocalCommandResult = reducer.reduce(
    categories = categories,
    schedules = schedules,
    occurrenceOverrides = occurrenceOverrides,
    command = command,
    nowMillis = now,
    localRevision = revision,
  )

  private fun ScheduleV2LocalCommandResult.applied(): ScheduleV2LocalCommandResult.Applied =
    assertIs(this)

  private fun ScheduleSyncState.pendingResource(): ScheduleResource {
    val pending = assertIs<PendingUpsert<*, *>>(pending)
    return assertIs(pending.resource)
  }

  private fun CategorySyncState.pendingCategoryResource(): CategoryResource {
    val pending = assertIs<PendingUpsert<*, *>>(pending)
    return assertIs(pending.resource)
  }

  private fun schedule(
    id: String = SCHEDULE_ID,
    title: String = "高数",
    categoryId: CategoryId? = CategoryId(CATEGORY_ID),
    timing: ScheduleTiming = ScheduleTiming.Timed(
      MinuteTimeDate(2026, 7, 20, 9, 0),
      durationMinutes = 60,
      timeZoneId = "Asia/Shanghai",
    ),
    recurrence: RecurrenceRule? = null,
    reminder: ScheduleReminder? = deviceReminder(),
    oldRevision: Long = 999,
  ): Schedule = Schedule(
    id = ScheduleId(id),
    revision = oldRevision,
    title = title,
    description = "第三章",
    categoryId = categoryId,
    timing = timing,
    recurrence = recurrence,
    reminder = reminder,
    todoState = ScheduleTodoState.PENDING,
    createdAt = Instant.fromEpochMilliseconds(1),
    updatedAt = Instant.fromEpochMilliseconds(2),
  )

  private fun scheduleResource(
    version: Long,
    title: String = "高数",
    timestamp: Long = 100,
  ): ScheduleResource {
    val start = MinuteTimeDate(2026, 7, 20, 9, 0)
      .toLocalDateTime()
      .toInstant(TimeZone.of("Asia/Shanghai"))
      .toEpochMilliseconds()
    return ScheduleResource(
      identity = ScheduleIdentity(SCHEDULE_ID),
      version = version,
      kind = ScheduleKind.TODO,
      title = AtomicField(title, timestamp),
      description = AtomicField("第三章", timestamp),
      categoryId = AtomicField(CATEGORY_ID, timestamp),
      timing = AtomicField(
        TimingInput(TimingKind.TIMED, startAt = start, endAt = start + 3_600_000),
        timestamp,
      ),
      recurrence = AtomicField(null, timestamp),
      reminder = AtomicField(ReminderInput(15), timestamp),
      todoState = AtomicField(TodoState.OPEN, timestamp),
      linkedToCourse = AtomicField(false, timestamp),
    )
  }

  private fun exception(
    recurrenceId: RecurrenceId,
    patch: OccurrencePatch,
  ): ScheduleOccurrenceException = ScheduleOccurrenceException(
    scheduleId = ScheduleId(SCHEDULE_ID),
    recurrenceId = recurrenceId,
    revision = 999,
    status = UiOccurrenceStatus.ACTIVE,
    patch = patch,
    createdAt = Instant.fromEpochMilliseconds(1),
    updatedAt = Instant.fromEpochMilliseconds(2),
  )

  private fun deviceReminder(): ScheduleReminder = ScheduleReminder(
    id = ReminderId(REMINDER_ID),
    offsetMinutes = 15,
    channel = ReminderChannel.DEVICE,
  )

  private fun Date.utcDaySlot(): Long =
    toLocalDate().atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

  private companion object {
    const val SCHEDULE_ID = "0197f000-0000-7000-8000-000000000001"
    const val SCHEDULE_ID_2 = "0197f000-0000-7000-8000-000000000002"
    const val CATEGORY_ID = "category-1"
    const val REMINDER_ID = "reminder-1"
  }
}
