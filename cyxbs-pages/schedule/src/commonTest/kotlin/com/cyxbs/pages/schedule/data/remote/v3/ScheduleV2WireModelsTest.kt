package com.cyxbs.pages.schedule.data.remote.v3

import com.cyxbs.components.config.serializable.defaultJson
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

/** 校验网络共用 JSON 配置下，必填 null 与可选字段省略的 wire 差异。 */
class ScheduleV2WireModelsTest {

  /** 必填 nullable 原子必须显式编码 data=null，避免服务端把“清空”误判成字段缺失。 */
  @Test
  fun requiredNullableAtomicDataIsEncodedAsNull() {
    val categoryJson = defaultJson.encodeToString(
      CategoryInput(
        id = "category-1",
        version = 0uL,
        name = AtomicField("分类", 1),
        color = AtomicField(null, 1),
        sortOrder = AtomicField(0, 1),
      ),
    )
    val scheduleJson = defaultJson.encodeToString(
      ScheduleInput(
        id = "schedule-1",
        version = 0uL,
        kind = ScheduleKind.AFFAIR,
        title = AtomicField("事务", 1),
        description = AtomicField("", 1),
        categoryId = AtomicField(null, 1),
        timing = AtomicField(
          TimingInput(TimingKind.TIMED, startAt = 1_000, endAt = 2_000),
          1,
        ),
        recurrence = AtomicField(null, 1),
        reminders = AtomicField(emptyList(), 1),
        todoState = AtomicField(null, 1),
        linkedToCourse = AtomicField(true, 1),
      ),
    )

    assertContains(categoryJson, "\"color\":{\"data\":null")
    assertContains(scheduleJson, "\"categoryId\":{\"data\":null")
    assertContains(scheduleJson, "\"recurrence\":{\"data\":null")
    assertContains(scheduleJson, "\"todoState\":{\"data\":null")
  }

  /** 联合类型和 Patch 的可选成员为 null 时必须省略，不能混同于可显式清空的 AtomicField.data。 */
  @Test
  fun optionalNullableMembersAreOmitted() {
    val timingJson = defaultJson.encodeToString(TimingInput(TimingKind.UNSCHEDULED))
    val recurrenceJson = defaultJson.encodeToString(
      RecurrenceInput(
        frequency = RecurrenceFrequency.DAILY,
        interval = 1,
        anchorDate = 0,
        weekdays = emptyList(),
      ),
    )
    val patchJson = defaultJson.encodeToString(FieldPatch<String>(PatchMode.INHERIT))

    assertFalse("startAt" in timingJson)
    assertFalse("endAt" in timingJson)
    assertFalse("dueAt" in timingJson)
    assertFalse("count" in recurrenceJson)
    assertFalse("untilDate" in recurrenceJson)
    assertFalse("value" in patchJson)
  }
}
