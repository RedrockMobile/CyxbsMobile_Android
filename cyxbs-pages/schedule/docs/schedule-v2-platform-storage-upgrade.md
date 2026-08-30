# Schedule v2 客户端存储

> 当前 Room v3 状态设计。旧 outbox、receipt、cursor、batch 和额外 tombstone 表均已删除。

## 1. 表

```text
schedule_v2_account_metadata
schedule_v2_category_state
schedule_v2_schedule_state
schedule_v2_occurrence_override_state
```

账号元数据只保存 `accountId` 与递增的 `localRevisionCounter`。

## 2. 每资源状态

三张资源表共享下列语义：

| 字段 | 含义 |
| --- | --- |
| identity | Category/Schedule 的 id；Override 的 scheduleId + occurrenceDate |
| remoteSnapshot | 最近一次确认的服务端完整 current，可空 |
| pendingOperation | UPSERT / DELETE，可空 |
| pendingSnapshot | UPSERT 的本地完整目标，可空 |
| pendingLocalModifiedAt | DELETE 的本地时刻，可空 |
| localRevision | 当前 pending 代数，可空 |

typed snapshot 通过 Room converter 编码为 JSON 列。业务代码直接维护 Kotlin DTO，不读取或拼接 JSON 字符串。

## 3. localRevision

`localRevision` 只用于防止旧请求响应清除新修改：

- 每次本地命令从账号 counter 取一个新 revision；
- 同一命令产生的相关资源可以共用 revision，便于日常 bridge 一次捕获；
- 请求捕获 revision R；
- 响应仅在当前行 revision 仍等于 R 时清 pending；
- 请求期间产生 U 时 revision 已变化，因此 U 被保留。

`localRevision` 不上传服务端，也不表示服务端版本。

## 4. remote 与 pending

投影规则：

```text
pending UPSERT → 显示 pendingSnapshot
pending DELETE → UI 隐藏
无 pending      → 显示 remoteSnapshot
两者都为空      → 不存在
```

接收服务端 current/tombstone 时只更新 remote；是否清 pending 由 uploadedRevision 比较决定。

## 5. 删除

DELETE pending 不保存版本，只保存 identity 与 `pendingLocalModifiedAt`。服务端确认 DELETED 后移除 remote 与 matching pending。不存在单独 tombstone 表。

## 6. 数据库版本

当前 schema version 为 8，变化是删除尚未上线的 `localBatchId` 列。Schedule v2 还没有发布，因此不编写从开发中间版本升级的 migration；本地旧开发库需要清除应用数据或重装。

## 7. 账号隔离

所有表都以 accountId 参与主键或查询条件。切换账号时 façade 取消旧 delegate 收集，并在发布快照与日历变更前再次核对 binding identity，避免旧账号迟到响应污染新账号。

## 8. 写入边界

- 本地命令与最终响应应用各使用一次 Room transaction；
- 网络调用不持有 repository mutex 或 Room transaction；
- 回包后必须重新读取当前状态再应用；
- 不在 Room 中保存 transport 重试次数、receipt 或请求历史。

## 9. 失败记录

业务拒绝与 HTTP400 的可编辑失败记录保存在账号 Settings，不属于 Room 同步状态。记录包含 scheduleId、失败时间、操作类型、源 Schedule、过滤后的 MutationRequest、reason 与安全 info。

记录只用于向用户解释并回到编辑器；真正的重试来源仍是 Room pending。
