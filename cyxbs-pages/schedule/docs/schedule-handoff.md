# Schedule 当前交接状态

> 更新时间：2026-09-03。本文只记录当前实现，不保留已经废弃的历史方案。

## 已完成实现

- 清单、课表事务统一使用 Schedule 领域模型；`kind` 区分 TODO/AFFAIR，`todoState` 表达是否属于清单及完成态，`linkedToCourse` 表达用户是否要求课表投影。
- 客户端采用 Room remote/pending 双快照，日常增删改本地优先；失败保留 pending，并支持失败记录定位和修复。
- 首次进入、存在 pending 的网络恢复和手动操作通过 `/schedule/sync` 完整对账。
- Category 与 OccurrenceAdjustment 使用客户端 UUID v7 本地 ID和服务端自增 ID；Schedule 使用客户端 UUID，普通新增为 v7，旧数据迁移为确定性 v5。
- 接口使用逐资源 `confirmed/upserts/deletes` 与 `confirmedResults/discoveredResults/upsertResults/deleteResults`，支持同请求部分成功。
- 重复规则支持日、周、月、年；单次调整使用不可变原始日期槽和字段级 AtomicField/FieldPatch。
- 分类、日程和单次调整使用物理删除；还原单次调整即删除调整资源。
- Android/iOS 旧清单与旧 Transaction 共享 commonMain 映射并按账号执行一次性迁移。
- Android/iOS 系统日历保持单向投影；时间点使用零时长事件，提醒支持准时和提前分钟。
- 课表按 AllDay、Timed、Deadline 三层投影，事务与清单分别渲染，只有当前登录账号主页课表允许创建和展示。

## 当前协议事实源

- [Schedule 接口协议](schedule-protocol.md)
- [重复日程与单次调整](schedule-recurrence.md)
- [Schedule 存储与旧数据迁移](schedule-storage-and-migration.md)
- [Schedule 系统日历投影](schedule-calendar-export.md)
- [Schedule Android 验收清单](schedule-android-e2e-test-runbook.md)

## 验证状态

新数据结构完成后已执行的聚焦验证：

```text
./gradlew :cyxbs-pages:schedule:desktopTest :cyxbs-pages:course:view:compileKotlinDesktop :cyxbs-pages:course:compileKotlinDesktop :cyxbs-pages:discover:compileKotlinDesktop --quiet
./gradlew :cyxbs-pages:schedule:compileAndroidMain :cyxbs-pages:course:view:compileAndroidMain :cyxbs-pages:course:compileAndroidMain --quiet
./gradlew :cyxbs-pages:schedule:compileKotlinIosSimulatorArm64 --quiet
/Users/guoxiangrui/sdk/go1.26.4/bin/go test ./schedule ./schedulewire ./dao
/Users/guoxiangrui/sdk/go1.26.4/bin/go test ./service -run '^Test(Schedule|PublicSchedule)' -count=1
```

Android 真机最终测试尚未执行，必须等待代码审批后按验收清单逐项进行。当前客户端和后端修改均保持未提交状态。

## 维护边界

- 不重新引入 batch、receipt、cursor、逐条 outbox、远端墓碑、摘要键或幂等请求表。
- 不为少见的多设备竞争增加额外状态机；字段级 modifiedAt、资源 version 和后续 Sync 已覆盖当前产品需要。
- `UNSCHEDULED` 只服务旧清单迁移，新 UI 不创建。
- 账号隔离必须冻结 AccountSession；不能根据请求体接受 owner 或学号。
- 日历适配器中的 occurrence exception 是平台术语，不应重新暴露成 Schedule 业务模型。
