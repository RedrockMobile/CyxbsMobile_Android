# Schedule v2 字段组同步

> 原文基于已经废弃的批次协议，已停止维护。

当前字段合并直接由 typed resource 的 `AtomicField<T>` 表达：

- `data` 是完整原子值；
- `modifiedAt` 是该原子的客户端修改时刻；
- Schedule 的 title、description、categoryId、timing、recurrence、reminders、todoState、linkedToCourse 分别独立；其中 categoryId.data=null 表示未分组；
- OccurrenceOverride 的 status、timing、title、description、categoryId、reminders 分别独立；
- 服务端返回完整 canonical current，不返回字段 patch。

## nullable 与省略规则

以下字段是 required `AtomicField`，外层原子和 `modifiedAt` 都不能省略，但 `data` 必须允许显式为 `null`：

| 字段 | `data=null` 语义 |
| --- | --- |
| `Category.color` | 使用客户端默认配色 |
| `Schedule.categoryId` | 未分组 |
| `Schedule.recurrence` | 非重复日程 |
| `Schedule.todoState` | 不属于清单；仅事务允许，TODO 必须为 `OPEN` 或 `COMPLETED` |

其他 nullable 字段表示“该联合分支不使用此成员”，请求中应省略，而不是显式发送 `null`：

- `Timing.startAt/endAt/dueAt` 由 `kind` 决定：`TIMED/ALL_DAY` 使用 start/end，`DEADLINE` 使用 due，`UNSCHEDULED` 全部省略；
- `Recurrence.count/untilDate` 都可省略，但两者不能同时存在；
- `FieldPatch.value` 仅 `REPLACE` 携带，`INHERIT/CLEAR` 必须省略；清除 Override 分类等字段使用 `CLEAR`，不使用 `value=null`；
- 响应中的 `firstRecurrenceAnchorDate`、`reason/info/current/tombstone/version` 按结果分支省略。

空集合和空字符串不是 null：`reminders/weekdays` 发送 `[]`，允许为空的 description/message 发送 `""`。客户端网络 JSON 使用 `explicitNulls=true` 与 `encodeDefaults=false`，因此上述 required nullable 原子会编码 `data:null`，带默认 null 的可选成员会被省略；服务端严格解码规则必须与此表同步。

wire 结构以客户端 `ScheduleV2WireModels.kt` 与后端 `schedulev2wire/contract.go` 为准。同步应用规则见 [资源版本与同步流程](./schedule-v2-resource-version-sync-flow.md)。
