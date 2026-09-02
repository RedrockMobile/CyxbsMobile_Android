package com.cyxbs.pages.schedule.data.local.room3

import androidx.room3.ColumnTypeConverter
import com.cyxbs.pages.schedule.data.remote.v3.CategoryCurrent
import com.cyxbs.pages.schedule.data.remote.v3.CategoryInput
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideCurrent
import com.cyxbs.pages.schedule.data.remote.v3.OccurrenceOverrideInput
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleCurrent
import com.cyxbs.pages.schedule.data.remote.v3.ScheduleInput

/**
 * Schedule v2 双快照的 Room JSON 转换器。
 *
 * 每种协议类型都显式绑定对应的 typed JSON 转换，避免反射或把任意字符串包装成伪 snapshot。
 */
internal class ScheduleV2RoomConverters {
  /** 把服务端 Category 快照持久化为 canonical JSON。 */
  @ColumnTypeConverter
  fun categoryCurrentToJson(value: CategoryCurrent?): String? =
    value?.let(ScheduleV2RoomJsonCodec::encodeCategoryCurrent)

  /** 从持久化 JSON 严格恢复服务端 Category 快照。 */
  @ColumnTypeConverter
  fun categoryCurrentFromJson(value: String?): CategoryCurrent? =
    value?.let(ScheduleV2RoomJsonCodec::decodeCategoryCurrent)

  /** 把待提交 Category 完整输入持久化为 canonical JSON。 */
  @ColumnTypeConverter
  fun categoryInputToJson(value: CategoryInput?): String? =
    value?.let(ScheduleV2RoomJsonCodec::encodeCategoryInput)

  /** 从持久化 JSON 严格恢复待提交 Category 完整输入。 */
  @ColumnTypeConverter
  fun categoryInputFromJson(value: String?): CategoryInput? =
    value?.let(ScheduleV2RoomJsonCodec::decodeCategoryInput)

  /** 把服务端 Schedule 快照持久化为 canonical JSON，并保留 recurrence.data=null。 */
  @ColumnTypeConverter
  fun scheduleCurrentToJson(value: ScheduleCurrent?): String? =
    value?.let(ScheduleV2RoomJsonCodec::encodeScheduleCurrent)

  /** 从持久化 JSON 严格恢复服务端 Schedule 快照。 */
  @ColumnTypeConverter
  fun scheduleCurrentFromJson(value: String?): ScheduleCurrent? =
    value?.let(ScheduleV2RoomJsonCodec::decodeScheduleCurrent)

  /** 把待提交 Schedule 完整输入持久化为 canonical JSON。 */
  @ColumnTypeConverter
  fun scheduleInputToJson(value: ScheduleInput?): String? =
    value?.let(ScheduleV2RoomJsonCodec::encodeScheduleInput)

  /** 从持久化 JSON 严格恢复待提交 Schedule 完整输入。 */
  @ColumnTypeConverter
  fun scheduleInputFromJson(value: String?): ScheduleInput? =
    value?.let(ScheduleV2RoomJsonCodec::decodeScheduleInput)

  /** 把服务端 OccurrenceOverride live/tombstone 二选一状态持久化为 canonical JSON。 */
  @ColumnTypeConverter
  fun occurrenceOverrideRemoteStateToJson(value: ScheduleV2OccurrenceOverrideRemoteState?): String? =
    value?.let(ScheduleV2RoomJsonCodec::encodeOccurrenceOverrideRemoteState)

  /** 从持久化 JSON 严格恢复服务端 OccurrenceOverride live/tombstone 状态。 */
  @ColumnTypeConverter
  fun occurrenceOverrideRemoteStateFromJson(value: String?): ScheduleV2OccurrenceOverrideRemoteState? =
    value?.let(ScheduleV2RoomJsonCodec::decodeOccurrenceOverrideRemoteState)

  /** 把待提交 OccurrenceOverride 完整输入持久化为 canonical JSON。 */
  @ColumnTypeConverter
  fun occurrenceOverrideInputToJson(value: OccurrenceOverrideInput?): String? =
    value?.let { ScheduleV2RoomJsonCodec.encodeOccurrenceOverrideInput(it) }

  /** 从持久化 JSON 严格恢复待提交 OccurrenceOverride 完整输入。 */
  @ColumnTypeConverter
  fun occurrenceOverrideInputFromJson(value: String?): OccurrenceOverrideInput? =
    value?.let(ScheduleV2RoomJsonCodec::decodeOccurrenceOverrideInput)
}
