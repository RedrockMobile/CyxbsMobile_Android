# Schedule v2 旧事务与旧清单迁移

## 1. 目标与边界

Schedule v2 上线时，由 Android 与 iOS 客户端为当前登录账号执行一次旧数据迁移：

- 旧事务读取 `magipoke-reminder/Person/getTransaction`，只迁移当前学期的 `Transaction`；未上线的 `TimeTransaction` 不参与。
- 旧清单读取 `magipoke-todo/list`，接口已经返回每个 `todo_id` 的最新有效状态；已删除数据不迁移。
- 转换结果统一通过 `ScheduleRepository` 写入本地 Room。仓库会同时保存 typed pending，并按现有日常接口尝试上传。
- 不在旧服务与 Schedule 服务之间增加双写，不支持新旧客户端长期交替编辑同一账号。
- 迁移入口在 `2028-09-01 00:00 UTC` 自动失效，之后保留的代码不会再请求旧服务；旧读取接口至少保留到该期限。

迁移只解决旧数据进入新模型，不改变 Schedule v2 的正式同步协议。

## 2. 执行流程

```text
账号专属 Schedule Repository 初始化
              │
              ├─ 本地 Room 快照发布
              ├─ Schedule v2 首次 Sync
              ▼
检查当前时间是否早于迁移截止时间
              │
         过期 ├──────────────► 结束
              │未过期
              ▼
检查 AccountSettings 中的 migrationVersion
              │
       已完成 ├──────────────► 结束
              │未完成
              ▼
并行读取旧 Transaction 与旧 Todo
              │
              ├─ 任一接口失败：不标记，下次启动或重新登录重试
              ├─ 有旧事务但开学日期尚未就绪：本次协程等待课表仓库发布，账号 scope 结束则取消
              ▼
过滤已存在的确定性 ScheduleId
              │
              ▼
逐条执行 Create / SaveScheduleWithNewCategory
              │
              ├─ 远端失败：Room 与 pending 已保存，继续迁移
              ├─ 本地拒绝/写入失败：不标记完成
              ▼
保存旧清单置顶顺序
              │
              ▼
确认所有有效目标 ID 均存在于本地快照
              │
              ▼
migrationVersion = 1
```

迁移版本按学号写入 `AccountSettings.get(accountId)`。卸载重装会丢失本地版本，因此幂等性不能只依赖该字段。
Android 与 iOS 的 repository initializer 分别调用同一个 `commonMain` 协调器；迁移只在初始化时尝试一次，不常驻
监听网络。首次离线会保持版本未完成，留到下次账号仓库初始化重试；课表学期锚点属于同次迁移的必要输入，若
尚未发布则只在账号 scope 中挂起等待，不会因此再发起另一轮迁移。

## 3. 幂等标识

所有迁移资源使用确定性 UUIDv7。同一旧资源在重试、重装或另一台设备上会得到相同 ScheduleId；首次 Schedule Sync 已经下载过该 ID 时直接跳过。

| 来源 | UUIDv7 时间部分 | UUIDv7 摘要输入 |
|---|---|---|
| 旧清单 | 规范化后的 `last_modify_time` | `accountId + todo + todoId` |
| 整学期事务时间位置 | 学期第一周周一 | `accountId + affair + remoteId + day + beginLesson + period + all` |
| 指定周事务时间位置 | 该周发生日期 | `accountId + affair + remoteId + day + beginLesson + period + week` |

UUIDv7 后 74 位由 SHA-256 摘要提供，并显式设置 version=7 与 RFC 4122 variant。清单的提醒 ID 只需在日程内部稳定，使用由 ScheduleId 派生的固定字符串。

## 4. 无时间日程约束

`ScheduleTiming.Unscheduled` 没有提醒锚点，必须满足：

- `reminder=null`；
- `recurrence` 为空；
- `linkedToCourse=false`；
- 不投射到课表；
- 不导出到 Android Calendar Provider 或 iOS EventKit。

信息区仍展示“设置时间”“不提醒”“未关联课表”等入口，避免布局跳变。用户在无时间状态点击提醒或关联课表时，仅 Toast 提示“请先设置时间”，不进入设置子区域。用户将已有日程改为无时间时，保存边界会原子清理提醒、重复规则和课表关联。

提醒偏移量支持三种状态：

| UI 状态 | 领域值 | 含义 |
|---|---:|---|
| 不提醒 | 无 reminder | 不写系统提醒 |
| 准时 | `offsetMinutes=0` | 在日程开始/截止时刻提醒 |
| 提前 | `offsetMinutes>0` | 在开始/截止前指定分钟提醒 |

编辑器不能用 `0` 表示“未设置”：`-1` 仅存在于本地编辑态，保存时转换为 `reminder=null`；`0` 必须作为
`minutesBefore=0` 原样写入 Room 和 wire。新建日程默认仍为“不提醒”；用户从“不提醒/准时”切到“提前”时，
滚轮才以 10 分钟作为初值。从“不提醒”直接点击“准时”时以 0 申请权限，授权失败再回退为不提醒。

当前 Schedule v2 客户端领域校验、wire mapper、Room JSON、Android Calendar 投影与后端
`schedulev2.Reminder`/`schedulev2wire.ReminderInput` 均已接受非负值。Wire 中 `minutesBefore` 必填且没有业务默认值，
后端严格解码会区分“字段缺失”和显式 `minutesBefore=0`，防止旧请求漏传字段后被静默解释成准时提醒。因此准时
提醒不需要数据库或后端协议变更，只需补齐客户端选择入口并防止编辑态默认值覆盖 0。

## 5. 旧事务映射

旧事务没有清单完成态和分类，一律转换为课表事务日程。

### 5.1 通用字段

| 旧字段 | Schedule 字段 | 映射规则 |
|---|---|---|
| `remoteId` + 时间位置 | `id` | 使用第 3 节确定性 UUIDv7 |
| `title` | `title` | trim 后保存；空标题的旧脏数据跳过 |
| `content` | `description` | 原样保存 |
| `beginLesson` | `timing.start` | 使用课表现有节次开始时间换算 |
| `period` | `durationMinutes` | 使用开始/结束行的分钟差 |
| `day` | 星期 | 旧值 0～6 对应周一～周日 |
| `week` | 日期或 recurrence | 按下表处理 |
| `time <= 0` | `reminder` | `null` |
| `time > 0` | `reminder` | DEVICE，`offsetMinutes=time` |
| 无对应字段 | `kind` | `AFFAIR` |
| 无对应字段 | `todoState` | `null` |
| 无对应字段 | `linkedToCourse` | `true` |
| 无对应字段 | `categoryId` | `null` |

### 5.2 周数与多时间位置

| 旧数据情况 | 迁移结果 |
|---|---|
| `week=[0]` | 一条 `Timed + WEEKLY`，从第一周对应星期开始，`Count=maxWeek` |
| `week=[1,2,3]` | 第 1、2、3 周各生成一条非重复 Timed |
| `week=[1,3,5]` | 第 1、3、5 周各生成一条非重复 Timed，不构造复杂间隔规则 |
| 同一个事务有多个 `AtWhatTime` | 每个时间位置独立生成 Schedule |
| 同一天有多个不同节次 | 每个节次独立生成 Schedule |
| `week` 为空、周数越界 | 跳过该时间位置 |
| `beginLesson/period` 非法 | 跳过该时间位置 |
| 所有时间位置均无效 | 整条旧事务不迁移 |

事务拆分后仍共享标题、描述和提醒配置，但拥有不同的稳定 ScheduleId。

## 6. 旧清单映射

旧清单不存在真正的开始—结束时间段。它只保存截止时间、当前通知时间和重复模式，因此只迁移为 `Deadline` 或 `Unscheduled`，不会推断为 Timed 或 AllDay。

### 6.1 通用字段

| 旧字段 | Schedule 字段 | 映射规则 |
|---|---|---|
| `todo_id` | `id` | 使用第 3 节确定性 UUIDv7 |
| `title` | `title` | trim 后保存；空标题跳过 |
| `detail` | `description` | 原样保存 |
| `is_done=0` | `todoState` | `PENDING` |
| `is_done=1` 且不重复 | `todoState` | `COMPLETED` |
| `is_done=1` 且重复 | `todoState` | `PENDING`，重复系列不保存系列级完成态 |
| `type=study/学习` | `categoryId` | 学习 |
| `type=life/生活` | `categoryId` | 生活 |
| `type=other/其他/未知/空` | `categoryId` | 其他 |
| `is_pinned=1` | AccountSettings | 将新 ScheduleId 追加到现有置顶 ID 列表 |
| `is_over` | 不迁移 | 由新客户端按当前时间重新计算 |
| `last_modify_time` | `createdAt/updatedAt` | 兼容秒和毫秒时间戳；非法值使用迁移时刻 |
| 无对应字段 | `kind` | `TODO` |
| 无对应字段 | `linkedToCourse` | `false` |

迁移前先按名称复用首次 Sync 已有的“学习/生活/其他”分类；不存在时使用产品固定分类 ID，与第一条引用它的日程通过 `SaveScheduleWithNewCategory` 同批创建。

### 6.2 非重复清单

| `end_time` | `notify_datetime` | timing | reminder |
|---|---|---|---|
| 空 | 空 | Unscheduled | 无 |
| 有 | 空 | Deadline(end) | 无 |
| 有 | 等于 end | Deadline(end) | DEVICE，offset=0 |
| 有 | 早于 end | Deadline(end) | DEVICE，offset=`end-notify` |
| 空 | 有 | Deadline(notify) | DEVICE，offset=0 |
| 有 | 晚于 end | Deadline(end) | 丢弃非法提醒 |
| 时间文本非法 | 无其他合法时间 | Unscheduled | 无 |

“只有通知时间”不能保留为无时间日程，否则提醒永远没有作用；它会转成以通知时间为截止点的 Deadline，并使用准时提醒。

### 6.3 重复清单

| 旧 `repeat_mode` | RecurrenceRule |
|---:|---|
| 0 NONE | `null` |
| 1 DAY | DAILY |
| 2 WEEK | WEEKLY + `byWeekDays` |
| 3 MONTH | MONTHLY + 合法且去重后的 `byMonthDays` |
| 4 YEAR | YEARLY + 能无损表达旧 `M.d` 集合的 `byMonths × byMonthDays` |

旧清单的星期值来自 `Calendar.DAY_OF_WEEK`：1=周日、2=周一、3=周二、4=周三、5=周四、6=周五、7=周六。

| notify | end | timing 与重复边界 | reminder |
|---|---|---|---|
| 有 | 空 | Deadline(notify)，Never | DEVICE，offset=0 |
| 有 | 晚于 notify | Deadline(notify)，Until(end.date) | DEVICE，offset=0 |
| 有 | 等于 notify | 降为一次性 Deadline | DEVICE，offset=0 |
| 空 | 有 | 按规则求不早于迁移时刻的下一次，Until(end.date) | 无 |
| 空 | 空 | 按规则求不早于迁移时刻的下一次，00:00，Never | 无 |
| 重复参数非法 | 有合法时间 | 降为非重复 Deadline | 按合法通知计算 |
| 重复参数非法 | 无合法时间 | Unscheduled | 无 |

月重复直接保留旧 `day` 集合。年重复旧协议使用多个 `M.d` 日期，而新规则使用
`byMonths × byMonthDays`；只有笛卡尔积与旧日期集合完全一致时才迁移为 YEARLY。例如 `3.8,4.8` 可以无损映射，
`3.8,4.9` 会被新规则扩成四个日期，因此降为下一次一次性 Deadline，不能静默扩大重复范围。

## 7. 明确不迁移的派生或虚构语义

| 数据 | 处理 | 原因 |
|---|---|---|
| 旧 `is_over` | 忽略 | 属于随时间变化的派生状态 |
| 旧清单零点时间 | 仍是 Deadline | 零点不等于全天事项 |
| 无时间清单提醒 | 清空 | 没有提醒锚点，也不能写系统日历 |
| 旧清单课表关联 | 默认 false | 旧数据没有表达过该意图 |
| 旧事务清单关联 | 默认 null todoState | 旧事务没有完成态 |
| 任意周事务 | 拆成明确日期 | 避免构造无法精确表达的 RRULE |
| 已删除旧数据 | 不迁移 | 旧读取接口只返回当前有效逻辑数据 |

## 8. 失败与重试

- 读取任一旧接口失败：不写完成版本。
- Schedule 远端请求失败：只要 Room 已成功保存，继续迁移；现有 pending 会在网络恢复后同步。
- 单条旧记录无合法标题或时间位置：跳过，不让一条脏数据永久阻塞账号迁移。
- 本地命令被拒绝、Room 写入失败或预期 ID 未出现在快照：不写完成版本。
- 账号切换：迁移器使用初始化时冻结的账号专属 repository 和 session，不会把旧账号数据写入新账号。
- 首次离线或旧接口失败：本次初始化结束，不常驻监听网络；下次启动或重新登录再尝试。
- 超过 `2028-09-01 00:00 UTC`：在旧接口请求前直接结束，无论本地是否保存过迁移版本都不再迁移。

## 9. 上线顺序

1. 在 dev 部署 Schedule v2 后端和表结构。
2. Android 与 iOS production factory 确认使用账号专属 Room repository。
3. dev 客户端分别执行真实旧数据迁移，核对源数量、有效目标数量和跳过数量。
4. 后端 master 上线。
5. 发布带迁移器的新客户端。
6. 旧读取接口保留至迁移截止时间；届时客户端入口自动休眠，再安排旧表和旧接口下线。

## 10. 回归测试矩阵

- 旧事务：全周、指定周、多个时间位置、非法节次/星期/周数、稳定 ID、事务提醒。
- 旧清单时间：无时间、截止时间、仅通知时间、准时提醒、提前提醒、晚于截止的非法提醒。
- 重复规则：日/周/月/年重复、旧星期制转换、当天时刻边界、年重复无损/降级边界、非法选择器、重复结束边界。
- 历史字段：秒/毫秒时间戳、缺失可选字段、空/缺失/显式 null 数组、服务端冗余字段。
- 本地提交：重试和批内去重、分类按 ID/名称复用、同批分类只创建一次、远端失败但本地成功、
  本地日程或分类漏提交、账号不匹配与空数据。
- 跨平台入口：Android/iOS initializer 均调用共享协调器；共享测试覆盖期限截止前、截止瞬间与截止后。
- 新协议提醒：`minutesBefore=0` 在领域、wire 编解码和后端严格解码中保持为准时提醒；字段缺失仍拒绝。
