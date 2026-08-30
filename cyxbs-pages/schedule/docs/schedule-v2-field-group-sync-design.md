# Schedule v2 字段组同步

> 原文基于已经废弃的批次协议，已停止维护。

当前字段合并直接由 typed resource 的 `AtomicField<T>` 表达：

- `data` 是完整原子值；
- `modifiedAt` 是该原子的客户端修改时刻；
- Schedule 的 title、description、categoryId、timing、recurrence、reminders、todoState、linkedToCourse 分别独立；
- OccurrenceOverride 的 status、timing、title、description、categoryId、reminders 分别独立；
- 服务端返回完整 canonical current，不返回字段 patch。

wire 结构以客户端 `ScheduleV2WireModels.kt` 与后端 `schedulev2wire/contract.go` 为准。同步应用规则见 [资源版本与同步流程](./schedule-v2-resource-version-sync-flow.md)。
