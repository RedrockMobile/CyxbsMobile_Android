# Schedule v2 资源版本与同步流程

> 当前事实源。旧 AtomicBatch、relatedUpserts、relatedDeletes、receipt 与 cursor 设计已废弃。

## 1. 本地状态

每个 Category、Schedule、OccurrenceOverride identity 只保存：

```text
remoteSnapshot?
pendingOperation?       // UPSERT | DELETE
pendingSnapshot?        // UPSERT 时的完整目标资源
pendingLocalModifiedAt? // DELETE 时刻
localRevision?          // 本地 pending 代数
```

显示状态优先使用 pending；没有 pending 时使用 remote。DELETE pending 会立即从 UI 隐藏对应资源。

## 2. 版本

- remote resource 自带服务端 `version`。
- 新建 pending 使用 `version=0`。
- 编辑已有资源时，pending 使用最后确认 remote 的正版本。
- DELETE 不带 version。
- accepted response 返回完整 current，current.resource.version 是下一次修改基线。
- tombstone 不带 version。

## 3. 日常请求

本地命令完成后，bridge 捕获本次 `localRevision` 对应的全部三类 pending，生成：

```text
MutationRequest {
  requestId
  categories { upserts[]; deletes[] }
  schedules { upserts[]; deletes[] }
  occurrenceOverrides { upserts[]; deletes[] }
}
```

路由选择：

- 含新资源：POST `/v2/schedules`；
- 仅修改：PUT `/v2/schedules`；
- 主操作为删除：DELETE `/v2/schedules`。

路由不改变协议语义；服务端始终逐资源处理。

## 4. 日常响应

```text
MutationResponse {
  requestId
  categories { upsertResults[]; deleteResults[] }
  schedules { upsertResults[]; deleteResults[] }
  occurrenceOverrides { upsertResults[]; deleteResults[] }
}
```

每个结果列表必须与对应请求列表数量、顺序和 identity 完全一致。任何错位、缺项或重复都按 InvalidResponse fail-closed，不猜测服务端意图。

结果处理：

| result | remote | pending |
| --- | --- | --- |
| CREATED / APPLIED | 接受 current | localRevision 仍等于 uploadedRevision 时清除 |
| ALREADY_EXISTS / ALREADY_SATISFIED / SERVER_WON | 接受 current | 同上 |
| DELETED | 接受 tombstone 并删除 remote | 同上 |
| RESOURCE_DELETED | 接受 tombstone | 同上 |
| REJECTED | 可接受返回的 current/tombstone 作为 remote | 永远保留本地 pending |

重复删除也返回 DELETED，不存在客户端 ALREADY_DELETED 分支。

## 5. 完整 Sync

请求：

```text
confirmed[] = 所有 live remote identity + version
upserts[]   = 所有 UPSERT pending
deletes[]   = 所有 DELETE pending
```

响应：

```text
confirmedResults[]  // 与 confirmed 对齐
discoveredResults[] // 客户端未知的其他 live 资源
upsertResults[]     // 与 upserts 对齐
deleteResults[]     // 与 deletes 对齐
```

`confirmedResults`：

- CONFIRMED：版本一致，只确认版本；
- CHANGED：使用 current 更新 remote；
- DELETED：使用 tombstone 删除 remote。

`discoveredResults` 直接加入 remote，但不能覆盖同 identity 的本地 pending 显示。

## 6. R→U

```text
t0  本地写入 R，localRevision=1
t1  捕获并上传 R(uploadedRevision=1)
t2  请求期间用户写入 U，localRevision=2
t3  R 响应回来，更新 remote
t4  compare-and-clear 发现 2 != 1，不清 U
t5  U 在下一次日常请求或 Sync 中上传
```

因此服务端回包永远不能直接无条件清 pending。

## 7. 部分成功

一次请求中的每个资源独立处理。客户端按以下顺序：

1. 校验所有结果与请求严格关联；
2. 收集 current/tombstone candidate；
3. 应用 accepted 和 rejected 携带的权威 remote；
4. 只清 accepted 且 revision 未变化的 pending；
5. 为 REJECTED Schedule/Override 写失败记录；
6. 返回业务 Failure 供调用方提示，但 repository 仍保持 Ready。

不存在整请求回滚，也不保存 batch。

## 8. “仅此次 / 此次及以后 / 整个系列”

服务端不接收语义命令：

- 仅此次：一个 OccurrenceOverride upsert/delete；
- 此次及以后：客户端把旧系列截断、新系列创建、相关 Override 变化拆成普通资源操作；
- 整个系列：修改原 Schedule，必要时修改普通 Override。

若其中一项 REJECTED，其余成功项照常落服务端；失败项继续保留在本地并显示在失败页，用户修正后再次提交。

## 9. 失败分类

| 情况 | 是否保留 pending | 是否写可编辑失败记录 |
| --- | --- | --- |
| HTTP 200 + accepted | 否，除非已有更高 revision | 否 |
| HTTP 200 + REJECTED | 是 | 是 |
| HTTP 400 | 是 | 是 |
| timeout / 断网 / 5xx | 是 | 否，等待重试 |
| HTTP 200 结果错位或无法解码 | 是 | 作为 InvalidResponse 诊断 |

失败记录按 scheduleId 保存源 Schedule、过滤后的 MutationRequest、操作类型、时间、reason 与安全 info。后续修改时刷新源数据；修改或删除成功后移除。

## 10. 网络恢复

网络恢复只在当前账号仍有 pending 时触发完整 Sync。没有 pending 时不主动同步，避免无意义请求。

## 11. 不支持

- 同 identity 删除后复活；
- 服务端 semantic command；
- 跨资源事务；
- receipt/history/cursor；
- Web 离线写入；
- 为极端大请求增加额外状态机。
