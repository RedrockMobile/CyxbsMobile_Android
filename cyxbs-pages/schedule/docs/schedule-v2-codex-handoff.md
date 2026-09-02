# Schedule v2 Codex 交接

> 更新时间：2026-08-30。本文只记录当前实现，不保留旧 Claude 方案。后端与客户端尚未正式上线，可以进行不兼容修改。

## 1. 当前状态

Schedule v2 主体功能已完成，同步协议也已从 AtomicBatch 收敛为逐资源请求与逐资源结果。已明确删除且不应恢复的设计包括 cursor、outbox、receipt、batch、checkpoint、journal、reservation 与服务端 semantic command。

当前保留：

- typed Category、Schedule、OccurrenceOverride；
- 资源内统一 `version` 与逐字段 `AtomicField`；
- 每个 identity 一份 remote snapshot 和至多一份 pending；
- `localRevision` 的 R→U 保护；
- 日常 CRUD 与完整 Sync；
- Android/iOS 单向导出系统日历；
- 旧事务与旧清单的一次性迁移；
- 可编辑的同步失败记录。

## 2. 当前后端合同

事实源：

```text
/Users/guoxiangrui/GolandProjects/magipoke-todo/SCHEDULE_BACKEND_DESIGN.md
```

接口：

```text
POST   /v2/schedule-mutations
POST   /v2/schedules
PUT    /v2/schedules
DELETE /v2/schedules
```

完整 Sync 上传每类资源的 `confirmed/upserts/deletes`；响应返回：

- `confirmedResults`：与 confirmed 逐项对齐；
- `discoveredResults`：其他设备新增、当前客户端未知的 live 资源；
- `upsertResults`：与 upserts 逐项对齐；
- `deleteResults`：与 deletes 逐项对齐。

日常接口没有 confirmed/discovered，只返回 upsert/delete 结果。三个资源类型在一次请求内可以同时出现，但服务端逐资源独立处理，不存在跨资源事务。

关键约束：

- 新建资源 `version=0`，修改资源携带当前正版本；
- Category/Schedule DELETE 不上传 version；OccurrenceOverride DELETE 表示还原单次调整，必须上传当前正 version；
- 重复 DELETE 统一视为 `DELETED`；
- Category/Schedule tombstone 不带 version 且 identity 不可复活；OccurrenceOverride tombstone 保留正 version，可由完整 Override 快照按该版本重建；
- Override tombstone 只保留 `scheduleId/occurrenceDate/version/deletedAt`，不得保存删除前的时间、标题等业务补丁；
- 服务端不感知“此次及以后”，客户端拆成普通资源增删改；
- 结构合法且已处理的响应统一 HTTP 200 / `status=10000`；
- 单资源业务错误使用 `result=REJECTED + reason + 可选安全 info`，不影响同请求其他资源；
- 畸形 JSON、未知字段、必填字段缺失等整体形状错误才返回 HTTP 400。

## 3. 客户端协议代码

`data/remote/v3`：

- `ScheduleV2WireModels.kt`：typed DTO；
- `ScheduleV2ApiService.kt`：Ktorfit 四个接口；
- `KtorScheduleV2Gateway.kt`：统一 `ApiWrapper` 与 HTTP/transport 分类；
- `ScheduleV2DiagnosticLog.kt`：只记录标题、日期、时间、周期和安全失败信息，不记录凭证或完整 payload。

`data/repository/v3`：

- `ScheduleV2LocalCommandReducer`：把 UI 命令写成本地最终资源状态；
- `ScheduleV2SyncCapture`：捕获全部当前 pending 与 uploadedRevision；
- `ScheduleV2DailyMutationBridge`：捕获本次 localRevision 对应的普通请求；
- `ScheduleV2ResponseApplier`：逐项应用 confirmed/discovered/upsert/delete 结果；
- `ScheduleV2SnapshotProjector`：把 Room 状态投影为领域快照。

## 4. Room 状态

数据库只保留四张表：

```text
schedule_v2_account_metadata
schedule_v2_category_state
schedule_v2_schedule_state
schedule_v2_occurrence_override_state
```

每个资源行保存：

- `remoteSnapshot`：最后确认的服务端 current；
- `pendingOperation`：UPSERT 或 DELETE；
- `pendingSnapshot`：UPSERT 的完整本地目标；
- `pendingLocalModifiedAt`：DELETE 时刻；
- `localRevision`：本地 pending 代数。

不再保存 `localBatchId`。本次协议尚未上线，当前 Room schema 重新固定为初始 version 1，不保留开发期历史版本或 migration。Android、Desktop 与 iOS 在缺少迁移时统一清库重建；远端保有完整数据，本地临时日程按产品约定允许丢弃。

## 5. Repository 行为

### 初始化 / 完整 Sync

1. 读取 Room 并先发布本地快照；
2. 在 mutex 外请求 `/v2/schedule-mutations`；
3. 回包后重新读取最新 Room；
4. 逐项应用结果；
5. 在一次 Room transaction 中替换账号状态。

### 日常 CRUD

1. 命令先写 Room 并发布 UI；
2. 捕获当前 localRevision 的 Category/Schedule/Override pending；
3. 通过 POST/PUT/DELETE 日常接口上传；
4. 成功项写 canonical remote，并仅在 localRevision 未变化时清 pending；
5. REJECTED 项保留 pending，并写入可编辑失败记录；
6. transport/5xx/timeout/HTTP400 均保留 pending。

HTTP 400 属于确定性请求失败，但客户端仍保留本地数据，避免旧迁移或本地输入被直接丢弃。失败页记录操作时间、源 Schedule、请求片段、reason/info；用户修改或删除成功后自动移除。

### R→U

R 上传期间发生本地 U 时，R 响应可以更新 remote，但不能清除更高 localRevision 的 U。U 继续作为 pending，等待下一次日常请求或网络恢复 Sync。

## 6. 部分成功

同一响应先应用所有合法 canonical 结果，再汇总 REJECTED：

- accepted 资源按 uploadedRevision compare-and-clear；
- rejected 资源保留 pending；
- 只为被拒绝的 Schedule 或相关 Override 生成失败记录；
- 合法 HTTP 200 说明后端可达，repository 状态保持 `Ready(pendingCount)`，不能标成远端不可用。

## 7. 业务模型边界

- recurrence 只支持 DAILY / WEEKLY；
- `minutesBefore=0` 表示准时提醒；
- UNSCHEDULED 不导出日历，点击提醒或关联课表时由 UI toast 说明；
- `kind=TODO|AFFAIR` 创建后不可修改；
- TODO 完成后暂不显示在课表；
- AFFAIR 即使关联清单并完成，仍显示在课表；
- OccurrenceOverride identity 永远是原始 `scheduleId + occurrenceDate`。

## 8. 日历与迁移

当前仅支持 Schedule 到系统日历的单向导出，不接收系统日历反向修改。

Android 与 iOS 在各自平台入口触发旧数据迁移，映射逻辑位于 commonMain。迁移版本记录在 AccountSettings；失败下次初始化重试，2028-09-01 UTC 后停止访问旧服务。旧数据迁移成功后不删除旧服务数据。

## 9. 验证状态

本轮协议改造已验证：

- 后端 `go test ./schedulev2 ./schedulev2wire ./dao`；
- 后端 `go test ./service -run '^TestScheduleV2'`；
- 客户端 Android main 编译；
- 客户端 Desktop 297 个测试；
- Android host 302 个测试；测试配置让 `android.jar` 的日志 stub 返回默认值，不影响生产日志。

- iOS Simulator 测试源码编译；
- 两端旧协议术语与 diff 已完成检查。

## 10. Git 边界

客户端分支：`guoxiangrui/feature/schedule`。

后端分支：`dev/test`。

本轮开始前已分别提交失败记录与参数错误改进。当前协议重构尚未提交，完成代码、测试与文档核对后再单独提交。
