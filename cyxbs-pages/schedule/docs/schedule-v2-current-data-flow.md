# Schedule v2 当前数据流

> 原文描述的旧 outbox、receipt、cursor 与批次链路已经删除，不再作为实现依据。

当前数据流：

```text
UI command
→ reducer 写 Room pending + localRevision
→ daily bridge 或完整 Sync 捕获 pending
→ Ktorfit typed request
→ 逐资源 typed result
→ applier 更新 remote 并 compare-and-clear
→ Room transaction
→ snapshot projector 发布 UI
```

完整状态与边界见：

- [资源版本与同步流程](./schedule-v2-resource-version-sync-flow.md)
- [客户端存储](./schedule-v2-platform-storage-upgrade.md)
- [Codex 交接](./schedule-v2-codex-handoff.md)
