# Schedule v2 暂不支持的边界

> 原文包含大量尚无真实场景的推演，已按当前产品范围收敛。

当前明确不支持：

- RDATE；
- 删除后的同 identity 复活；
- 跨资源原子事务；
- receipt、history、cursor、协议 rollout 状态机；
- Web 离线编辑；
- 系统日历到 Schedule 的反向同步；
- 为未观察到的超大请求或极端并发增加额外恢复协议。

普通断网、超时、5xx、HTTP400、逐资源 REJECTED、R→U 与旧数据迁移失败已经由 Room pending 和失败记录覆盖。详见 [资源版本与同步流程](./schedule-v2-resource-version-sync-flow.md)。
