package com.cyxbs.pages.schedule.data.remote.v3

import com.cyxbs.components.utils.extensions.log
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Schedule v2 详细业务日志。
 *
 * 该日志会包含用户填写的标题，仅用于联调增删改查与服务端合并结果；不记录描述、提醒文案、token、header 或完整 JSON。
 */
internal const val SCHEDULE_DETAIL_LOG_TAG = "ScheduleV2Detail"

/** 输出完整 Sync 中客户端已持有的 identity、待提交日程与原子批次。 */
internal fun SyncRequest.logScheduleRequest(timeZone: TimeZone) {
  log(SCHEDULE_DETAIL_LOG_TAG, "REQUEST SYNC requestId=$syncRequestId")
  logItems(
    label = "REQUEST SYNC confirmed",
    items = schedules.confirmed.map { "id=${it.id}, version=${it.version}" },
  )
  logItems(
    label = "REQUEST SYNC upsert",
    items = schedules.upserts.map { it.diagnosticSummary(timeZone) },
  )
  logItems(
    label = "REQUEST SYNC delete",
    items = schedules.deletes.map { "id=${it.id}" },
  )
  atomicBatches.forEach { it.logScheduleRequest("REQUEST SYNC atomic", timeZone) }
}

/** 输出日常聚合请求中实际新增或修改的完整日程，以及删除 identity。 */
internal fun AtomicBatch.logScheduleRequest(operation: String, timeZone: TimeZone) {
  log(SCHEDULE_DETAIL_LOG_TAG, "$operation batchId=$batchId")
  logItems(
    label = "$operation upsert",
    items = schedules.upserts.map { it.diagnosticSummary(timeZone) },
  )
  logItems(
    label = "$operation delete",
    items = schedules.deletes.map { "id=${it.id}" },
  )
}

/** 输出完整 Sync 返回的 inventory、普通 mutation 结果和原子批次 canonical 结果。 */
internal fun SyncResponse.logScheduleResponse(timeZone: TimeZone) {
  log(SCHEDULE_DETAIL_LOG_TAG, "RESPONSE SYNC requestId=$syncRequestId")
  logItems(
    label = "RESPONSE SYNC inventoryUpsert",
    items = schedules.upserts.map { it.resource.diagnosticSummary(timeZone) },
  )
  logItems(
    label = "RESPONSE SYNC inventoryDelete",
    items = schedules.deletes.map { "id=${it.id}" },
  )
  logItems(
    label = "RESPONSE SYNC upsertResult",
    items = schedules.upsertResults.map { result ->
      "id=${result.id}, code=${result.code}, reason=${result.reason}, " +
        "current=${result.current?.resource?.diagnosticSummary(timeZone)}, tombstoneId=${result.tombstone?.id}"
    },
  )
  logItems(
    label = "RESPONSE SYNC deleteResult",
    items = schedules.deleteResults.map { result ->
      "id=${result.id}, code=${result.code}, reason=${result.reason}, " +
        "current=${result.current?.resource?.diagnosticSummary(timeZone)}, tombstoneId=${result.tombstone?.id}"
    },
  )
  atomicBatchResults.forEach { it.logScheduleResponse(timeZone) }
}

/** 输出日常原子批次的处理结论及服务端最终返回的相关日程。 */
internal fun AtomicBatchResult.logScheduleResponse(timeZone: TimeZone) {
  log(
    SCHEDULE_DETAIL_LOG_TAG,
    "RESPONSE ATOMIC batchId=$batchId, code=$code, reason=$reason",
  )
  logItems(
    label = "RESPONSE ATOMIC upsertResult",
    items = schedules.upsertResults.map {
      "id=${it.id}, code=${it.code}, reason=${it.reason}"
    },
  )
  logItems(
    label = "RESPONSE ATOMIC deleteResult",
    items = schedules.deleteResults.map {
      "id=${it.id}, code=${it.code}, reason=${it.reason}"
    },
  )
  logItems(
    label = "RESPONSE ATOMIC relatedUpsert",
    items = schedules.relatedUpserts.map { it.resource.diagnosticSummary(timeZone) },
  )
  logItems(
    label = "RESPONSE ATOMIC relatedDelete",
    items = schedules.relatedDeletes.map { "id=${it.id}" },
  )
}

/** 将 wire 日程压缩为标题、日期时间与周期，不包含描述等额外用户内容。 */
internal fun ScheduleInput.diagnosticSummary(timeZone: TimeZone): String =
  "id=$id, version=$version, title=${title.data.singleLine()}, " +
    "timing=${timing.data.diagnosticSummary(timeZone)}, " +
    "recurrence=${recurrence.data.diagnosticSummary(timeZone)}"

/** 将 wire 时间联合值转换为设备时区下可读的日期时间。 */
private fun TimingInput.diagnosticSummary(timeZone: TimeZone): String = when (kind) {
  TimingKind.TIMED ->
    "TIMED ${startAt?.localDateTime(timeZone)}..${endAt?.localDateTime(timeZone)}"
  TimingKind.DEADLINE -> "DEADLINE ${dueAt?.localDateTime(timeZone)}"
  TimingKind.ALL_DAY ->
    "ALL_DAY ${startAt?.localDateTime(timeZone)}..${endAt?.localDateTime(timeZone)}"
  TimingKind.UNSCHEDULED -> "UNSCHEDULED"
}

/** 输出 wire 重复频率、间隔、星期和终止条件。 */
private fun RecurrenceInput?.diagnosticSummary(timeZone: TimeZone): String = when (this) {
  null -> "NONE"
  else -> buildString {
    append(frequency)
    append("(interval=")
    append(interval)
    append(", anchor=")
    append(anchorDate.localDateTime(timeZone).date)
    if (weekdays.isNotEmpty()) append(", weekdays=$weekdays")
    if (count != null) append(", count=$count")
    if (untilDate != null) append(", until=${untilDate.localDateTime(timeZone).date}")
    append(')')
  }
}

/** 将毫秒时间戳转换成设备时区下的日期时间；nullable 字段由调用方保留 null 以便诊断。 */
private fun Long.localDateTime(timeZone: TimeZone): LocalDateTime =
  Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)

/** 防止标题中的换行破坏一条日程对应一条日志的结构。 */
private fun String.singleLine(): String = replace('\n', ' ').replace('\r', ' ')

/** 列表为空时也输出 EMPTY，避免无法区分“未打印”和“确实没有返回”。 */
private fun logItems(label: String, items: List<String>) {
  if (items.isEmpty()) {
    log(SCHEDULE_DETAIL_LOG_TAG, "$label: EMPTY")
  } else {
    items.forEachIndexed { index, item ->
      log(SCHEDULE_DETAIL_LOG_TAG, "$label[$index]: $item")
    }
  }
}
