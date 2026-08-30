# Schedule v2 创建、修改与同步

> 本文原有方案已废弃。旧 generic mutation、receipt、batch 与顶层业务拒绝状态不再使用。

当前创建、修改、删除与完整同步合同统一见：

- [资源版本与同步流程](./schedule-v2-resource-version-sync-flow.md)
- [Codex 交接](./schedule-v2-codex-handoff.md)
- 后端事实源：`/Users/guoxiangrui/GolandProjects/magipoke-todo/SCHEDULE_BACKEND_DESIGN.md`

当前结论只有三点：

1. 日常 CRUD 上传三类 typed 资源的普通 upsert/delete；
2. 完整 Sync 额外上传 confirmed，并接收 confirmedResults/discoveredResults；
3. 每个资源独立成功或 REJECTED，合法请求顶层始终 status=10000。
