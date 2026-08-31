# Schedule v2 重复日程单次覆盖与周期破坏能力矩阵

> **状态：本文记录当前 canonical 合同；功能仍处于 dev 验证阶段，正式环境尚未上线。**
>
> 本文统一跨平台权威语义并记录 Android Calendar Provider 与 iOS EventKit 的平台能力。平台原始实例字段只能作为 adapter-only 信息，不能进入 wire identity，也不授权本次修改客户端代码、测试或部署。

---

## 1. 术语

| 标记 | 含义 |
| --- | --- |
| **权威合同支持** | Schedule v2 领域与 wire 明确表达，后端能够保存和校验。 |
| **adapter 已实现未启用** | 项目已有部分平台底层能力，但正式 export runtime 尚未启用。 |
| **平台支持未接入** | Android/iOS API 可表达，但项目没有正式写入链路。 |
| **不支持** | 权威合同刻意不表达，不能因平台 API 有能力就反向扩展 wire。 |

---

## 2. UTC 时间、anchor 与 occurrence identity

所有 wire 日期和时间都是 JSON 有符号 `int64` Unix 毫秒，不保存或同步时区：

```text
TIMED      → 实际 startAt/endAt
DEADLINE   → 实际 dueAt
ALL_DAY    → 单个 UTC 午夜 date 日期槽
重复锚点    → recurrence.anchorDate，稳定 UTC 午夜逻辑槽位
本次 identity→ occurrenceDate，parent rule 真实生成的 UTC 午夜逻辑槽位
```

第一次启用 recurrence 时确定稳定 `anchorDate`。重复规则仍生效期间，普通修改只能调整 timing 或规则结构，不能切换 anchor；否则既有 Override identity 会失去含义。清除 recurrence 会结束当前 occurrence 序列，`ScheduleCurrent.firstRecurrenceAnchorDate` 仍作为历史信息下发；之后在同一 Schedule identity 上重新启用 recurrence 时，以当前 timing 日期建立新的 anchor，并替换这份历史信息。

客户端物化 occurrence：

```text
startOffset = parent.startAt - parent.recurrence.anchorDate
occurrence.startAt = occurrenceDate + startOffset

endOffset = parent.endAt - parent.recurrence.anchorDate
occurrence.endAt = occurrenceDate + endOffset

dueOffset = parent.dueAt - parent.recurrence.anchorDate
occurrence.dueAt = occurrenceDate + dueOffset
```

offset 可以为负数或大于等于 24 小时，禁止取模或截断。parent 修改实际 timing 时，`anchorDate` 与已有 Override 的 `occurrenceDate` 都不变；不得从修改后的实际时间、设备显示日期或时区反推逻辑槽位。

客户端展开结果必须同时携带稳定 `occurrenceDate` 与实际时间，用户选择“本次”时复用已有 date-slot。

当前后端不物化 occurrence 实际时间；它根据 recurrence 规则验证 `occurrenceDate` membership。上述 offset 是客户端与跨端领域合同。

---

## 3. OccurrenceOverride typed 合同

OccurrenceOverride 没有独立 ID：

```text
identity = scheduleId + occurrenceDate
```

请求资源直接包含 `scheduleId` 和 `occurrenceDate`，不再使用 `Kind + ResourceIdentity + nullable body`：

```text
OccurrenceOverrideInput {
  scheduleId
  occurrenceDate

  status       AtomicField<OccurrenceStatus>
  timing       AtomicField<FieldPatch<Timing>>
  title        AtomicField<FieldPatch<String>>
  description  AtomicField<FieldPatch<String>>
  categoryId   AtomicField<FieldPatch<String>>
  reminder     AtomicField<FieldPatch<Reminder>>
}
```

六个原子彼此独立：

```text
status      = ACTIVE | COMPLETED | CANCELLED
timing      = INHERIT | REPLACE
title       = INHERIT | REPLACE
description = INHERIT | CLEAR | REPLACE
categoryId  = INHERIT | CLEAR | REPLACE
reminder    = INHERIT | CLEAR | REPLACE
```

Override 明确没有：

- 独立 exception ID；
- occurrence 序号；
- 独立 recurrence 规则。

timing `REPLACE` 上传完整 timing union，不能 `CLEAR` 或替换为 `UNSCHEDULED`，且必须与父系列保持 timing kind；
title `REPLACE` 必须是非空白文本，`CLEAR` 非法；categoryId `REPLACE` 必须引用同 owner 的 live Category。
`COMPLETED` 仍物化 occurrence；只有 `CANCELLED` 抑制该 date-slot。

完整 JSON 示例：

```json
{
  "scheduleId": "schedule-42",
  "occurrenceDate": 1776038400000,
  "status": {
    "data": "COMPLETED",
    "modifiedAt": 1775995200123
  },
  "timing": {
    "data": { "mode": "INHERIT" },
    "modifiedAt": 1775995200123
  },
  "title": {
    "data": { "mode": "REPLACE", "value": "复习高数" },
    "modifiedAt": 1775995200124
  },
  "description": {
    "data": { "mode": "CLEAR" },
    "modifiedAt": 1775995200125
  },
  "categoryId": {
    "data": { "mode": "REPLACE", "value": "category-study" },
    "modifiedAt": 1775995200125
  },
  "reminder": {
    "data": { "mode": "INHERIT" },
    "modifiedAt": 1775995200126
  }
}
```

AtomicField 的 `data` 与 `modifiedAt` 必须同时出现。所有 signed int64 时间戳都合法，包括 `0`；presence 不能用零值判断。

---

## 4. 服务端 live Override 保证

每条 live Override 必须满足：

```text
parent Schedule 存在且 live
parent 当前为 recurring
occurrenceDate 是 UTC 午夜
parent recurrence 真实生成 occurrenceDate
timing REPLACE 与 parent kind 一致且不是 UNSCHEDULED
categoryId REPLACE 引用同 owner 的 live Category
```

服务端不会自动删除不再使用的 live Override。同一 Schedule identity 的 recurrence 修改要求所有 live Override
仍属于新日期集合。WEEKLY 可以取消 anchorDate 当天对应的星期；anchorDate 只保存系列的稳定日期轴，不强制成为
每周选择项。

如果规则变化会使 live Override 失效，客户端需要先删除或迁移对应 Override，再提交规则更新；资源操作彼此独立，某项失败不会回滚其他已经成功的资源。失败操作仍保留在本地 pending/失败记录中，用户修正对应日程后再次提交。当前不检测历史 tombstone date-slot 重入，也不为该场景定义专用原因码。

Override tombstone 仍必须保留合法的 `scheduleId + UTC occurrenceDate` identity，但不要求 tombstone 日期继续属于 parent 当前 recurrence。重新启用 recurrence 不会复活或删除旧 tombstone；正常向未来建立的新序列会使用新的 occurrenceDate。

将某次恢复为系列默认状态时，保存 neutral live Override：

```text
status = ACTIVE
timing = INHERIT
title = INHERIT
description = INHERIT
categoryId = INHERIT
reminder = INHERIT
```

不要用 DELETE 表示“恢复默认”，因为 tombstone 不可复活，会阻止以后再次编辑同一 `scheduleId + occurrenceDate`。

---

## 5. 完整能力表

| 操作 | Schedule v2 权威合同 | Android Calendar Provider adapter | iOS EventKit adapter |
| --- | --- | --- | --- |
| **创建普通重复日程** | **权威合同支持**。按 UTC rule/weekday 从稳定 `anchorDate` 生成 `occurrenceDate`，客户端再叠加完整 timing offset。 | RRULE master 可表达；正式 master 导出已存在。 | `EKRecurrenceRule` master 可表达；正式 recurring 导出已存在。 |
| **修改整条重复日程标题** | **权威合同支持**。更新 Schedule `title` AtomicField。 | 更新 master 标题。 | 更新 recurring event 标题。 |
| **修改整条重复日程描述** | **权威合同支持**。更新独立 `description` AtomicField。 | 更新 master 描述。 | 更新 recurring event notes。 |
| **修改整条重复日程分类** | **权威合同支持**。更新 parent Schedule `categoryId` AtomicField。 | 分类是应用内字段，不投影 Provider。 | 分类是应用内字段，不映射 EventKit。 |
| **修改整条重复日程提醒** | **权威合同支持**。更新 parent 单个 reminder AtomicField。 | 更新 master reminder。 | 更新 recurring event alarm。 |
| **修改整条重复日程实际时间** | **权威合同支持**。更新独立 timing AtomicField，anchor 与 Override date-slot 不变。 | 更新 master timing；平台实例关联仅属 adapter。 | 更新 recurring event timing；平台 occurrence 信息仅属 adapter。 |
| **改变 recurrence 规则** | **权威合同支持**。同 identity 要求全部 live Override 仍属于新规则且不重入 tombstone date-slot；WEEKLY 不要求保留 anchor weekday。否则创建新 Schedule identity，并以独立操作迁移所需 Override。 | 更新或替换 master，最终只提交权威资源图。 | 可借助 recurring event/span，最终只提交权威资源图。 |
| **删除整条重复日程** | **权威合同支持**。Schedule 删除为 delete-wins；关联 Override 由 parent closure 一并失效。 | 删除受管 master。 | 删除 recurring event；具体 span 只属平台实现。 |
| **仅取消本次** | **权威合同支持**。写 `status.data=CANCELLED`，该 date-slot 不再物化。 | `ORIGINAL_ID + ORIGINAL_INSTANCE_TIME + STATUS_CANCELED` 可作为 adapter-only detached row；正式 exception 导出未启用。 | `.thisEvent` 删除可表达；项目未接入，`EKEvent.occurrenceDate` 仅 adapter-only。 |
| **仅完成本次** | **权威合同支持**。写 `status.data=COMPLETED`，仍按 parent timing 物化。 | 不应写 canceled row；正式 projection 尚未启用。 | EventKit 没有 Schedule 完成状态，不能映射为删除。 |
| **仅编辑本次标题** | **权威合同支持**。更新独立 title `FieldPatch` AtomicField。 | detached row 可表达；adapter 已实现未启用。 | `.thisEvent` detached override 可表达；平台支持未接入。 |
| **仅编辑本次描述** | **权威合同支持**。更新独立 description `FieldPatch` AtomicField。 | detached row 可表达；adapter 已实现未启用。 | `.thisEvent` notes override 可表达；平台支持未接入。 |
| **仅编辑本次分类** | **权威合同支持**。更新 categoryId `FieldPatch` AtomicField。 | 分类不投影 Provider。 | 分类不映射 EventKit。 |
| **仅编辑本次提醒** | **权威合同支持**。更新 reminder `FieldPatch` AtomicField。 | detached row 可表达；adapter 已实现未启用。 | `.thisEvent` alarm 可表达；平台支持未接入。 |
| **仅改期本次** | **权威合同支持**。更新 timing `FieldPatch` AtomicField，identity 仍是原始 `occurrenceDate`。 | Provider detached timing 可作为 adapter 投影。 | `.thisEvent` timing 可作为 adapter 投影。 |
| **恢复本次字段为系列默认** | **权威合同支持**。对应 timing/title/description/categoryId/reminder 设为 INHERIT，保留 neutral live Override。 | adapter 可删除或更新 detached row；平台投影是否保留不改变远端 identity。 | 平台可撤销 detached override；平台对象删除不等于远端 tombstone。 |
| **恢复已取消本次** | **权威合同支持**。status 恢复 ACTIVE；无其它覆盖时仍保留 neutral live Override。 | adapter 可删除 canceled row；正式 exception 导出未启用。 | 平台可恢复 occurrence；项目未接入。 |
| **从本次起编辑后续** | **权威合同支持**。客户端独立提交截断 A、创建 B 与 affected Overrides；B 使用新 identity/anchor，单项失败留在本地修正。 | 应用截断旧 master 并创建新 master。 | `.futureEvents` 可在平台内部拆分；远端只接收最终 A/B/Override 资源。 |
| **从本次起删除后续** | **权威合同支持**。客户端独立提交截断或删除 A 及 Override closure。 | 修改旧 master 结束边界。 | `.futureEvents` 可截断；远端只接收最终资源。 |
| **Override 跨 parent 或跨日期** | **权威合同支持迁移**。分别 `DELETE old + CREATE new`，identity 不可 PATCH，不承诺跨资源原子性。 | detached row 可删除后重建；原始实例字段仅 adapter-only。 | occurrence 可删除后重建；`occurrenceDate` 仅 adapter-only。 |
| **增加规则不生成的日期（RDATE）** | **不支持**。live Override 必须指向 parent rule 真实生成的 date-slot。 | Provider 支持 RDATE，但项目不使用。 | 公共 API 未公开等价 RDATE 集合。 |
| **排除多个指定日期（EXDATE）** | **不支持直接字段**。逐条 `CANCELLED` Override 表达。 | Provider 支持 EXDATE，但项目不使用。 | 公共 API 未公开等价 EXDATE 集合。 |
| **系统日历反向同步到 Schedule** | **不支持**。Schedule 是一期权威源。 | 不作为生产同步链路。 | 一期仅 `Schedule → EventKit` 单向导出。 |

---

## 6. recurrence 结构操作

后端不接收 SplitSeries、DeleteThisAndFollowing 等因果命令，只接收 typed 最终资源：

```text
从第 8 周起编辑后续：
  PATCH full Schedule A
  CREATE full Schedule B
  UPSERT/DELETE affected Overrides

从第 8 周起删除后续：
  PATCH/DELETE full Schedule A
  DELETE affected Overrides

同 identity recurrence 变化会使 live Override 失效或重入 tombstone slot：
  CREATE full Schedule B
  CREATE/UPSERT B 下仍需保留的 Overrides
  DELETE A 下全部 live Overrides
  DELETE Schedule A

Override 跨 parent/date：
  DELETE old scheduleId + occurrenceDate
  CREATE new scheduleId + occurrenceDate
```

服务端分别校验 Category 引用、parent closure、UTC date-slot membership、anchor history 和 tombstone。每个上传资源都在对应的 `upsertResults` 或 `deleteResults` 中得到结果；一个资源被拒绝不会让同请求内其他合法资源回滚。常见结果包括：

```text
APPLIED
ALREADY_SATISFIED
REJECTED
```

客户端接受成功结果和服务端 canonical current；失败项继续留在本地 pending 与失败记录中，不保存 lineage 或 receipt。

---

## 7. Android adapter 边界

Android Calendar Provider 可使用 `ORIGINAL_ID`、`ORIGINAL_INSTANCE_TIME`、`ORIGINAL_ALL_DAY` 和状态字段建立 detached exception row。这些字段只能维护平台关联：

```text
权威定位：scheduleId + occurrenceDate
平台定位：adapter 自行维护 ORIGINAL_* 映射
```

平台返回的 original instance time 不得覆盖或重算 wire `occurrenceDate`。Schedule v2 的单次改期仍以
原 date-slot 为 identity；Provider detached timing 只负责投影该 timing patch。

当前 Android gateway 已有部分创建、替换、取消与回读能力，但正式 outbound runtime 尚未启用完整 OccurrenceOverride projection，必须继续 fail-closed，不能只导出 master 而遗漏本次取消/覆盖。`COMPLETED` 必须保持 occurrence 可见，不能映射成 canceled row。

---

## 8. iOS EventKit adapter 边界

EventKit 的 `.thisEvent` 可操作选中 occurrence，`.futureEvents` 可操作选中 occurrence 及以后。`EKEvent.occurrenceDate` 与 `isDetached` 只描述平台对象，是 adapter-only 信息：

- 不能进入 Schedule v2 wire identity；
- 不能按设备时区反算 UTC date-slot；
- 不能让 `.thisEvent` 的平台 identity 或额外字段改变权威合同；
- `.futureEvents` 的平台内部拆分最终只映射为 A/B/Override 独立资源操作。

当前 iOS bridge 尚未接通 Schedule OccurrenceOverride 到 `.thisEvent` 的正式写入路径；foundation 应继续保守
返回 Unsupported。未来接通后可投影单次标题、描述、提醒、取消与 timing；categoryId 是应用内分类，不映射 EventKit。

---

## 9. 分层

```text
跨平台领域权威：
  Schedule(actual timing AtomicField
           + recurrence AtomicField
           + stable first anchor history)
  + OccurrenceOverride(scheduleId + UTC occurrenceDate
                        + status/timing/title/description/categoryId/reminder AtomicField)

客户端同步状态：
  remoteSnapshot
  + 至多一份 pendingSnapshot
  + localRevision compare-and-clear

远端同步：
  typed confirmed/upserts/deletes
  + /v2/schedule-mutations
  + 每项独立的 typed operation results
  + canonical resource/tombstone delta

Android adapter-only：
  RRULE master
  + detached exception row
  + ORIGINAL_* 平台关联

iOS adapter-only：
  EKRecurrenceRule master
  + .thisEvent detached occurrence
  + .futureEvents span
```

平台对象可以由权威资源图重建，但平台字段不能反向决定 wire identity 或扩展原子集合。

---

## 10. 代码与平台资料索引

### 当前项目关键代码

| 内容 | 位置 |
| --- | --- |
| occurrence exception、`FieldPatch` 与 status | `src/commonMain/.../domain/model/ScheduleModels.kt` |
| recurrence 展开、取消过滤、稳定 identity | `src/commonMain/.../domain/recurrence/RecurrenceEngine.kt` |
| THIS_ONLY / THIS_AND_FOLLOWING 编辑与删除路由 | `src/commonMain/.../ui/edit/ScheduleEditRouting.kt` |
| SplitSeries / DeleteThisAndFollowing 领域命令 | `src/commonMain/.../domain/repository/ScheduleRepository.kt` |
| common 投影 capability gate | `src/commonMain/.../domain/calendar/ScheduleCalendarProjection.kt` |
| Android native occurrence exception 写入 | `src/androidMain/.../calendar/AndroidScheduleCalendarGateway.kt` |
| Android export projection 调用 | `src/androidMain/.../calendar/ScheduleCalendarExportCoordinator.kt` |
| iOS exception fail-closed mapper | `src/iosMain/.../calendar/IosEventKitCalendarAdapterFoundation.kt` |
| iOS recurring save/remove span bridge | `src/iosMain/.../calendar/IosEventKitStoreBridge.kt` |

这些客户端路径仍是旧实现现状，不代表已经满足本文 typed 远端合同。

### 平台公开资料

- Android：[CalendarContract.Events](https://developer.android.com/reference/android/provider/CalendarContract.Events)、[EventsColumns](https://developer.android.com/reference/android/provider/CalendarContract.EventsColumns)、[Instances](https://developer.android.com/reference/android/provider/CalendarContract/Instances)；
- Apple：[Creating events and reminders](https://developer.apple.com/documentation/eventkit/creating-events-and-reminders)、[EKSpan.thisEvent](https://developer.apple.com/documentation/eventkit/ekspan/thisevent)、[EKSpan.futureEvents](https://developer.apple.com/documentation/EventKit/EKSpan/futureEvents)、[EKEvent.occurrenceDate](https://developer.apple.com/documentation/eventkit/ekevent/occurrencedate)、[EKEvent.isDetached](https://developer.apple.com/documentation/eventkit/ekevent/isdetached)。
