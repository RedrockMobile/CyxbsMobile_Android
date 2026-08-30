package com.cyxbs.pages.schedule.data.remote.v3

import kotlinx.serialization.Serializable

/** Schedule v2 JSON 合同使用的 Unix 毫秒，不允许客户端自行换算为本地时区。 */
typealias UnixMillis = Long

/** 可独立 LWW 合并的业务字段；data 与 modifiedAt 都是 required。 */
@Serializable
data class AtomicField<T>(
  val data: T, // 当前业务值；Category.color、Schedule.recurrence 与 Schedule.todoState 可显式为 null。
  val modifiedAt: UnixMillis, // 客户端最后修改此原子的时刻；零值合法但字段不可缺失。
)

/** OccurrenceOverride 字段的继承、清空与替换模式。 */
@Serializable
enum class PatchMode { INHERIT, CLEAR, REPLACE }

/** 单次 occurrence 的活动、完成或取消状态。 */
@Serializable
enum class OccurrenceStatus { ACTIVE, COMPLETED, CANCELLED }

/** 日程进入清单后的完成状态；原子 data 为 null 时表示不属于清单。 */
@Serializable
enum class TodoState { OPEN, COMPLETED }

/** 日程的不可变创建来源。 */
@Serializable
enum class ScheduleKind { TODO, AFFAIR }

/** Schedule 时间联合类型；kind 决定哪些 nullable 时间字段可以出现。 */
@Serializable
enum class TimingKind { TIMED, DEADLINE, ALL_DAY, UNSCHEDULED }

/** 当前 wire 支持的重复频率。 */
@Serializable
enum class RecurrenceFrequency { DAILY, WEEKLY }

/** WEEKLY recurrence 使用的 ISO 风格星期枚举。 */
@Serializable
enum class Weekday { MO, TU, WE, TH, FR, SA, SU }

/** 普通单资源 mutation 的稳定机器结果码。 */
@Serializable
enum class MutationResultCode {
  CREATED, ALREADY_EXISTS, DELETED, APPLIED,
  ALREADY_SATISFIED, SERVER_WON, REJECTED, RESOURCE_DELETED,
}

/** 拒绝结果可携带的稳定机器原因。 */
@Serializable
enum class ResultReason {
  INVALID_REQUEST, RESOURCE_NOT_FOUND, RESOURCE_DELETED, CATEGORY_NOT_FOUND,
  RESOURCE_CHANGED, FINAL_GRAPH_INVALID, UNSUPPORTED_RECURRENCE,
}

/** INHERIT/CLEAR 必须省略 value；REPLACE 必须携带完整 value。 */
@Serializable
data class FieldPatch<T>(
  val mode: PatchMode, // required，明确继承、清空或替换语义。
  val value: T? = null, // 仅 REPLACE 使用；其他模式保持 null 并从 JSON 省略。
)

/** 相对 Schedule timing 的提醒配置。 */
@Serializable
data class ReminderInput(
  val minutesBefore: Int, // required 且非负；零表示事件发生时提醒。
  val message: String, // required；空串表示没有自定义文案。
)

/** kind 决定 startAt/endAt/dueAt 的唯一合法组合。 */
@Serializable
data class TimingInput(
  val kind: TimingKind, // required，决定后续字段 presence。
  val startAt: UnixMillis? = null, // TIMED/ALL_DAY required，其他 kind 省略。
  val endAt: UnixMillis? = null, // TIMED/ALL_DAY required 且为排他结束边界。
  val dueAt: UnixMillis? = null, // DEADLINE required，其他 kind 省略。
)

/** 以 UTC 日期槽表达的重复规则；count 与 untilDate 互斥。 */
@Serializable
data class RecurrenceInput(
  val frequency: RecurrenceFrequency, // required，目前仅 DAILY/WEEKLY。
  val interval: Int, // required，正数周期跨度。
  val anchorDate: UnixMillis, // required，UTC 午夜日期槽。
  val count: Int? = null, // 可选成员数上限。
  val untilDate: UnixMillis? = null, // 可选包含边界，不早于 anchorDate。
  val weekdays: List<Weekday>, // required；DAILY 为空，WEEKLY 非空且不重复。
)

/** Category 的完整 live 快照；version 直接属于资源。 */
@Serializable
data class CategoryInput(
  val id: String, // required，owner 范围内稳定 identity。
  val version: ULong, // required；0 表示 CREATE，正数表示 PATCH。
  val name: AtomicField<String>, // required，名称原子。
  val color: AtomicField<String?>, // required；课表配色 JSON，没有自定义颜色时 data 显式为 null。
  val sortOrder: AtomicField<Long>, // required，排序原子。
)

/** Schedule 的完整 live 快照；kind 不可变，其余八个业务字段按原子合并。 */
@Serializable
data class ScheduleInput(
  val id: String, // required，owner 范围内稳定 identity。
  val version: ULong, // required；0 表示 CREATE，正数表示 PATCH。
  val kind: ScheduleKind, // required，创建来源；PATCH 不允许改变。
  val title: AtomicField<String>, // required，标题原子。
  val description: AtomicField<String>, // required，详情原子。
  val categoryId: AtomicField<String>, // required，Category 引用原子。
  val timing: AtomicField<TimingInput>, // required，完整时间联合值。
  val recurrence: AtomicField<RecurrenceInput?>, // required；data=null 明确表示非重复。
  val reminders: AtomicField<List<ReminderInput>>, // required，空列表合法。
  val todoState: AtomicField<TodoState?>, // required；data=null 表示当前不属于清单。
  val linkedToCourse: AtomicField<Boolean>, // required，是否请求投射到课表。
)

/** identity 固定为 scheduleId + occurrenceDate；移动 timing 不改变原始实例身份。 */
@Serializable
data class OccurrenceOverrideInput(
  val scheduleId: String, // required，parent Schedule identity。
  val occurrenceDate: UnixMillis, // required，UTC 午夜日期槽。
  val version: ULong, // required；0 表示 CREATE，正数表示 PATCH。
  val status: AtomicField<OccurrenceStatus>, // required，实例状态原子。
  val timing: AtomicField<FieldPatch<TimingInput>>, // required，完整 timing 三态原子；CLEAR 非法。
  val title: AtomicField<FieldPatch<String>>, // required，标题三态原子。
  val description: AtomicField<FieldPatch<String>>, // required，详情三态原子。
  val categoryId: AtomicField<FieldPatch<String>>, // required，分类三态原子。
  val reminders: AtomicField<FieldPatch<List<ReminderInput>>>, // required，提醒列表三态原子。
)

/** 客户端已持有的 live Category；tombstone 不进入 confirmed。 */
@Serializable
data class ConfirmedCategory(
  val id: String, // required，live identity。
  val version: ULong, // required 且 >0，客户端已确认的服务端版本。
)

/** 客户端已持有的 live Schedule；可与同 identity pending 同时上传。 */
@Serializable
data class ConfirmedSchedule(
  val id: String, // required，live identity。
  val version: ULong, // required 且 >0，客户端已确认的服务端版本。
)

/** 客户端已持有的 live OccurrenceOverride。 */
@Serializable
data class ConfirmedOccurrenceOverride(
  val scheduleId: String, // required，parent identity。
  val occurrenceDate: UnixMillis, // required，UTC 午夜日期槽。
  val version: ULong, // required 且 >0，客户端已确认的服务端版本。
)

/** delete-wins 的 Category 删除输入；不携带 version。 */
@Serializable
data class CategoryDelete(
  val id: String, // required，待删除 identity。
  val localModifiedAt: UnixMillis, // required，本地删除时刻；零值合法。
)

/** delete-wins 的 Schedule 删除输入；不携带 version。 */
@Serializable
data class ScheduleDelete(
  val id: String, // required，待删除 identity。
  val localModifiedAt: UnixMillis, // required，本地删除时刻；零值合法。
)

/** delete-wins 的 Override 删除输入；parent/date 构成 identity，不携带 version。 */
@Serializable
data class OccurrenceOverrideDelete(
  val scheduleId: String, // required，parent identity。
  val occurrenceDate: UnixMillis, // required，UTC 午夜日期槽。
  val localModifiedAt: UnixMillis, // required，本地删除时刻；零值合法。
)

/** Category live inventory 与普通 pending；三个列表都 required。 */
@Serializable
data class CategorySyncRequest(
  val confirmed: List<ConfirmedCategory>, // live-only inventory，空列表也显式发送。
  val upserts: List<CategoryInput>, // 结果与 upsertResults 按下标对齐。
  val deletes: List<CategoryDelete>, // 结果与 deleteResults 按下标对齐。
)

/** Schedule live inventory 与普通 pending；三个列表都 required。 */
@Serializable
data class ScheduleSyncRequest(
  val confirmed: List<ConfirmedSchedule>, // live-only，可与同 identity pending 并存。
  val upserts: List<ScheduleInput>, // 结果与 upsertResults 按下标对齐。
  val deletes: List<ScheduleDelete>, // 结果与 deleteResults 按下标对齐。
)

/** Override live inventory 与普通 pending；三个列表都 required。 */
@Serializable
data class OccurrenceOverrideSyncRequest(
  val confirmed: List<ConfirmedOccurrenceOverride>, // live-only parent/date inventory。
  val upserts: List<OccurrenceOverrideInput>, // 结果与 upsertResults 按下标对齐。
  val deletes: List<OccurrenceOverrideDelete>, // 结果与 deleteResults 按下标对齐。
)

/** 日常 Category 变更；两个列表均 required，结果分别按下标对齐。 */
@Serializable
data class CategoryMutationRequest(
  val upserts: List<CategoryInput>,
  val deletes: List<CategoryDelete>,
)

/** 日常 Schedule 变更；不同资源独立处理，不形成跨资源事务。 */
@Serializable
data class ScheduleMutationRequest(
  val upserts: List<ScheduleInput>,
  val deletes: List<ScheduleDelete>,
)

/** 日常 OccurrenceOverride 变更。 */
@Serializable
data class OccurrenceOverrideMutationRequest(
  val upserts: List<OccurrenceOverrideInput>,
  val deletes: List<OccurrenceOverrideDelete>,
)

/** 日常三个接口共用的请求；至少包含一项操作。 */
@Serializable
data class MutationRequest(
  val requestId: String,
  val categories: CategoryMutationRequest,
  val schedules: ScheduleMutationRequest,
  val occurrenceOverrides: OccurrenceOverrideMutationRequest,
)

/** 一次完整同步请求；所有 typed block/list 都必须显式出现。 */
@Serializable
data class SyncRequest(
  val syncRequestId: String, // required，请求级关联 ID，响应原样回显。
  val categories: CategorySyncRequest, // required，Category inventory 与普通 mutation。
  val schedules: ScheduleSyncRequest, // required，Schedule inventory 与普通 mutation。
  val occurrenceOverrides: OccurrenceOverrideSyncRequest, // required，Override inventory 与普通 mutation。
)

/** canonical live 资源的服务端只读时间元数据。 */
@Serializable
data class ServerResourceMeta(
  val createdAt: UnixMillis, // required，服务端首次创建时刻。
  val remoteModifiedAt: UnixMillis, // required，服务端最后接受变更时刻。
)

/** 服务端 canonical live Category。 */
@Serializable
data class CategoryCurrent(
  val resource: CategoryInput, // required，包含当前 version 的完整资源。
  val meta: ServerResourceMeta, // required，服务端时间元数据。
)

/** 服务端 canonical live Schedule。 */
@Serializable
data class ScheduleCurrent(
  val resource: ScheduleInput, // required，字段合并后的完整资源，客户端直接接受。
  val meta: ServerResourceMeta, // required，服务端时间元数据。
  val firstRecurrenceAnchorDate: UnixMillis? = null, // 可选，首次启用重复的稳定 UTC 日期槽。
)

/** 服务端 canonical live OccurrenceOverride。 */
@Serializable
data class OccurrenceOverrideCurrent(
  val resource: OccurrenceOverrideInput, // required，parent/date 与四原子完整资源。
  val meta: ServerResourceMeta, // required，服务端时间元数据。
)

/** Category 删除状态；不携带 version/deleteVersion。 */
@Serializable
data class CategoryTombstone(
  val id: String, // required，被删除 identity。
  val deletedAt: UnixMillis, // required，服务端确认删除时刻。
  val reason: ResultReason? = null, // 可选稳定原因；无值时省略。
)

/** Schedule 删除状态；不携带 version/deleteVersion。 */
@Serializable
data class ScheduleTombstone(
  val id: String, // required，被删除 identity。
  val deletedAt: UnixMillis, // required，服务端确认删除时刻。
  val reason: ResultReason? = null, // 可选稳定原因；无值时省略。
)

/** OccurrenceOverride 删除状态；仍使用 parent/date identity。 */
@Serializable
data class OccurrenceOverrideTombstone(
  val scheduleId: String, // required，parent identity。
  val occurrenceDate: UnixMillis, // required，UTC 午夜日期槽。
  val deletedAt: UnixMillis, // required，服务端确认删除时刻。
  val reason: ResultReason? = null, // 可选稳定原因；无值时省略。
)

/** 与 categories.upserts 按下标对齐的结果。 */
@Serializable
data class CategoryUpsertResult(
  val id: String, // required，对应输入 identity。
  val result: MutationResultCode, // required，稳定处理结论。
  val reason: ResultReason? = null, // 可选，通常只在拒绝时存在。
  val info: String? = null, // 可选，仅暴露不含凭证或实际输入值的业务错误说明。
  val current: CategoryCurrent? = null, // live 最终状态；与 tombstone 按 code 语义互斥。
  val tombstone: CategoryTombstone? = null, // 删除最终状态；与 current 按 code 语义互斥。
)

/** 与 schedules.upserts 按下标对齐的结果。 */
@Serializable
data class ScheduleUpsertResult(
  val id: String, // required，对应输入 identity。
  val result: MutationResultCode, // required，稳定处理结论。
  val reason: ResultReason? = null, // 可选，通常只在拒绝时存在。
  val info: String? = null, // 可选安全业务说明。
  val current: ScheduleCurrent? = null, // live 合并结果；与 tombstone 按 code 语义互斥。
  val tombstone: ScheduleTombstone? = null, // 不可复活删除状态；与 current 互斥。
)

/** 与 occurrenceOverrides.upserts 按下标对齐的结果。 */
@Serializable
data class OccurrenceOverrideUpsertResult(
  val scheduleId: String, // required，对应输入 parent identity。
  val occurrenceDate: UnixMillis, // required，对应输入 UTC 日期槽。
  val result: MutationResultCode, // required，稳定处理结论。
  val reason: ResultReason? = null, // 可选，通常只在拒绝时存在。
  val info: String? = null, // 可选安全业务说明。
  val current: OccurrenceOverrideCurrent? = null, // live 最终状态；与 tombstone 互斥。
  val tombstone: OccurrenceOverrideTombstone? = null, // 删除最终状态；与 current 互斥。
)

/** 与 categories.deletes 按下标对齐的结果。 */
@Serializable
data class CategoryDeleteResult(
  val id: String, // required，对应输入 identity。
  val result: MutationResultCode, // required，删除或拒绝结论；重复删除同样返回 DELETED。
  val reason: ResultReason? = null, // 可选机器原因。
  val info: String? = null, // 可选安全业务说明。
  val current: CategoryCurrent? = null, // 删除拒绝时的 live 状态；与 tombstone 互斥。
  val tombstone: CategoryTombstone? = null, // 删除成功/已删除状态；与 current 互斥。
)

/** 与 schedules.deletes 按下标对齐的结果。 */
@Serializable
data class ScheduleDeleteResult(
  val id: String, // required，对应输入 identity。
  val result: MutationResultCode, // required，删除或拒绝结论。
  val reason: ResultReason? = null, // 可选机器原因。
  val info: String? = null, // 可选安全业务说明。
  val current: ScheduleCurrent? = null, // 删除拒绝时的 live 状态；与 tombstone 互斥。
  val tombstone: ScheduleTombstone? = null, // 删除成功/已删除状态；与 current 互斥。
)

/** 与 occurrenceOverrides.deletes 按下标对齐的结果。 */
@Serializable
data class OccurrenceOverrideDeleteResult(
  val scheduleId: String, // required，对应输入 parent identity。
  val occurrenceDate: UnixMillis, // required，对应输入 UTC 日期槽。
  val result: MutationResultCode, // required，删除或拒绝结论。
  val reason: ResultReason? = null, // 可选机器原因。
  val info: String? = null, // 可选安全业务说明。
  val current: OccurrenceOverrideCurrent? = null, // 删除拒绝时的 live 状态；与 tombstone 互斥。
  val tombstone: OccurrenceOverrideTombstone? = null, // 删除成功/已删除状态；与 current 互斥。
)

/** inventory 核对结果码。 */
@Serializable
enum class ConfirmedResultCode { CONFIRMED, CHANGED, DELETED }

/** 与 categories.confirmed 按下标对齐的核对结果。 */
@Serializable
data class CategoryConfirmedResult(
  val id: String,
  val result: ConfirmedResultCode,
  val version: ULong? = null,
  val current: CategoryCurrent? = null,
  val tombstone: CategoryTombstone? = null,
)

/** 与 schedules.confirmed 按下标对齐的核对结果。 */
@Serializable
data class ScheduleConfirmedResult(
  val id: String,
  val result: ConfirmedResultCode,
  val version: ULong? = null,
  val current: ScheduleCurrent? = null,
  val tombstone: ScheduleTombstone? = null,
)

/** 与 occurrenceOverrides.confirmed 按下标对齐的核对结果。 */
@Serializable
data class OccurrenceOverrideConfirmedResult(
  val scheduleId: String,
  val occurrenceDate: UnixMillis,
  val result: ConfirmedResultCode,
  val version: ULong? = null,
  val current: OccurrenceOverrideCurrent? = null,
  val tombstone: OccurrenceOverrideTombstone? = null,
)

/** Category inventory 核对、其他设备发现项与本次操作结果。 */
@Serializable
data class CategorySyncResponse(
  val confirmedResults: List<CategoryConfirmedResult>,
  val discoveredResults: List<CategoryCurrent>,
  val upsertResults: List<CategoryUpsertResult>,
  val deleteResults: List<CategoryDeleteResult>,
)

/** Schedule inventory 核对、其他设备发现项与本次操作结果。 */
@Serializable
data class ScheduleSyncResponse(
  val confirmedResults: List<ScheduleConfirmedResult>,
  val discoveredResults: List<ScheduleCurrent>,
  val upsertResults: List<ScheduleUpsertResult>,
  val deleteResults: List<ScheduleDeleteResult>,
)

/** OccurrenceOverride inventory 核对、其他设备发现项与本次操作结果。 */
@Serializable
data class OccurrenceOverrideSyncResponse(
  val confirmedResults: List<OccurrenceOverrideConfirmedResult>,
  val discoveredResults: List<OccurrenceOverrideCurrent>,
  val upsertResults: List<OccurrenceOverrideUpsertResult>,
  val deleteResults: List<OccurrenceOverrideDeleteResult>,
)

/** 日常 Category 操作结果。 */
@Serializable
data class CategoryMutationResponse(
  val upsertResults: List<CategoryUpsertResult>,
  val deleteResults: List<CategoryDeleteResult>,
)

/** 日常 Schedule 操作结果。 */
@Serializable
data class ScheduleMutationResponse(
  val upsertResults: List<ScheduleUpsertResult>,
  val deleteResults: List<ScheduleDeleteResult>,
)

/** 日常 OccurrenceOverride 操作结果。 */
@Serializable
data class OccurrenceOverrideMutationResponse(
  val upsertResults: List<OccurrenceOverrideUpsertResult>,
  val deleteResults: List<OccurrenceOverrideDeleteResult>,
)

/** 日常三个接口共用的逐资源结果。 */
@Serializable
data class MutationResponse(
  val requestId: String,
  val categories: CategoryMutationResponse,
  val schedules: ScheduleMutationResponse,
  val occurrenceOverrides: OccurrenceOverrideMutationResponse,
)

/** 一次完整 Schedule v2 同步响应。 */
@Serializable
data class SyncResponse(
  val syncRequestId: String,
  val categories: CategorySyncResponse,
  val schedules: ScheduleSyncResponse,
  val occurrenceOverrides: OccurrenceOverrideSyncResponse,
)
