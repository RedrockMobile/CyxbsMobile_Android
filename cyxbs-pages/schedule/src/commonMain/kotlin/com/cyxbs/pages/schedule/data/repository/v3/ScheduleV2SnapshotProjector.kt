package com.cyxbs.pages.schedule.data.repository.v3

import com.cyxbs.components.config.time.Date
import com.cyxbs.components.config.time.MinuteTimeDate
import com.cyxbs.components.config.time.toDate
import com.cyxbs.components.config.time.toMinuteTimeDate
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleInput
import com.cyxbs.pages.schedule.domain.model.CategoryId
import com.cyxbs.pages.schedule.domain.model.IsoWeekDay
import com.cyxbs.pages.schedule.domain.model.OccurrencePatch
import com.cyxbs.pages.schedule.domain.model.RecurrenceEnd
import com.cyxbs.pages.schedule.domain.model.RecurrenceId
import com.cyxbs.pages.schedule.domain.model.RecurrenceRule
import com.cyxbs.pages.schedule.domain.model.ReminderChannel
import com.cyxbs.pages.schedule.domain.model.ReminderId
import com.cyxbs.pages.schedule.domain.model.Schedule
import com.cyxbs.pages.schedule.domain.model.ScheduleCategory
import com.cyxbs.pages.schedule.domain.model.ScheduleId
import com.cyxbs.pages.schedule.domain.model.ScheduleOccurrenceException
import com.cyxbs.pages.schedule.domain.model.ScheduleReminder
import com.cyxbs.pages.schedule.domain.model.ScheduleTiming
import com.cyxbs.pages.schedule.domain.model.ScheduleTodoState
import com.cyxbs.pages.schedule.domain.repository.ScheduleRepositoryStatus
import com.cyxbs.pages.schedule.domain.repository.ScheduleSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.CategoryResource
import com.cyxbs.pages.schedule.domain.sync.v2.CategorySyncState
import com.cyxbs.pages.schedule.domain.sync.v2.FieldPatch
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideResource
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceOverrideSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.OccurrenceStatus
import com.cyxbs.pages.schedule.domain.sync.v2.PendingDelete
import com.cyxbs.pages.schedule.domain.sync.v2.PendingUpsert
import com.cyxbs.pages.schedule.domain.sync.v2.RecurrenceFrequency
import com.cyxbs.pages.schedule.domain.sync.v2.RecurrenceInput
import com.cyxbs.pages.schedule.domain.sync.v2.ReminderInput
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleKind
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleRemoteSnapshot
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleResource
import com.cyxbs.pages.schedule.domain.sync.v2.ScheduleSyncState
import com.cyxbs.pages.schedule.domain.sync.v2.ServerResourceMeta
import com.cyxbs.pages.schedule.domain.sync.v2.TimingInput
import com.cyxbs.pages.schedule.domain.sync.v2.TimingKind
import com.cyxbs.pages.schedule.domain.sync.v2.TodoState
import com.cyxbs.pages.schedule.domain.sync.v2.Weekday
import com.cyxbs.pages.schedule.domain.validation.ScheduleValidator
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import com.cyxbs.pages.schedule.domain.model.FieldPatch as UiFieldPatch
import com.cyxbs.pages.schedule.domain.model.OccurrenceStatus as UiOccurrenceStatus
import com.cyxbs.pages.schedule.domain.model.RecurrenceFrequency as UiRecurrenceFrequency
import com.cyxbs.pages.schedule.domain.model.ScheduleKind as UiScheduleKind

private const val UTC_DAY_MILLIS = 86_400_000L
private const val MINUTE_MILLIS = 60_000L

/** common typed 状态投影为旧 UI 快照的受控结果。 */
sealed interface ScheduleV2SnapshotProjection {
  /** 全部资源均满足最终领域合同时返回的完整快照。 */
  data class Success(val snapshot: ScheduleSnapshot) : ScheduleV2SnapshotProjection

  /** 任一资源无法无损表达时整次失败；调用方不得发布部分列表。 */
  data class Failure(val message: String) : ScheduleV2SnapshotProjection
}

/**
 * 将三类 Schedule v2 双快照状态纯投影为 UI 使用的 [ScheduleSnapshot]。
 *
 * pending UPSERT 由 effective 规则覆盖 remote，pending DELETE 不可见。绝对毫秒没有携带原始时区，
 * 因此 Timed/Deadline 必须使用调用方传入的 [timeZone] 恢复墙上时间；本类不访问 Room 或网络。
 * ScheduleRemoteSnapshot.firstRecurrenceAnchorDate 会投影为领域层稳定 recurrenceAnchorDate；整个系列移动实际
 * 日期时仍以它生成 occurrence identity，不能退回当前 timing 日期。
 * 服务端提醒没有端内 identity，故 ReminderId 按日程 identity 稳定派生；不为单个本地实现字段引入 sidecar。
 */
class ScheduleV2SnapshotProjector {
  /**
   * 原子地投影当前账号的三类 typed 状态。
   *
   * @param accountId 发布快照所属账号，必须非空。
   * @param timeZone 恢复 Timed/Deadline 墙上时间所用的明确时区。
   * @return 成功时返回完整快照；任何坏行、重复 identity 或缺失 parent 均返回 [ScheduleV2SnapshotProjection.Failure]。
   */
  fun project(
    accountId: String,
    timeZone: TimeZone,
    categories: List<CategorySyncState>,
    schedules: List<ScheduleSyncState>,
    occurrenceOverrides: List<OccurrenceOverrideSyncState>,
  ): ScheduleV2SnapshotProjection = try {
    requireProjection(accountId.isNotBlank(), "accountId must not be blank")
    requireUnique(categories.map { it.identity }, "Category")
    requireUnique(schedules.map { it.identity }, "Schedule")
    requireUnique(occurrenceOverrides.map { it.identity }, "OccurrenceOverride")

    val visibleSchedules = schedules.mapNotNull { state ->
      state.effectiveResource()?.let { state.toUi(it, timeZone) }
    }
    val scheduleStatesById = schedules.associateBy { it.identity.id }
    val schedulesById = visibleSchedules.associateBy { it.id.value }
    val visibleOverrides = occurrenceOverrides.mapNotNull { state ->
      state.effectiveResource()?.let { resource ->
        val parentState = scheduleStatesById[resource.identity.scheduleId]
          ?: abortProjection("OccurrenceOverride parent Schedule is missing")
        // parent pending DELETE 时 UI 同时隐藏 parent 与 child；这不是坏行，也不能发布孤立 Override。
        if (parentState.effectiveResource() == null) return@mapNotNull null
        val parent = schedulesById[resource.identity.scheduleId]
          ?: abortProjection("OccurrenceOverride effective parent was not projected")
        state.toUi(resource, parent)
      }
    }
    val pendingCount = categories.count { it.pending != null } +
      schedules.count { it.pending != null } +
      occurrenceOverrides.count { it.pending != null }
    val hasDeletes = categories.any { it.pending is PendingDelete } ||
      schedules.any { it.pending is PendingDelete } ||
      occurrenceOverrides.any { it.pending is PendingDelete }

    ScheduleV2SnapshotProjection.Success(
      ScheduleSnapshot(
        schedules = visibleSchedules,
        exceptions = visibleOverrides,
        categories = categories.mapNotNull { state ->
          state.effectiveResource()?.let { it.toUi() }
        },
        status = ScheduleRepositoryStatus.Ready(pendingCount, hasDeletes),
        accountId = accountId,
      ),
    )
  } catch (failure: ProjectionAbort) {
    ScheduleV2SnapshotProjection.Failure(failure.message ?: "Schedule v2 projection failed")
  } catch (failure: IllegalArgumentException) {
    ScheduleV2SnapshotProjection.Failure(failure.message ?: "Schedule v2 projection is invalid")
  }

  /**
   * 将失败记录中的单条完整请求源恢复成编辑器使用的 [Schedule]。
   *
   * version=0 按本地 CREATE pending 投影；正版本按最小 canonical 快照投影。两条路径最终都复用 [project]，
   * 因而时间、重复规则、提醒和完成态不会与正常清单产生第二套转换语义。
   */
  internal fun projectFailureSource(
    input: ScheduleInput,
    timeZone: TimeZone,
  ): Schedule? {
    val resource = input.toDomain()
    val atomTimes = listOf(
      resource.title.modifiedAt,
      resource.description.modifiedAt,
      resource.categoryId.modifiedAt,
      resource.timing.modifiedAt,
      resource.recurrence.modifiedAt,
      resource.reminder.modifiedAt,
      resource.todoState.modifiedAt,
      resource.linkedToCourse.modifiedAt,
    )
    val state = if (resource.version == 0L) {
      ScheduleSyncState(
        identity = resource.identity,
        remoteSnapshot = null,
        pending = PendingUpsert(resource, localRevision = 1),
      )
    } else {
      ScheduleSyncState(
        identity = resource.identity,
        remoteSnapshot = ScheduleRemoteSnapshot(
          resource = resource,
          meta = ServerResourceMeta(
            createdAt = atomTimes.minOrNull() ?: 0L,
            remoteModifiedAt = atomTimes.maxOrNull() ?: 0L,
          ),
          firstRecurrenceAnchorDate = resource.recurrence.data?.anchorDate,
        ),
      )
    }
    return (project(
      accountId = "failure-record",
      timeZone = timeZone,
      categories = emptyList(),
      schedules = listOf(state),
      occurrenceOverrides = emptyList(),
    ) as? ScheduleV2SnapshotProjection.Success)?.snapshot?.schedules?.singleOrNull()
  }

  private fun CategoryResource.toUi(): ScheduleCategory = ScheduleCategory(
    id = CategoryId(identity.id),
    revision = version,
    name = name.data,
    // Category color 的 nullable AtomicField 已直接表达“未设置”，不把空串擅自改写为 null。
    color = color.data,
    sortOrder = sortOrder.data.toIntExact("Category sortOrder"),
  )

  private fun ScheduleSyncState.toUi(
    resource: ScheduleResource,
    timeZone: TimeZone,
  ): Schedule {
    val atomTimes = listOf(
      resource.title.modifiedAt,
      resource.description.modifiedAt,
      resource.categoryId.modifiedAt,
      resource.timing.modifiedAt,
      resource.recurrence.modifiedAt,
      resource.reminder.modifiedAt,
      resource.todoState.modifiedAt,
      resource.linkedToCourse.modifiedAt,
    )
    val (createdAt, updatedAt) = timestamps(remoteSnapshot?.meta, atomTimes)
    val timing = resource.timing.data.toUi(timeZone)
    val schedule = Schedule(
      id = ScheduleId(resource.identity.id),
      revision = resource.version,
      title = resource.title.data,
      description = resource.description.data,
      categoryId = resource.categoryId.data?.let(::CategoryId),
      timing = timing,
      recurrence = resource.recurrence.data?.toUi(),
      reminder = resource.reminder.data?.toUiReminder(resource.identity.id),
      todoState = resource.todoState.data?.toUi(),
      createdAt = createdAt,
      updatedAt = updatedAt,
      kind = resource.kind.toUi(),
      linkedToCourse = resource.linkedToCourse.data,
      recurrenceAnchorDate = (remoteSnapshot?.firstRecurrenceAnchorDate
        ?: resource.recurrence.data?.anchorDate)?.toUtcDate(),
    )
    // 服务端快照属于不可信输入：wire 形状合法不代表完整领域组合合法，发布前必须统一闭合校验。
    val issues = ScheduleValidator.validate(schedule)
    requireProjection(
      issues.isEmpty(),
      issues.joinToString(prefix = "invalid Schedule: ") { "${it.field} ${it.message}" },
    )
    return schedule
  }

  /** wire 只保留绝对毫秒，恢复本地墙上时间时必须显式使用 repository 选择的时区。 */
  private fun TimingInput.toUi(timeZone: TimeZone): ScheduleTiming = when (kind) {
    TimingKind.TIMED -> {
      val start = startAt ?: abortProjection("TIMED startAt is required")
      val end = endAt ?: abortProjection("TIMED endAt is required")
      requireProjection(dueAt == null && date == null, "invalid TIMED extra fields")
      requireProjection(start >= 0 && end > start, "TIMED duration must be positive")
      requireProjection(
        start % MINUTE_MILLIS == 0L && end % MINUTE_MILLIS == 0L,
        "TIMED bounds must align to whole minutes",
      )
      val duration = end - start
      requireProjection(duration % MINUTE_MILLIS == 0L, "TIMED duration must be whole minutes")
      ScheduleTiming.Timed(
        start = Instant.fromEpochMilliseconds(start).toLocalDateTime(timeZone).toMinuteTimeDate(),
        durationMinutes = (duration / MINUTE_MILLIS).toIntExact("TIMED durationMinutes"),
        timeZoneId = timeZone.id,
      )
    }
    TimingKind.DEADLINE -> {
      val due = dueAt ?: abortProjection("DEADLINE dueAt is required")
      requireProjection(startAt == null && endAt == null && date == null, "invalid DEADLINE extra fields")
      requireProjection(due >= 0, "DEADLINE dueAt must not be negative")
      requireProjection(due % MINUTE_MILLIS == 0L, "DEADLINE dueAt must align to a whole minute")
      ScheduleTiming.Deadline(
        due = Instant.fromEpochMilliseconds(due).toLocalDateTime(timeZone).toMinuteTimeDate(),
        timeZoneId = timeZone.id,
      )
    }
    TimingKind.ALL_DAY -> {
      val allDayDate = date ?: abortProjection("ALL_DAY date is required")
      requireProjection(startAt == null && endAt == null && dueAt == null, "invalid ALL_DAY extra fields")
      requireDateSlot(allDayDate, "ALL_DAY date")
      ScheduleTiming.AllDay(date = allDayDate.toUtcDate())
    }
    TimingKind.UNSCHEDULED -> {
      requireProjection(startAt == null && endAt == null && dueAt == null && date == null, "invalid UNSCHEDULED fields")
      ScheduleTiming.Unscheduled
    }
  }

  private fun RecurrenceInput.toUi(): RecurrenceRule {
    requireDateSlot(anchorDate, "recurrence anchorDate")
    untilDate?.let { requireDateSlot(it, "recurrence untilDate") }
    // anchorDate 是稳定 occurrence 日期轴；当前 timing 允许相对它产生正负偏移，不能要求二者日期相同。
    requireProjection(untilDate == null || untilDate >= anchorDate, "recurrence untilDate precedes anchorDate")
    val uiWeekdays = weekdays.map { it.toUi() }.toSet()
    when (frequency) {
      RecurrenceFrequency.DAILY -> requireProjection(
        uiWeekdays.isEmpty() && monthDays.isEmpty() && months.isEmpty(),
        "DAILY must not carry recurrence selectors",
      )
      RecurrenceFrequency.WEEKLY -> requireProjection(
        uiWeekdays.isNotEmpty() && monthDays.isEmpty() && months.isEmpty(),
        "WEEKLY requires only weekdays",
      )
      RecurrenceFrequency.MONTHLY -> requireProjection(
        uiWeekdays.isEmpty() && monthDays.isNotEmpty() && months.isEmpty(),
        "MONTHLY requires only monthDays",
      )
      RecurrenceFrequency.YEARLY -> requireProjection(
        uiWeekdays.isEmpty() && monthDays.isNotEmpty() && months.isNotEmpty(),
        "YEARLY requires monthDays and months",
      )
    }
    return RecurrenceRule(
      frequency = when (frequency) {
        RecurrenceFrequency.DAILY -> UiRecurrenceFrequency.DAILY
        RecurrenceFrequency.WEEKLY -> UiRecurrenceFrequency.WEEKLY
        RecurrenceFrequency.MONTHLY -> UiRecurrenceFrequency.MONTHLY
        RecurrenceFrequency.YEARLY -> UiRecurrenceFrequency.YEARLY
      },
      interval = interval,
      byWeekDays = uiWeekdays,
      byMonthDays = monthDays.toSet(),
      byMonths = months.toSet(),
      end = when {
        count != null -> RecurrenceEnd.Count(count)
        untilDate != null -> RecurrenceEnd.Until(untilDate.toUtcDate())
        else -> RecurrenceEnd.Never
      },
    )
  }

  /** 服务端不保存端内 identity，因此按父资源 identity 稳定派生当前唯一提醒的本地 ID。 */
  private fun ReminderInput.toUiReminder(identity: String): ScheduleReminder {
    requireProjection(minutesBefore >= 0, "reminder minutesBefore must not be negative")
    return ScheduleReminder(
      id = ReminderId("$identity:reminder"),
      offsetMinutes = minutesBefore,
      channel = ReminderChannel.DEVICE,
    )
  }

  private fun OccurrenceOverrideSyncState.toUi(
    resource: OccurrenceOverrideResource,
    parent: Schedule,
  ): ScheduleOccurrenceException {
    val atomTimes = listOf(
      resource.status.modifiedAt,
      resource.timing.modifiedAt,
      resource.title.modifiedAt,
      resource.description.modifiedAt,
      resource.categoryId.modifiedAt,
      resource.reminder.modifiedAt,
    )
    val (createdAt, updatedAt) = timestamps(remoteSnapshot?.meta, atomTimes)
    val recurrenceId = parent.toRecurrenceId(resource.identity.occurrenceDate)
    return ScheduleOccurrenceException(
      scheduleId = parent.id,
      recurrenceId = recurrenceId,
      revision = resource.version,
      status = resource.status.data.toUi(),
      patch = OccurrencePatch(
        timing = resource.timing.data.toUiTimingPatch(parent),
        title = resource.title.data.toUiTitlePatch(),
        description = resource.description.data.toUiStringPatch(),
        categoryId = resource.categoryId.data.toUiCategoryPatch(),
        reminder = resource.reminder.data.toUiReminderPatch(
          "${parent.id.value}@${resource.identity.occurrenceDate}",
        ),
      ),
      createdAt = createdAt,
      updatedAt = updatedAt,
    )
  }

  /** timing patch 沿用 parent 的 IANA 时区恢复墙上时间；identity 日期不取移动后的 timing。 */
  private fun FieldPatch<TimingInput>.toUiTimingPatch(parent: Schedule): UiFieldPatch<ScheduleTiming> = when (this) {
    FieldPatch.Inherit -> UiFieldPatch.Inherit
    FieldPatch.Clear -> abortProjection("OccurrenceOverride timing cannot be Clear")
    is FieldPatch.Replace -> {
      val zone = when (val timing = parent.timing) {
        is ScheduleTiming.Timed -> TimeZone.of(timing.timeZoneId)
        is ScheduleTiming.Deadline -> TimeZone.of(timing.timeZoneId)
        is ScheduleTiming.AllDay -> TimeZone.UTC
        ScheduleTiming.Unscheduled -> abortProjection("Unscheduled parent cannot own timing override")
      }
      UiFieldPatch.Replace(value.toUi(zone))
    }
  }

  private fun FieldPatch<String>.toUiCategoryPatch(): UiFieldPatch<CategoryId> = when (this) {
    FieldPatch.Inherit -> UiFieldPatch.Inherit
    FieldPatch.Clear -> UiFieldPatch.Clear
    is FieldPatch.Replace -> UiFieldPatch.Replace(CategoryId(value))
  }

  /** occurrenceDate 提供日期，parent timing 提供稳定墙上时刻、时区与 allDay 语义。 */
  private fun Schedule.toRecurrenceId(occurrenceDate: Long): RecurrenceId {
    requireDateSlot(occurrenceDate, "OccurrenceOverride occurrenceDate")
    val date = occurrenceDate.toUtcDate()
    return when (val parentTiming = timing) {
      is ScheduleTiming.Timed -> RecurrenceId(
        MinuteTimeDate(date, parentTiming.start.time),
        parentTiming.timeZoneId,
        allDay = false,
      )
      is ScheduleTiming.Deadline -> RecurrenceId(
        MinuteTimeDate(date, parentTiming.due.time),
        parentTiming.timeZoneId,
        allDay = false,
      )
      is ScheduleTiming.AllDay -> RecurrenceId(
        MinuteTimeDate(date, 0, 0),
        timeZoneId = null,
        allDay = true,
      )
      ScheduleTiming.Unscheduled -> abortProjection("Unscheduled parent cannot restore RecurrenceId")
    }
  }

  private fun FieldPatch<String>.toUiTitlePatch(): UiFieldPatch<String> = when (this) {
    FieldPatch.Inherit -> UiFieldPatch.Inherit
    FieldPatch.Clear -> abortProjection("OccurrenceOverride title cannot be Clear")
    is FieldPatch.Replace -> UiFieldPatch.Replace(value)
  }

  private fun FieldPatch<String>.toUiStringPatch(): UiFieldPatch<String> = when (this) {
    FieldPatch.Inherit -> UiFieldPatch.Inherit
    FieldPatch.Clear -> UiFieldPatch.Clear
    is FieldPatch.Replace -> UiFieldPatch.Replace(value)
  }

  private fun FieldPatch<ReminderInput>.toUiReminderPatch(identity: String):
    UiFieldPatch<ScheduleReminder> = when (this) {
    FieldPatch.Inherit -> UiFieldPatch.Inherit
    FieldPatch.Clear -> UiFieldPatch.Clear
    is FieldPatch.Replace -> UiFieldPatch.Replace(value.toUiReminder(identity))
  }

  private fun TodoState.toUi(): ScheduleTodoState = when (this) {
    TodoState.OPEN -> ScheduleTodoState.PENDING
    TodoState.COMPLETED -> ScheduleTodoState.COMPLETED
  }

  private fun ScheduleKind.toUi(): UiScheduleKind = when (this) {
    ScheduleKind.TODO -> UiScheduleKind.TODO
    ScheduleKind.AFFAIR -> UiScheduleKind.AFFAIR
  }

  private fun OccurrenceStatus.toUi(): UiOccurrenceStatus = when (this) {
    OccurrenceStatus.ACTIVE -> UiOccurrenceStatus.ACTIVE
    OccurrenceStatus.COMPLETED -> UiOccurrenceStatus.COMPLETED
    OccurrenceStatus.CANCELLED -> UiOccurrenceStatus.CANCELLED
  }

  private fun Weekday.toUi(): IsoWeekDay = when (this) {
    Weekday.MO -> IsoWeekDay.MONDAY
    Weekday.TU -> IsoWeekDay.TUESDAY
    Weekday.WE -> IsoWeekDay.WEDNESDAY
    Weekday.TH -> IsoWeekDay.THURSDAY
    Weekday.FR -> IsoWeekDay.FRIDAY
    Weekday.SA -> IsoWeekDay.SATURDAY
    Weekday.SU -> IsoWeekDay.SUNDAY
  }

  private fun timestamps(meta: ServerResourceMeta?, atomTimes: List<Long>): Pair<Instant, Instant> {
    requireProjection(atomTimes.isNotEmpty(), "resource must contain atomic fields")
    val created = meta?.createdAt ?: atomTimes.min()
    val updated = meta?.let { maxOf(it.remoteModifiedAt, atomTimes.max()) } ?: atomTimes.max()
    return Instant.fromEpochMilliseconds(created) to Instant.fromEpochMilliseconds(updated)
  }

  private fun Long.toUtcDate(): Date =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date.toDate()

  private fun requireDateSlot(value: Long, label: String) {
    requireProjection(value >= 0 && value % UTC_DAY_MILLIS == 0L, "$label must be a UTC date slot")
  }

  private fun Long.toIntExact(label: String): Int {
    requireProjection(this in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong(), "$label is out of Int range")
    return toInt()
  }

  private fun <T> requireUnique(values: List<T>, label: String) {
    requireProjection(values.size == values.toSet().size, "$label contains duplicate identities")
  }
}

private class ProjectionAbort(message: String) : IllegalStateException(message)

private fun requireProjection(condition: Boolean, message: String) {
  if (!condition) abortProjection(message)
}

private fun abortProjection(message: String): Nothing = throw ProjectionAbort(message)
