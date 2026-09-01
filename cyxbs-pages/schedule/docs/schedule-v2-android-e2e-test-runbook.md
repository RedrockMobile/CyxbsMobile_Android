# Schedule v2 Android 端到端验收清单

## 1. 目标与执行约定

本清单用于在真实 Android 设备上验收 Schedule v2 的清单、课表事务、同步、系统日历与旧数据迁移。
每完成一项立即把 `- [ ]` 改为 `- [x]`；失败项保持未勾选，并在第 15 节登记问题、证据和修复提交。

- 测试设备：小米 `2211133C / fuxi`，ADB serial `3ad7ce04`。
- 测试应用：`com.mredrock.cyxbs.test`，使用项目的 `.codex/scripts/install-test-debug.sh` 覆盖安装。
- 测试后端：当前测试包配置的 dev/test 环境；从设备读取的登录凭证只用于本轮请求，不打印完整值。
- 当前应用内均为测试数据，可按用例直接创建、修改和清理；新增数据仍使用 `E2E-SV2-0831-` 前缀方便核对。
- 只允许覆盖安装，不卸载应用、不执行整包 `pm clear`，避免触发小米首次安装授权。
- 每个远端写操作至少核对：界面结果、本地 Room/pending、Schedule 网络日志、下一次 Sync 返回。
- 迁移版本和测试注入属于临时代码，验证结束必须还原；不得进入正式提交。
- 不备份应用数据，也不单独保存测试数据；过程证据只放本机临时目录并在验收后清理。
- 修复按问题相关性分批提交，不把测试文档勾选与无关代码混入功能修复。
- 不测试多设备同时修改、请求中途杀进程等低频极端场景；重点覆盖学生单设备日常使用。
- 账号隔离只审查 repository/session 冻结与既有自动化测试，不在设备上反复切换账号。
- 换设备后的远端恢复只做代码链路与自动化测试，不定向清理当前手机数据模拟第二台设备。

## 2. 环境与基线

- [x] E00-01 ADB 能稳定识别唯一目标设备，设备已解锁，应用允许调试。
- [x] E00-02 使用项目安装脚本覆盖安装测试包，并确认启动的是正式 Schedule 清单入口。
- [x] E00-03 记录当前分支、HEAD、应用版本、后端 host、账号标识的脱敏摘要。
- [x] E00-04 记录初始本地日程/分类/pending/失败记录数量，以及首次 Sync 的远端 inventory 数量。
- [x] E00-05 验证日志不会输出完整 token、Authorization、Cookie 或完整敏感 payload。
- [x] E00-06 确认本轮新增数据使用统一前缀，便于逐项核对和最终清理。
- [x] E00-07 验证正式 `cyxbs://schedule`、设置、分类和失败记录路由均打开正确页面。（分别冷启动 `cyxbs://schedule`、`cyxbs://schedule/settings`、`cyxbs://schedule/category`、`cyxbs://schedule/failures`，页面标题依次为“邮子清单”“邮子清单设置”“分组管理”“失败记录”；另从清单页热跳课表、再从课表热跳清单均成功。过程中分别修复监听离场导致热跳失效的 BUG-006、裸 `cyxbs://course` 因学号必填而被拒绝的 BUG-009，以及栈顶相同日程 deeplink 无法再次定位的 BUG-013。裸课表 URI 现回退到当前账号；系统日历格式 URI 在冷启动、热启动和完全相同参数重复投递时均能定位 `222`，重复投递后一次返回即回到主页。）
- [x] E00-08 检查账号级 repository/session 的创建与销毁代码，确认不会把数据写入其他账号。

## 3. 分类管理

- [x] C01 新建分类 `E2E-SV2-0831-分类A`，选择一组背景色和字体色，列表立即出现且远端创建成功。
- [x] C02 新建分类 `E2E-SV2-0831-分类B`，重启应用并 Sync 后仍存在。
- [x] C03 尝试新建同名分类，客户端阻止提交；使用直连接口重复创建时后端也按业务失败返回。（设备端输入已有 `E2E-SV2-0831-CatA-Renamed` 后即时提示“已存在同名分组”且无法保存；后端 `duplicate category name` 图校验测试确认 trim 后的同名资源被拒绝。）
- [x] C04 将分类 A 改名为 `E2E-SV2-0831-分类A改`，已有日程引用保持同一 categoryId。
- [x] C05 修改分类 A 配色，分类引用、时间轴和课表投射同步使用新背景色/字体色。（`E2E-SV2-0831-CatA-Renamed` 从亮紫改为亮橙后 UPDATE 成功、pending=0；Room 保存 `#FFFFC98F/#FF6B3700/#BF5C3514`，`E2E-DOUBLE-SAVE` 仍引用同一 categoryId。时间轴与课表投射均从分类 JSON 解析背景/文字色；普通清单卡片按设计保留状态色，只展示分类标签，不把整张卡片改为分类色。）
- [x] C06 拖动调整 A/B 顺序，退出重进及重新 Sync 后顺序保持。
- [x] C07 删除未被引用的分类 B，本地与远端均删除。
- [x] C08 删除被日程引用的分类 A 时，界面给出明确处理结果，不产生悬空 categoryId。（引用中的 `E2E-SV2-0831-CatA-Renamed` 被客户端拦截，提示“仍有 1 项日程使用该分组”，未发起删除请求。）
- [x] C09 固定分类“学习/生活/其他”不提供删除入口。
- [x] C10 分类名称首尾空格会被规范化，空白名称无法保存，中文与常用符号可正常使用。（设备端纯空格时保存按钮保持禁用；编辑器统一以 `name.trim()` 提交并按 trim 后名称判断空白/同名，现有中文内置分类和带连字符测试分类均可正常往返。）
- [x] C11 断网新增、改名、换色和调整顺序时本地立即生效，恢复网络后逐项同步成功。（应用级 FakeGateway 连续返回 TransportFailure；真实 Room 仓库仍立即发布创建、最终名称、完整颜色 JSON 和新顺序。恢复后的 Sync 只上传合并后的最终分类状态，所有 localRevision 清零。）
- [x] C12 同一分类连续多次改名和换色，最终只保留最后一次本地状态，不生成重复分类。（reducer 连续改名、换色后仍只有同一 identity 的一条 pending，最终名称和颜色均为末次值。）
- [x] C13 逐一抽查全部候选配色，背景色与字体色 JSON 在 Room、wire 和远端往返后不丢字段。（18 组互异配色及 JSON 编解码测试通过；Category typed mapper/Room codec 的完整字段往返由 Q03/Q04 覆盖。）
- [x] C14 分类被多个清单与事务共同引用时，改名、换色和排序不会改变引用 ID。（分类资源更新和排序只替换 CategorySyncState；Schedule/AFFAIR 的 categoryId 原子不参与该 reducer 分支，引用 identity 保持不变。）

## 4. 清单日程创建矩阵

- [x] T01 新建时间点 `E2E-SV2-0831-时间点`，设置明确日期与 10:15，不提醒、不关联课表。
- [x] T02 新建时间段 `E2E-SV2-0831-时间段`，设置 14:30–16:00，归入测试分类。
- [x] T03 新建全天 `E2E-SV2-0831-全天`，只保存单个日期，界面不出现伪造的 00:00 时间段。
- [x] T04 新建准时提醒 `E2E-SV2-0831-准时提醒`，确认 `minutesBefore=0` 未被解释为不提醒。
- [x] T05 新建提前 10 分钟提醒 `E2E-SV2-0831-提前提醒`，信息区和列表均展示正确文案。
- [x] T06 新建关联课表的时间点，清单与课表同时出现，点击两端打开同一 ScheduleId。（`E2E-SV2-0831-Point` 调整到 2026-03-02 10:15 后，第 1 周出现零时长课表项；点击课表项打开同一标题、日期、时间和关联状态的详情。）
- [x] T07 新建关联课表的时间段，课表高度、时间轴和重叠布局正确。（`E2E-SV2-0831-Range` 调整到 2026-03-03 14:30–16:00 并关联课表；第 1 周按时间段高度展示，点击后详情保留完整区间与分组。）
- [x] T08 新建关联课表的全天日程，全天背景可点击且不阻断课表创建事务。（第 1 周点击 `E2E-SV2-0831-AllDay` 能打开同一日程详情；全天层存在时，周六空白处普通点击仍由底层创建层生成一小时临时时间段，再点其他空白会按既有逻辑取消。全天手势只消费小于长按阈值的短按 UP，DOWN/MOVE 继续通过 `sharePointerInput` 交给底层长按创建逻辑。）
- [x] T09 创建只含标题的日程时，客户端要求先选择日期/时间，不产生新的 Unscheduled。
- [x] T10 空标题、纯空格标题均无法保存；后端直连接口也拒绝空标题。（客户端统一经过 `ScheduleValidator` 的 `title.isBlank()` 校验；后端 `blank schedule title` 图校验测试通过，空白标题不会落入最终资源图。）
- [x] T11 未填写任何内容时点击外部或下拉，直接放弃且不显示“未保存”二次确认。（设备端打开全空新建弹窗后点击外部，直接回到清单且没有二次确认，也未产生本地/远端资源。）
- [x] T12 已填写内容时点击外部或下拉，立即显示二次确认；“继续编辑”可回弹并保留输入。（设备端输入 `E2E-UNSAVED-DRAFT` 后点击外部出现“未保存”；点击“继续编辑”后标题完整保留，最后选择放弃且未创建资源。）
- [x] T13 创建 00:00 与 23:59 的时间点，日期和分钟值不跨天、不被改写。（typed wire 投影分别恢复为同日 00:00 与 23:59 Deadline，未跨日或改写分钟。）
- [x] T14 创建靠近午夜的合法时间段，滚轮能设置到当日边界且不会生成跨日时长。（滚轮小时/分钟始终使用完整 `0..23`/`0..59` 范围，不以另一端设置动态边界；`intervalAdjustmentHandlesDayBoundary` 验证开始端收敛为 `23:29—23:59`、结束端收敛为 `00:00—00:30`，不会表达跨日时间段。）
- [x] T15 创建恰好 30 分钟的时间段成功；小于 30 分钟时自动调整另一端而不是保存非法时长。（`changingStartMinutePushesEndToThirtyMinutesLater` 与 `changingEndKeepsEndAndPullsStartBackward` 分别验证调整开始/结束时由另一端补足 30 分钟。）
- [x] T16 开始小时越过结束小时、结束小时早于开始小时的交互均保持可继续滚动，并自动收敛到合法区间。（滚轮未设置相互依赖的上下界；`changingStartHourPreservesEndMinuteBeforeApplyingMinimumDuration` 验证开始小时越界后优先保留结束分钟，仅不足 30 分钟时补足，结束端越界由对应测试验证反向调整开始端。）
- [x] T17 标题和备注包含中文、英文、数字、emoji 与换行时，列表摘要、详情和同步往返不乱码。（中英数字 emoji 标题及中英换行备注通过 typed wire → snapshot 精确往返断言。）
- [x] T18 两条日程允许使用相同标题，不能把标题误当成 identity 或覆盖旧日程。（真机先后创建两条 `E2E-DOUBLE-SAVE`，远端分别返回 ID `01a0574f-a43d-7c65-9c35-ea40c738f787` 与 `01a05752-5718-752f-aaaf-c122c8d96ac7`，重合标题没有覆盖第一条。）
- [x] T19 一次创建同时设置新分类、提醒、重复和课表关联，各资源结果分别成功且本地不缺字段。（SaveScheduleWithNewCategory 同 revision 测试同时断言分类、DAILY recurrence、15 分钟提醒和 linkedToCourse 完整落入 typed pending。）
- [x] T20 快速连续点击保存只创建一条日程，按钮状态能阻止重复命令。（真机在同一 ADB shell 内无等待连续点击两次保存；日志仅产生一次本地 Create、一次 `REQUEST CREATE` 和一个 ScheduleId，弹窗收起后第二次触点不再命中保存。）

## 5. 重复规则创建与实例状态

- [x] R01 创建每日重复时间点，设置有限次数，展开数量与结束边界正确。（`E2E-SV2-0831-Daily`：2026-08-31 03:27 起每天一次、共 3 次；UI 显示“直到 9 月 2 日”，wire/回包均为 `DAILY(interval=1, count=3)`。首次发现后端空选择器返回 null，修复 `539793d` 部署后 PendingUpsert 经 Sync 收敛为 version=1、pending=0。）
- [x] R02 创建每周重复时间段，选择多个星期，未选择的星期不生成实例。（`E2E-SV2-0831-Weekly` 选择周三、周五，CREATE 回包 `WEEKLY(interval=1, weekdays=[WE, FR])`；首个实例为 9 月 2 日，完成后下一实例直接变为 9 月 4 日，没有周四或其他未选日期。）
- [x] R03 创建每月重复日程，跨大小月时只在合法日期生成。（`E2E-SV2-0831-Monthly31`：2026-08-31 全天、UI 摘要“每月31日”，CREATE 成功、version=1、pending=0；时间轴选中 9 月 30 日无该实例，选中 10 月 31 日正常出现，没有把 31 号夹到短月月末。）
- [x] R04 创建每年重复全天日程，月份和日期保持正确。（`E2E-SV2-0831-Yearly`：2026-08-31 全天，UI 摘要“每年8月31日”；CREATE 与 canonical 回包均为 `YEARLY(interval=1, anchor=2026-08-31)`，version=1、pending=0。）
- [x] R05 完成一个重复实例，只影响该次；下一次实例仍为未完成。（完成 `E2E-SV2-0831-Daily` 的 8 月 31 日实例后，清单立即切换为 9 月 1 日 03:27 的未完成实例；时间轴仍能定位 8 月 31 日实例并显示“恢复未完成”。）
- [x] R06 取消完成同一实例，状态恢复且远端 occurrence override 正确更新。（从时间轴打开 8 月 31 日实例并恢复未完成，UPDATE 成功、pending=0；随后详情按钮恢复为“标记完成”。）
- [x] R07 重启及 Sync 后，重复实例完成态和异常实例仍保持。（再次完成 8 月 31 日实例并确认 UPDATE 成功、pending=0；强停重启并完成 SYNC 后，时间轴同一 recurrence identity 仍显示“恢复未完成”。）
- [x] R08 每日重复分别验证“永不结束”“重复 1 次”“重复多次”和指定截止日期。（新增 `every_repeat_end_option_maps_without_ambiguity`，逐项断言 `Never`、`Count(1)`、`Count(6)`、`Until` 的编辑器映射；与 RecurrenceEngine 的 count/until 展开测试共同通过。）
- [x] R09 每周重复起始日不在所选星期时，首个实例落在下一合法星期且无额外实例。（系列 anchor 为周一 8 月 31 日、选择器为周三/周五；UI 首个实例为周三 9 月 2 日，完成后为周五 9 月 4 日。）
- [x] R10 每周选择周一/周三/周五后修改为另一组星期，旧实例消失，新实例按新规则生成。（替换为周二/周四后展开结果只包含新星期，旧周一/周三/周五实例不残留。）
- [x] R11 每月 29、30、31 日分别跨过二月和小月，缺失日期跳过而不是移动到月末。（真机 Monthly31 已验证短月跳过；`monthlySelectorsUseMonthDaysAndSkipInvalidDates` 进一步断言 1 月 31 日之后跳过 2 月 31 日，3 月恢复 31 日，同时合法的 2 月日期正常生成。）
- [x] R12 每年 2 月 29 日在闰年生成、平年跳过，后续闰年仍可继续生成。（`dailyWeeklyMonthlyYearlyAndCount` 断言年度 2 月 29 日依次生成于 2024、2028、2032，平年不夹到 2 月末且系列没有中断。）
- [x] R13 全天、时间点和时间段三种 timing 分别创建周重复，实例 identity 与显示日期一致。（新增 `weeklyRecurrenceKeepsTimingKindAndIdentityForAllSupportedTimings`，三种 timing 均生成 1 月 31 日与 2 月 7 日两个实例，类型不变且 recurrenceId 唯一；聚焦 desktopTest 通过。）
- [x] R14 已完成一个实例后修改父系列标题或分类，该实例状态保留并继承未覆盖字段的新值。
- [x] R15 已移动单次实例后再修改整个系列时间，移动实例不重复、不回到错误日期。

## 6. 清单日程修改矩阵

- [x] U01 修改普通日程标题、备注、分类，确认一次保存只产生对应字段更新。（同一次编辑把 Range 标题改为 `E2E-SV2-083_Updated1-Range`、备注改为 `E2E-note-Updated`、分类改为未分组；只产生一次 UPDATE，version 3→4、pending=0，列表同时显示新标题和备注。）
- [x] U02 时间点改为时间段，日期保持，结束时间至少晚于开始时间 30 分钟。（同一 Range 从 `DEADLINE 2026-03-03T16:00` 切回 `TIMED 2026-03-03T03:32..16:00`，日期保持且区间合法；UPDATE version 5→6、pending=0。）
- [x] U03 时间段改为时间点，日期保持且只保存 dueAt。（Range 从 `TIMED 2026-03-03T14:30..16:00` 切为 `DEADLINE 2026-03-03T16:00`，请求与 canonical 回包均不再含时间段语义；version 4→5、pending=0。）
- [x] U04 时间点或时间段改为全天，只保存 ALL_DAY date；再切回时间模式后时间可正常编辑。（Range 从 TIMED 转为 `ALL_DAY 2026-03-03`，请求和回包不再携带钟点；随后切回 `DEADLINE 2026-03-03T04:36` 成功，日期保持、version 6→7→8、pending=0。）
- [x] U05 全天修改日期，系统日历、清单和课表投射同步移动。（`E2E-DOUBLE-SAVE` 从 9 月 7 日改到 9 月 8 日后，UPDATE version 5→6、pending=0，第一周全天条从周一列移除并出现在周二列；设备已有日历权限，Provider 事件 `_id=569` 最终为 `2026-09-08T00:00Z..2026-09-09T00:00Z / allDay=1`。过程中发现课表详情保存协程随弹窗离场被取消，并完成 BUG-005 修复。）
- [x] U06 修改提醒：不提醒 → 准时 → 提前 10 分钟 → 不提醒，逐次核对 wire 与系统日历。（同一每周事务先由无提醒改为准时，服务端 version 1→2，Provider 创建 0 分钟 reminder；再改为提前 10 分钟，version 2→3、pending=0，event 573 对应 reminder row 344 的 `minutes=10/method=1`；最后改回不提醒，version 3→4、pending=0，Provider 按 event_id 查询为 `No result found`。）
- [x] U07 修改课表关联开关，课表投射新增/移除，清单本体不丢失。（`E2E-SV2-0831-Point` 关闭关联后 UPDATE 成功、版本 5→6、pending 清零；重启后第一周仅移除 Point，清单本体、Range 与 AllDay 均保留。重新开启并重启后 Point 在 3 月 2 日 10:15 恢复投射。）
- [x] U08 修改分类为“未分组”，categoryId 变为 null 且正常发起更新请求。（Range 从 `E2E-SV2-0831-CatA-Renamed` 切换到未分组，UPDATE 成功且列表不再显示分类标签；version=4、pending=0。）
- [x] U09 重复日程选择“全部”修改标题、时间、日期和规则，整个系列按新规则重建。（范围路由测试断言 ALL 只生成父 Schedule Update；从中间 occurrence 编辑只把相对日期/时间偏移应用到父锚点，规则整体替换且不会把 occurrence 专属字段误提升到系列。）
- [x] U10 重复日程选择“仅此次”修改标题、备注、分类、时间，其他实例保持原值。（sparse occurrence patch 测试分别覆盖标题、备注、分类和 timing；只 UpsertOccurrenceException，未触碰字段及其他实例继续继承父系列。）
- [x] U11 重复日程选择“此次及以后”修改时间与规则，旧系列截断，新系列从边界开始。（EditRouting 生成 SplitSeries；reducer 同 revision 截断旧系列、新建边界系列，并迁移边界后的 remote/local override identity。）
- [x] U12 对已经存在实例例外的系列再次修改，旧完成态/取消态与新 timing identity 不丢失。（父系列时间移动后经 typed snapshot 重新水合 occurrence identity；完成态继承父字段、未来例外迁移和日历 native CANCEL 均有精确断言。）
- [x] U13 清空备注、分类和提醒时分别生成明确的空值语义，不保留旧值也不误清其他字段。（reducer 分别写入空字符串、null categoryId、null reminder，同时保留标题、timing 与 todoState 原子。）
- [x] U14 打开既有日程但不修改直接保存或返回，不发 Update、不增加 revision、不产生未保存提示。（在 version=4、local revision=31 后直接进入编辑并保存，日志没有新的 reducer、revision、UPDATE 或 pending 记录。）
- [x] U15 修改日期跨周、跨月和跨年，标题、提醒、重复与分类不被重置。（编辑状态分别跨周、跨月和跨年显式选日，只迁移 Timed.start.date；标题、备注、分类、提醒和重复规则逐项保持。）
- [x] U16 从全天切换为时间点/时间段时使用原日期；再切回全天时只清除钟点语义。（同一资源在 TIMED→ALL_DAY→DEADLINE 往返中始终保持 2026-03-03；ALL_DAY canonical 仅含日期，DEADLINE 恢复可编辑钟点。）
- [x] U17 调整开始时间导致不足 30 分钟时只推后结束；调整结束时间时只提前开始。（`adjustScheduleTimeInterval` 是两组滚轮停止后的统一提交入口；开始端测试保持开始值并推后结束，结束端测试保持结束值并提前开始。）
- [x] U18 在 00:30 和 23:29 附近调整时间段，两端仍可滚动且最终时长合法。（滚轮范围不随另一端收窄；边界测试断言 `00:00—00:30` 与 `23:29—23:59`，均保持最短 30 分钟且不跨日。）
- [x] U19 编辑已完成普通清单的标题、备注和分类，完成态不被恢复为未完成。（完成态资源编辑标题/备注后仍保持 COMPLETED 及其原 modifiedAt。）
- [x] U20 打开迁移得到的 Unscheduled，显示“未设置日期/时间”；补充日期可变全天，补充钟点可变时间点或时间段。（编辑状态保持 Unscheduled 直到显式操作；选日生成 AllDay，显式时间模式分别生成 Deadline/Timed，默认候选今天和滚轮值不会偷偷改领域 timing。）

## 7. 清单展示、完成、置顶与批量操作

- [x] L01 列表视图排序符合：超期、置顶、临期、普通、无截止时间；已完成仅展示 7 天内记录。（固定时钟测试断言完整业务优先级和七天完成过滤。）
- [x] L02 时间轴视图展示时间点、时间段、全天和重复实例，分类颜色正确。（真机 8 月 31 日同时显示准时提醒时间点、每日重复时间点和 4 个全天项；切到 9 月 2 日凌晨后显示 `E2E-SV2-0831-Weekly` 的 `02:46-03:46` 时间段与每日实例。时间轴按稳定 CategoryId 同时解析背景/文字色；测试过程中发现全天 sticky 标题未裁剪并完成 BUG-004 修复。）
- [x] L03 列表/时间轴切换状态写入本地 Settings，重启后保持。（切到时间轴后强停重开仍显示“切换到清单列表”；切回列表后再次强停重开仍显示“切换到时间轴”。）
- [x] L04 单条置顶、取消置顶后顺序与图标立即更新，重启后保持。（`Monthly31` 侧滑置顶后显示“已置顶”，侧滑操作变为“取消置顶”；重启后标识仍存在，取消后恢复。）
- [x] L05 删除置顶日程后 Settings 中的置顶 ID 自动移除。（真机将 `E2E-DOUBLE-SAVE` `01a0574f-a43d-7c65-9c35-ea40c738f787` 置顶后，账号 Settings 出现对应 `schedule_todo_pinned_ids`；批量删除返回 `DELETED`、`pendingCount=0` 后该字段被直接移除。）
- [x] L06 普通清单完成/取消完成，卡片动画、完成样式和远端 todoState 正确。（`Point` 完成后移入“已完成”并显示“恢复未完成”，UPDATE version 7→8；取消完成再次 UPDATE 且 pending=0。两个分区均以稳定 occurrence key 配合 `animateItem` 的淡入、位移动画和淡出配置，状态切换没有重复项或瞬间回滚。）
- [x] L07 长按进入批量管理，全选、删除、置顶和“全为置顶时取消置顶”均正确。（真机长按进入管理态；全选后批量置顶并退出，再次长按全选时按钮切换为“取消置顶”，执行后恢复；随后仅选择 `E2E-OFFLINE-POINT-FINAL2` 批量删除，DELETE 返回 DELETED 且 pending=0。当前产品设计的批量底栏没有“完成”，因此用例按实际能力修正，未额外扩展功能。）
- [x] L08 Feed 卡片左右滑置顶/删除、完成、提醒与关联课表图标状态与清单页一致。（真机依次验证 `E2E-SV2-0831-OnTimeReminder` 完成/恢复、左右滑置顶/取消置顶；Feed 时间与“准时提醒”、课表关联图标状态和清单页一致。对月重复 `E2E-SV2-0831-Monthly31` 左滑删除时生成当前实例取消例外，CREATE 成功且 `pendingCount=0`，未误删整个系列。）
- [x] L09 从 Feed 点击日程后进入清单页并高亮正确 ScheduleId，不暴露侧滑按钮。（真机点击 Feed 的 `E2E-SV2-0831-Point` 后进入清单页；代码核对目标参数会先清除分类筛选、滚动到对应分区并仅设置 730ms 覆盖层高亮，侧滑位移状态不参与高亮绘制。）
- [x] L10 临期边界前后各构造一条日程，24 小时内、超期和普通分区判定一致且随时间刷新。（固定 now 下 24 小时整为临期、24 小时零 1 分钟为普通，已过期项优先；投影由调用方的时间 tick 重新计算。）
- [x] L11 已完成恰好 7 天、少于 7 天和超过 7 天的记录只展示产品要求的范围。（2 天内和恰好 7 天记录保留，7 天零 1 分钟及 8 天记录排除。）
- [x] L12 顶部分组可横向滚动，选择自定义分组后只展示对应清单，切回全部恢复完整列表。（横向滚到 `E2E-SV2-0831-CatA-Renamed` 后列表为空，切回“全部”恢复全部 E2E 条目。）
- [x] L13 未完成为空、已完成为空、两者同时为空时分别显示正确空状态且不遮挡悬浮按钮。（选择无引用的自定义分组后同时显示两块独立空状态，右下角“新建事项”仍可访问。）
- [x] L14 置顶第一条和中间条目时滚动定位不把“未完成”标题推离可视区域。（真机分别置顶列表中间的 `E2E-SV2-0831-Weekly` 和当前首条 Range；自动定位后提示条、“未完成”标题与首张卡片仍同时可见。实现固定滚到索引 0 的分区标题，不以被移动卡片本身作为滚动锚点。）
- [x] L15 完成、取消完成、置顶和侧滑恢复时卡片移动使用动画，不出现重复项或瞬间闪回。（完成/恢复与两次置顶操作均在真机得到唯一条目和稳定最终顺序；pending/completed 两区统一使用 occurrence 稳定 key，并配置 320ms placement、180ms fade-in、160ms fade-out，状态切换不会重建为重复卡片。）

## 8. 删除与重复范围

- [x] D01 删除普通未完成日程，本地立即消失，远端 deleteResult 成功，重启不恢复。（删除 `E2E-SV2-0831-Ahead10` 后立即从列表消失；本地先为 PendingDelete，DELETE 返回 `DELETED` 与同 ID tombstone，pending=0；强停重启并 SYNC 后仍未恢复。）
- [x] D02 删除已完成日程，行为与未完成一致。（已完成普通日程仍路由为同一 ScheduleCommand.Delete，不引入完成态专属分支。）
- [x] D03 删除重复日程“仅此次”，只生成取消实例，后续实例仍存在。（路由只 Upsert 同 identity 的 CANCELLED occurrence，保留既有 revision/patch；父系列不进入删除命令。）
- [x] D04 删除重复日程“此次及以后”，边界前保留，边界及以后消失。（路由生成 DeleteThisAndFollowing，周重复边界前截断到上一个真实 occurrence；reducer 同时丢弃本地未来例外。）
- [x] D05 删除重复日程“全部”，父系列和关联 occurrence override 均删除。（ALL 路由为父 Schedule Delete；真实 Room 仓库测试断言同一次 DELETE 携带父日程和关联 override，并在 canonical 回包后同时移除。）
- [x] D06 对同一资源重复删除，服务端仍视为删除成功，客户端不产生额外失败记录。（DAO 的缺失/重复删除内部状态统一经 `normalizedDeleteResult` 对外映射为 `DELETED`，wire 不暴露 `ALREADY_DELETED`；客户端收到 `DELETED` 即清 pending。后端聚焦 `TestScheduleV2` 及 DAO/wire 测试通过。）
- [x] D07 断网删除普通与重复日程，本地先隐藏，恢复网络后远端删除且不会被 Sync 复活。（应用级 FakeGateway 令 DELETE 发生 TransportFailure，真实 Room 立即隐藏资源并保留 pending；恢复后的完整 Sync 返回 tombstone，Room/published snapshot 均保持为空。重复日程的父项+override 同请求删除由 D05 覆盖。）
- [x] D08 网络结果未知的新建日程随后立即删除，本地隐藏并收敛为 PendingDelete；恢复后直接发幂等 DELETE，防止 CREATE 实际已到服务端时留下幽灵资源。（`E2E-OFFLINE-DROP` 从 version=0 PendingUpsert 转为无快照 PendingDelete，未先补发 CREATE；恢复后的 SYNC 仅携带 delete，服务端返回 DELETED，pending=0。）
- [x] D09 已有 PendingUpsert 的已确认日程继续删除，最终 PendingDelete/本地隐藏状态正确。（version=1 的 `E2E-OFFLINE-EDIT1-CREATE-FINAL` 先离线切换课表关联成为 PendingUpsert，再删除成为同 ID PendingDelete；恢复后只上传 DELETE 并返回 DELETED。）
- [x] D10 从清单卡片、详情弹窗、Feed 和批量管理删除，均走同一仓库命令和失败记录更新逻辑。（IDE 引用核对：清单卡片和详情弹窗统一调用 `ScheduleMainViewModel.deleteScheduleScoped`，Feed 调用同一 `applyScheduleDelete` 路由，批量管理把选中 ID 转为 `ScheduleCommand.Delete` 后交给同一仓库；真机批量删除同时验证了 local-first、DELETE 响应合并和 pending 清理。）

## 9. 离线、失败记录与恢复同步

- [x] S01 断网创建时间点，本地立即显示并记录 PendingUpsert；纯网络失败不生成需要用户处理的确定性失败记录。（应用级故障下注入创建 `E2E-OFFLINE-POINT`，本地立即得到 DEADLINE `2026-08-31 10:59`、version=0、`PendingUpsert`；网关未执行 HTTP，仓库现有测试同时断言 transport failure 不生成业务失败记录。）
- [x] S02 断网修改同一日程多次，只以上一次本地状态作为待同步结果。（应用级故障注入下，新建资源连续两次改名始终保持同一 ID、`PendingUpsert` 与 version=0；每次 capture 均为单资源 CREATE，恢复后仅最终标题 `E2E-OFFLINE-EDIT1-CREATE-FINAL` 上传。）
- [x] S03 断网删除已有远端日程，本地立即隐藏并记录 PendingDelete。（已确认的 `E2E-SV2-0831-AllDay` version=3 在应用级故障下立即转为 `PendingDelete` 并从列表隐藏，未执行真实 HTTP。）
- [x] S04 恢复网络时，仅在存在 pending 的情况下自动 Sync；成功后 pending 与相应失败记录清除。（设备端关闭应用级故障并重启后，一次 SYNC 同时返回最终 CREATE 与 DELETE，pending 由 2 清零；`recoveryWithPendingRequestsSyncOnce` 进一步覆盖系统网络由不可用恢复时只触发一次 Sync。）
- [x] S05 无 pending 时切换网络，不发起多余自动 Sync。（`initialAvailableDoesNotRequestSync` 与 `recoveryWithoutPendingDoesNotRequestSync` 均通过；无需关闭设备网络破坏小米互联连接。）
- [x] S06 通过直连接口构造单条业务非法 upsert，响应整体 HTTP/业务状态正常，目标结果携带安全且明确的失败原因。（后端逐资源业务拒绝测试和客户端 `dailyRejectedKeepsPendingAndCreatesFailureRecord` 通过：结构请求仍正常返回，拒绝只落在目标 result，pending 与可操作失败原因保留。）
- [x] S07 同一次 Sync 中混入合法和非法资源，合法项成功、非法项进入失败记录，不能整批失败。（`partialSuccessClearsOnlyAcceptedPending`、分类/日程 full-sync rejected 测试通过；只清理成功 identity 的 pending，拒绝项继续保留。）
- [x] S08 在失败记录页打开目标日程，修正后再次提交成功，原失败记录自动删除。（失败记录路由已验收；`successfulRetryClearsFailureRecord` 验证修正后成功回包按 ScheduleId 自动删除原记录。）
- [x] S09 失败日程再次修改、删除时，失败记录中的源数据和状态同步更新。（`failedRecordFollowsNewerLocalUpdateWhenRetryTransportFails` 与 `deletingFailedLocalCreateRemovesFailureRecordWhenDeleteTransportFails` 分别覆盖修改刷新源快照、删除移除记录。）
- [x] S10 模拟 5xx/超时，客户端保留 pending 而不是当作确定性业务拒绝。（设备应用级 TransportFailure 下 CREATE/DELETE 均保留 pending；`dailyTransportFailureKeepsLocallyPersistedPending` 与 `requestTimeoutMapsToTimeout` 分别覆盖 pending 持久化及超时错误映射，HTTP 非 400 的 ResponseException 同样归入 TransportFailure。）
- [x] S11 Sync 的 confirmedResults、discoveredResults、upsertResults、deleteResults 与请求逐项对应。（planner/applier 的 identity、数量不匹配均 fail-closed；设备日志也逐段核对了空请求和带 CREATE/UPDATE/DELETE 请求的四类结果。）
- [x] S12 空本地首次 Sync 能完整接收分类、日程、实例例外和 tombstone，不依赖旧 cursor/outbox/receipt。（空 Room 投影、四态 Schedule、override 与 tombstone 自动化测试通过；当前实现只使用 confirmed/discovered/upsert/delete 四段结构。）
- [x] S13 CREATE、UPDATE、DELETE 日常请求分别核对标题、日期、时间、周期和版本日志，成功后 pending 清零。（本轮分别核对 Daily/Weekly/Monthly/Yearly CREATE、Range 多次 UPDATE、Ahead10 DELETE；日志含资源 ID、版本、标题、四态 timing 与 recurrence 摘要，不含敏感 payload，成功后均 pending=0。）
- [x] S14 同一日程离线连续修改标题、备注和时间后恢复网络，上传最终完整资源且三项均保留。（同一 version=0 时间点离线改为标题 `E2E-OFFLINE-POINT-FINAL2`、备注 `offline-final-note-updated`、时间 `11:59`；恢复后 SYNC 仅上传最终 DEADLINE 资源并返回 CREATED/version=1，重启后的列表仍展示三项最终值。）
- [x] S15 日程与首次使用的新分类同次保存时，分类和日程均成功；任一业务失败时失败记录能定位具体资源。（真实 Room 仓库测试断言同一普通请求携带 Category+Schedule 并分别确认；分类成功而日程 REJECTED 时分类收敛、仅日程保留 pending，失败记录含精确 ScheduleId/reason/info。）
- [x] S16 失败记录在正常退出并重新进入应用后仍存在，修复或删除目标日程后自动清除。（`failureRecordPersistsInChunkedAccountSettings`、成功重试与删除失败创建三项测试覆盖持久化和两个清理出口。）
- [x] S17 服务端 tombstone 的删除优先级高于旧 upsert，客户端不能把已删除资源重新创建。（`tombstoneDeletesRemoteAndPending` 通过：同 identity 的 remote 与 pending 同时移除，旧 upsert 无法复活。）

## 10. 课表事务与清单互关联

- [x] A01 在课表空白时间段创建事务 `E2E-SV2-0831-AFFAIR`，保存为 AFFAIR + TIMED + linkedToCourse=true。（ADB 无法稳定模拟同一触点长按拖动，按已确认的普通点击兜底路径：先点击空白格显示“点击添加事务”，再次点击打开编辑器；保存 2026-09-12 10:00–11:00 后 CREATE 返回 version=1、pending=0，课表周六对应时间段出现同一标题，详情显示“未关联清单”。）
- [x] A02 事务编辑器不展示“时间点”和“全天”，不能生成非法 timing。（`EditScheduleTimeArea` 仅在 `kind == TODO` 时组合三态切换，AFFAIR 固定走 INTERVAL；`affairCreationUsesInitialTimingWithoutTodoState` 验证事务创建保持 TIMED、默认关联课表且不伪造清单完成态。）
- [x] A03 点击事务打开与旧事务一致的详情弹窗，时间轴、透明背景、重叠左右切换正确。（真机点击 `E2E-SV2-0831-AFFAIREDIT` 后，详情正常展示标题、日期、时间、重复、提醒、清单关联、备注和编辑/删除入口；A13 三重叠数据验证通用弹窗可依次左右切换三项。宿主统一使用透明 scrim、`BeginFinalTimeShowModifier` 时间轴和同一重叠 Pager，事务与清单只替换详情业务内容。）
- [x] A04 长按拖动事务时可预览移动，松手返回原位置，不提交新时间。（在同一远端触点先 DOWN、等待超过长按阈值、再 MOVE 并保持：`E2E-SV2-0831-AFFAIR-WEEKLY` 节点从 `[113,427][245,579]` 移到 `[113,627][245,779]`；UP 后恢复原边界，期间没有本地持久化、revision 或 UPDATE 日志。）
- [ ] A05 修改事务标题、备注、日期、时间、重复与提醒，保存后课表和远端一致。（已把 `AFFAIREDIT` 一次性修改为标题 `e2e-affair-fields`、备注 `e2e-affair-note-fields`、9 月 16 日 12:00–13:30、每周三和准时提醒；客户端本地完整快照与 pending 均正确。后端 BUG-008 修复已推送，但 9 月 1 日 07:57 与正常包覆盖安装后的 08:09 两次 Sync 仍返回旧服务的 `REJECTED/INVALID_REQUEST`，pending=1；等待 dev/test 部署生效后重试收敛。）
- [x] A06 将事务关联到清单，出现完成圆圈和分类入口；课表仍保留事务条纹。（事务编辑态一次点击切换为“已关联清单”，同时出现“未分组”入口；保存后 version 4→5、服务端 `APPLIED`、pending=0，清单列表出现同一 ScheduleId 的完成圆圈，课表周二 11:00–11:30 投射仍存在。条纹由 `ScheduleAffairBackgroundItemModifier` 持续按 AFFAIR 来源绘制。）
- [x] A07 已关联清单的事务切换分类，清单与课表使用同一分类配色，条纹仍可辨认。（选择 `E2E-SV2-0831-CatA-Renamed` 后同一次 UPDATE 成功；课表 Item 仅在 `isLinkedTodoAffair` 时读取 categoryColor，并以该背景色作为斜纹色、透明间隙透出课表底色，未把事务退化为普通 TODO 块。）
- [x] A08 完成由事务关联出的清单后，事务仍显示在课表，清单完成态正确。（`completionOnlyHidesTodoOrigin` 覆盖同为 COMPLETED occurrence 时 TODO 隐藏、AFFAIR 继续可见；AFFAIR 的完成态由 todoState 表达，不改变事务来源。）
- [x] A09 取消事务的清单关联，todoState 清空但事务继续显示在课表。（事务关系开关只在 `ScheduleKind.AFFAIR` 分支切换 `todoState`；`linkedToCourse` 保持事务默认 true，课表可见性测试覆盖取消 occurrence 之外仍保留。）
- [x] A10 从清单侧关闭课表关联，仅原生 TODO 从课表移除；AFFAIR 不允许 linkedToCourse=false。（TODO 开关只切 `linkedToCourse`；AFFAIR 开关只切 `todoState`，Feed 与主 ViewModel 也拒绝对 AFFAIR 执行课表投射开关；可见性测试断言未关联 TODO 不进入课表。）
- [x] A11 删除事务后课表、清单（若关联）和远端同时删除。（对未关联清单的每日事务 `01a05948-673f-7154-beb3-d5b79258bcd6` 选择“删除整个系列”，本地先进入 PendingDelete；DELETE 响应为 `DELETED` 且 tombstoneId 与资源 ID 一致，pending 回到 0。第一周课表立即不再包含该事务；该事务原本未关联清单，因此清单侧无额外残留。）
- [x] A12 创建每日/每周重复事务，课表只在规则命中的日期显示，跨周翻页不重复、不漏项。（修复完整课表遗漏创建 Decoration 后，通过空白轻击分别创建 `E2E-SV2-0831-AFFAIR-WEEKLY` 与 `E2E-SV2-0831-AFFAIR-DAILY`；两次 CREATE 均返回 version=1、pending=0。每周事务在第 1/2/3 周均只出现于周一 08:00–09:00；每日事务从 9 月 13 日起逐日投射，第二周 6 列独立可见，周四 16:00–17:00 与课程完全重叠而进入既有覆盖关系。）
- [x] A13 构造下午两重叠、三重叠事务与清单日程，宽度、层级、点击后置顶和左右切换正确。（真机创建 `E2E-A13-1/2/3` 三条 9 月 12 日下午的关联课表日程，形成两重叠与 14:31–15:57 三重叠区间；点击顶层 `A13-3` 后可向左依次切换到 `A13-2`、`A13-1`。结合重叠算法测试确认三层按时间片保留直接上层关系；点击入口以当前 item 的 overlap 为根构造有序集合，因此被点击项固定为弹窗第一页，展开时提升其 `zIndex`、恢复完整区间，并复用被点击片段 key 作为动画起点。）
- [x] A14 时间点清单与时间段重叠时，时间点优先展示并为下方文本留出空间。（第二周周二 `Point 11:15` 位于最高层并把 `AFFAIREDIT 11:00–11:30` 切为前后两段；事务文字按切割点增加 8dp 间距。尺寸日志确认时间点实际占 `y=861..906`，事务下方真实可见尾部为 `y=906..926`；点击 `y=915` 正常打开原事务详情。）
- [x] A15 全天清单位于课程/事务底层，仅覆盖当天列且点击不阻断空白处长按创建事务。（真机确认全天条只铺满 9 月 8 日周二列，课程、时间段与事务仍绘制在其上，点击全天标题打开详情；同页周六空白处普通点击可生成一小时临时时间段。Decoration 注册顺序固定把 AllDay 放在最后最底层，全天层只消费短按 UP 并共享其余指针事件。）
- [x] A16 同一日程不会因整学期 dayIndex 与当前周索引换算错误而出现在两个 page。（非重复事务 `E2E-SV2-0831-AFFAIREDIT` 的日期为 9 月 15 日：第一周 9 月 7–13 日语义树中出现 0 次，滑到第二周 9 月 14–20 日后恰好出现 1 次；同一 ScheduleId 没有被同时放入相邻 page。）
- [x] A17 编辑事务时展开日历、重复和提醒区域，底部弹窗高度自适应且收起后不透出背景。（AFFAIREDIT 基线标题栏中心 y=1636；展开完整月历后上移到 y=1367、内容延伸至 y=2198，返回后恢复 y=1636，而非固定扩展到 440dp。重复选项按实际内容保持紧凑高度，提醒滚轮可延伸到 y=2121；收起均恢复基线。代码核对动画内容外层由同一个带圆角的 `colors.topBg` Column 包裹，尺寸动画期间不会暴露课表背景；测试产生的默认提前 10 分钟草稿已通过“放弃”退出，未提交。）
- [x] A18 事务关联清单后从清单页编辑，再回到课表仍使用 AFFAIR 条纹与清单配色。（从清单页编辑 `E2E-SV2-0831-AFFAIREDIT` 备注后，UPDATE version 5→6、pending=0；返回第二周课表仍由同一 AFFAIR ScheduleId 投射在周二 11:00–11:30，详情读取到新备注。`ScheduleAffairBackgroundItemModifier` 的来源判断与分类配色路径未因清单侧保存改变。）

## 11. 系统日历与提醒权限

优先通过 ADB 授予/撤销权限并直接进入应用设置；若小米系统仍要求人工确认，则相关授权交互标记为跳过，
不视为应用缺陷。已授权和未授权状态下的业务行为仍需继续测试。

- [x] K01 首次设置提醒时说明依赖系统日历，并在确认后请求日历权限。（未来每周事务首次点击提醒先显示“日程提醒借助手机系统日历实现”及受管日历“掌邮日程”的说明；点击“去授权”后小米系统弹出日历修改权限，选择“始终允许”后返回提醒设置区。）
- [x] K02 拒绝权限后回退为不提醒；再次操作能识别永久拒绝并引导到系统设置。（撤销权限后给原本无提醒的每日事务开启提醒，在应用说明框点“暂不使用”，编辑器仍停留且字段回到“不提醒”；系统选择“拒绝且不再询问”后再次操作，应用内显示“需要日历权限”及“去设置”，点击后直达小米应用详情页。）
- [x] K03 远端已有提醒在当前设备未授权时显示“提前 X 分钟（未授权）”或“准时（未授权）”，点击可重新授权。（已有准时提醒的每周事务在撤权后详情显示“准时(未授权)”；非编辑态点击即可打开授权说明，重新授予 READ/WRITE_CALENDAR 后返回同一详情，文本恢复为“准时”。）
- [x] K04 时间点以 0 分钟事件写入系统日历，不被转换成 1 分钟时间段。（小米真机 Provider 回归的 Deadline 形状断言 `DTSTART == DTEND`，与 common/Android host 的 0 分钟 canonical 测试共同通过。）
- [x] K05 时间段、全天、重复、提醒和 occurrence exception 均正确写入应用管理的系统日历。（真机 Provider 15 条测试覆盖 Timed、AllDay、Deadline、RRULE、Reminder 的 CRUD/回读；occurrence exception 的 planner/gateway 由 Android host 测试覆盖。）
- [x] K06 Unscheduled 不写系统日历；点击提醒或关联课表只 Toast 提示先设置时间。（真机打开旧清单迁移的 `222 / Unscheduled`，编辑态点击两入口分别记录 Toast“请先设置时间后再开启提醒”“请先设置时间后再关联课表”，未产生 UPDATE；系统 Calendar Provider 按标题精确查询返回 `No result found`。）
- [x] K07 删除应用管理的日历事件后重新进入清单页，对账机制可恢复缺失事件。（给 `E2E-SV2-0831-AFFAIR-WEEKLY` 设置准时提醒后生成受管 row 571；精确删除该 row，再不清数据地冷启动并进入清单页，自动重建为 row 573，`dtstart` 与 `RRULE=FREQ=WEEKLY;BYDAY=MO` 保持一致。已过期的 8 月 31 日事件不会被误用于该恢复断言。）
- [x] K08 用户自己创建的同名或非所有权事件被忽略，不被覆盖或删除。（真机 fixed-calendar/update 测试验证缺失 row、replacement row、ownership 漂移均拒绝写入；测试只操作随机 LOCAL 测试日历。）
- [x] K09 应用管理的日历名称和内部识别标记均为当前约定，不再创建旧“邮子清单”日历。（真机 registry 测试只创建并发现 `掌邮日程` + strict `CAL_SYNC1` identity，清理时按精确受管 identity 删除。）
- [x] K10 已授权并写入事件后从系统设置撤销权限，应用不崩溃，提醒显示未授权并可重新申请。（已写入系统日历的每周事务在 ADB 撤权导致进程重启后仍可打开，详情降级为“准时(未授权)”；永久拒绝、跳设置、重新授权后原编辑上下文与提醒状态均保留。）
- [x] K11 完成普通清单后不再保留无意义提醒；取消完成后按当前日程状态恢复日历投影。（Calendar projection 对 COMPLETED 普通清单返回空事件；恢复为 PENDING 后仍由同一资源当前 timing/reminder 重新生成投影。）
- [x] K12 完成或取消重复实例时，只处理对应 occurrence 的系统日历状态，不影响系列其他实例。（native occurrence exception 测试对 COMPLETED/CANCELLED 各生成一个精确 recurrence identity 的 CANCEL operation，父 SERIES_MASTER 和其他实例不被删除。）
- [x] K13 全天事件在 Provider 中使用单日排他结束边界，系统日历不会多显示一天或少显示一天。（真机 `gatewayRoundTripsAllDayDeadlineAndRecurringShapes` 核对单日 AllDay 的 UTC 半开区间并成功回读。）
- [x] K14 修改重复规则、仅此次和此次及以后后，Provider 主事件与 exception 对账结果和应用内一致。（host 的 occurrence exception snapshot/gateway 与 planner 测试全部通过；真机重复主事件 RRULE 更新、畸形 RDATE fail-closed/recovery 通过。）

## 12. 旧数据迁移

迁移使用测试源数据或已确认可直接重建的旧记录；临时提升迁移版本后必须恢复源码。

- [x] M01 临时提高 migrationVersion，覆盖安装后只触发一次迁移；完成后写入新的账号级版本。（临时从 v2 提到 v3 覆盖安装，账号 SharedPreferences 明确写入 3；资源仍为原 9 条、`222` 仅一条、pending=0。再次强停启动只发空 pending 的正常 Sync，没有 CREATE/UPDATE；测试后源码和账号标记均恢复为 v2，并再次覆盖正式构建。）
- [x] M02 旧清单无时间 → Unscheduled，且 reminder/recurrence/linkedToCourse 均为空或 false。（`todoWithoutTime_mapsUnscheduledWithoutDerivedFields` 通过。）
- [x] M03 旧清单有截止时间 → Deadline；准时与提前提醒偏移正确。（截止/通知组合矩阵覆盖无提醒、0 分钟、提前 30 分钟及非法晚提醒。）
- [x] M04 旧清单仅通知时间 → Deadline + 准时提醒。（仅通知时间被提升为 Deadline，`offsetMinutes=0`。）
- [x] M05 旧清单日/周/月/年重复按迁移文档映射；不可无损表达的年重复安全降级。（四种频率、缺少 notify、无效周选择及年规则笛卡尔积扩张均有 mapper 测试。）
- [x] M06 旧清单完成态、分类、置顶顺序和秒/毫秒修改时间正确迁移。（完成语义、三类固定分类、置顶标记与顺序合并、秒/毫秒时间戳规范化均有自动化覆盖。）
- [x] M07 旧 Transaction 全学期 → WEEKLY 事务；指定周和多个时间位置拆为稳定的独立日程。（全学期、指定周、多个 AtWhatTime 自动化测试通过。）
- [x] M08 旧事务标题、详情、提醒、节次时间与当前学期日期映射正确。（普通节次及午间/晚间旧特殊行均验证为明确 Timed 事务。）
- [x] M09 非法旧标题、周数、星期、节次或时间只跳过对应脏项，不阻塞其他迁移。（脏事务/非法节次与空标题清单均验证为逐项跳过，不影响同批合法项。）
- [x] M10 首次迁移时后端不可用，本地仍保存 pending，迁移完成条件与后续恢复同步符合文档。（`remoteFailureAfterLocalCommit_isAccepted` 验证先落本地即视为可恢复；本轮应用级网络故障也验证 pending 可在恢复后收敛。）
- [x] M11 重启、重登、清除迁移版本后重复执行，确定性 ScheduleId 保证不重复创建。（确定性 UUID、已存在 ID 跳过与同批重复 ID 只提交一次测试通过。）
- [x] M12 恢复正式 migrationVersion 源码，确认工作区不残留测试注入。（应用级网络故障 wrapper 与设备标志均已移除；迁移常量、真机账号标记均恢复 v2，随后已覆盖安装正式构建；当前仅保留本验收文档改动。）
- [x] M13 旧清单 `is_done=1`：非重复迁移为 COMPLETED，重复系列迁移为 PENDING。（`todoCompletion_mapsByRecurrenceSemantics` 通过。）
- [x] M14 旧清单学习/生活/其他/未知/空分类分别映射到正确固定分类，已有同名分类优先复用。（study/学习、life/生活 映射对应固定分类，other/未知/空映射“其他”；同名与固定 identity 复用测试通过。）
- [x] M15 多条旧置顶清单迁移后顺序稳定，并与 Settings 中已有置顶项合并而不是覆盖。（已有顺序优先、迁移顺序追加、跨两侧重复 ID 首次去重的纯合并测试通过。）
- [x] M16 旧提醒晚于截止时间时丢弃非法提醒；时间文本非法且无备用时间时安全降为 Unscheduled。（迁移矩阵同时覆盖两种降级路径。）
- [x] M17 旧日重复、多个星期的周重复、29/30/31 号月重复分别映射为当前规则。（DAILY、周日/周一/周三集合及月末 29/30/31 selector 均精确映射，当前引擎负责短月跳过。）
- [x] M18 旧年重复可形成无损月份×日期笛卡尔积时保留 YEARLY，不可无损时降为下一次 Deadline。（单一合法年日期保留 YEARLY；`3.8 + 4.9` 这类会被笛卡尔积扩张的组合降为一次性 Deadline。）
- [x] M19 旧 Transaction `week=[0]` 迁移为整学期 WEEKLY，指定连续周和稀疏周分别生成明确实例。（全学期与 distinct valid weeks 测试覆盖两条映射分支。）
- [x] M20 一个旧事务包含多个 AtWhatTime 或同日多个节次时，每个时间位置生成独立稳定 ScheduleId。（多时间位置拆分测试并与确定性 UUID 测试共同覆盖。）
- [x] M21 旧接口任一读取失败时不写完成版本；下次覆盖启动可重新执行并与已写入项去重。（两个旧接口均在映射/持久化前 `throwApiExceptionIfFail`；任一异常退出 `migrateOnce`，版本只在最终快照确认后写入；确定性 ID 与批内去重测试保证重试幂等。）
- [x] M22 远端已存在同一确定性 ScheduleId 时迁移跳过创建，本地最终仍能看到该资源。（`existingScheduleId_isSkippedIdempotently` 验证不重复写；远端首次 Sync 投影链路由 N01/N02 覆盖。）

## 13. 远端恢复代码链路审查

本节不操作第二台设备，也不清理当前手机数据；通过源码、纯仓库 fixture 和自动化测试验证。

- [x] N01 空 Room 首次 Sync 会把远端分类、日程和 occurrence override 作为 discoveredResults 投影到本地。（planner/applier、Room store 与 snapshot projector 测试通过；discovered 资源按三类 identity 原子落库，坏行整次失败关闭。）
- [x] N02 snapshot projector 能恢复时间点、时间段、全天、重复、提醒、完成态、分类和课表关联全部字段。（`fourTimingKindsAreRestoredWithoutLosingTheirSemantics`、重复与 override 投影测试通过，领域校验在发布快照前闭合。）
- [x] N03 本地 Settings 专属状态（置顶、列表/时间轴视图）不进入服务端资源，也不会被远端数据伪造。（wire/领域 mapper 只包含服务端八个原子字段；置顶与视图模式仅由账号级 Settings 读取。）
- [x] N04 旧数据迁移使用确定性 ScheduleId；远端已有同 ID 时跳过创建并复用远端状态。（确定性 UUID、`existingScheduleId_isSkippedIdempotently` 与同批重复 ID 测试通过。）
- [x] N05 远端 tombstone 会阻止旧数据迁移或本地旧 upsert 复活已删除资源。（`tombstoneDeletesRemoteAndPending` 通过；响应应用器同时移除 remote/pending，迁移的确定性 ID 随后由远端删除事实继续约束。）
- [x] N06 首次 Sync 失败不会写入虚假的已同步状态，后续正常 Sync 仍能完整恢复。（结构失败、TransportFailure、InvalidResponse 均保留 pending/旧快照；只有完整响应通过身份与数量校验后才事务应用。）
- [x] N07 远端恢复完成后触发当前设备系统日历对账，但恢复流程不依赖日历权限才能完成。（账号 delegate 初始化成功后才登记日历 handoff；权限检查发生在独立导出 worker，失败只更新导出状态，不回滚仓库初始化。）

## 14. 自动化与代码链路回归

- [x] Q01 运行编辑状态与范围路由测试，覆盖 no-op、全天/时间点/时间段互转和三种重复编辑范围。
- [x] Q02 运行 RecurrenceEngine、SeriesSplitter 与 occurrence override 测试，覆盖日/周/月/年和边界日期。
- [x] Q03 运行本地 reducer、request planner、snapshot projector 测试，覆盖四态 timing、版本和部分结果。
- [x] Q04 运行 Room repository/JSON codec 测试，覆盖 pending、失败记录、可空分类和 destructive migration 配置。
- [x] Q05 运行系统日历 projection、Android Provider gateway 与对账测试，覆盖 0 分钟时间点和单日全天。（除 host 回归外，`:cyxbs-pages:schedule:persistentAndroidDeviceTest` 在小米真机执行 15 条 Provider 测试全部通过。）
- [x] Q06 运行旧清单/Transaction mapper 与 migration coordinator 测试，覆盖成功、跳过、重试和幂等。
- [x] Q07 运行课表 overlap、PageDecoration 和 Schedule service 映射测试，覆盖三种投射层级。（`:cyxbs-pages:course:view:desktopTest` 通过：零分钟时间点会切开后加入的时间段，三层重叠按时间片保留直接上层关系；真机分别验证 Deadline 位于时间段之上且仍可点击、Timed 三重叠层级与弹窗切换、AllDay 位于课程/事务底层并可打开详情。Schedule service 映射测试此前已通过，三种投射层级均有算法或真机证据。）
- [x] Q08 运行后端 wire strict decode、领域校验、同步、日常增删改与部分成功测试。（`schedulev2`、`schedulev2wire` 全部通过；全仓旧 `service`/友盟联网测试因历史断言、Nacos 与 IP 白名单失败，不属于 Schedule v2。）
- [x] Q09 代码审查账号切换链路：repository、session tag、迁移 scope 和回调均冻结账号，不做设备切号压测。（完整 `AccountSession` binding、命令 delegate 冻结、迟到快照/日历事件门禁及七个账号切换测试均通过。）
- [x] Q10 扫描客户端/后端日志代码与失败信息，确认不会暴露 token、Authorization 或实际敏感输入值。（客户端诊断日志仅按联调要求输出标题与结构摘要，不输出描述、提醒文案、请求体或 header；后端 Schedule v2 不记录请求 payload，校验错误只含字段路径、枚举或资源 identity。）

## 15. 问题记录与修复批次

### 15.1 本轮基线

- 分支/HEAD：`guoxiangrui/feature/schedule` / `8a90a0e7940120476eb9613d077a0c41b40f163b`。
- 应用：`com.mredrock.cyxbs.test`，`versionCode=94`，`versionName=6.10.6-alpha`。
- 环境：`https://be-dev.redrock.cqupt.edu.cn`，账号 `2020****88`（日志 requestId 中冻结账号为 `2020214988`）。
- 本地初始状态：分类 1、日程 1、实例例外 0、pending 0、失败记录 0；日程为 `222 / Unscheduled / version=1`。
- 首次 Sync：confirmed 1、discovered 0、upsert 0、delete 0，服务端确认 `222 / version=1`。
- 安全核对：实际 Schedule 日志及日志实现均不输出 token、Authorization、Cookie、描述或完整 JSON；详细日志仅包含标题、日期时间、周期、资源版本和结果摘要。
- 本轮新增资源统一使用 `E2E-SV2-0831-` 前缀。
- 账号隔离审查：`AccountSwitchingScheduleRepository` 以完整 `AccountSession` 建立 binding，切号先发布空快照并取消旧初始化/收集任务；Ktor 网关冻结 exact session，发包前再次校验 accountId，旧账号迟到快照和网络身份均不能进入新账号。
- 分类基线用例实际使用 ASCII 名称 `E2E-SV2-0831-CatA/CatB`（避免 ADB 输入法对中文注入的限制）；两者均远端创建成功，重启后的首次 Sync 为成功且列表仍存在。
- 自动化基线：`:cyxbs-pages:schedule:desktopTest` 311 条、`:cyxbs-pages:schedule:testAndroidHostTest` 314 条，合计 625 条全部通过；课表 `view` 新增 2 条重叠纯算法测试并通过，`widget` 的 `desktopTest` 仍为 `NO-SOURCE`。
- 后端基线：`go test -timeout 30s ./...` 中 `schedulev2`、`schedulev2wire` 通过；旧 `service` 包存在历史断言失败及 24 小时 sleep，`tool/umeng` 依赖真实 IP 白名单，故只记录为仓库既有非 Schedule 阻塞项。

| ID | 用例 | 状态 | 现象与证据 | 根因 | 修复提交 |
|---|---|---|---|---|---|
| BUG-001 | R01 / 周期日程 CREATE 与 SYNC | 已修复并部署 | 客户端发送 `weekdays/monthDays/months=[]`，服务端创建成功但响应把后两项编码为 `null`，客户端 `JsonConvertException`；本地保持 PendingUpsert，部署后 Sync 收敛。 | Go 领域 canonical 函数以 nil 为目标 append，空切片被折叠为 nil，违反现有 required-list wire 合同。 | 后端 `539793d` |
| BUG-002 | Q05 / Android 真机日历测试无法编译 | 已修复 | `androidDeviceTest` 引用已删除的 W42 mapper 与旧 finalized worker helper，导致 Provider 真机回归长期无法启动。 | 正式日历恢复/worker 分层调整后，两个独立 device smoke/契约测试未同步当前 API；真实 Provider 测试本体未受影响。 | 客户端 `bffcd4fa9` |
| BUG-003 | Q05 / 真机 Provider 两条失败 | 已修复 | 15 条中 expectedCount 竞争测试和非法 RDATE 恢复测试失败；修正后 15/15 通过。 | 前者依赖“第 10 次权限回调”的脆弱时序；后者仍期待普通 update 静默修复畸形 row，与当前 fail-closed + 显式重建策略相反。 | 客户端 `bffcd4fa9` |
| BUG-004 | L02 / 时间轴全天标题 | 已修复 | 同日存在多个全天项且标题较长时，sticky 标题会绘制到对应全天色条边界外。 | 全天色条和 sticky 标题分层绘制；底层色条有 6dp 圆角裁剪，但上层标题列没有复用该裁剪边界。 | 客户端 `877ef53b1` |
| BUG-005 | U05 / 课表详情保存编辑 | 已修复 | 从清单页改期可正常提交；从课表全天详情把 9 月 7 日改为 9 月 8 日后，弹窗关闭但没有本地命令、网络请求或课表更新。 | 课表详情和事务新建使用 `rememberCoroutineScope` 持有持久化命令，保存时编辑器同步关闭 Window，Composable 离场会取消尚未执行的协程。改为进程级应用 scope 后，UI 生命周期不再取消落库与同步。 | 客户端 `7d3eebf22` |
| BUG-006 | E00-07 / 非主页接收 deep link | 已修复 | 冷启动或停留主页时 deep link 可用；进入清单后再发送 `cyxbs://course`，系统确认 Intent 已送达 MainActivity，但应用无解析日志且页面不跳转。 | `onNewIntentListener` 绑定在主页 Composable，离开主页后随 `onDispose` 被移除；直接在 Activity `onCreate` 入栈又早于导航栈首次组合。现改为 App 根组合中的常驻 `DisposableEffect`，在 `AppNavDisplay` 就绪后处理初始 Intent，并跨页面持续监听。 | 客户端 `cfdf6868f` |
| BUG-007 | A05 / 事务改期后首次开启重复 | 已修复 | 把非重复事务日期从 9 月 14 日改到 9 月 15 日并开启每周重复时，选择器曾显示旧日期星期；修正后显示“每周二”，本地 pending 使用 9 月 15 日。 | 编辑器无条件使用父日程旧 timing 作为 recurrence 预览 anchor；projector 又错误要求稳定 anchor 必须等于当前 timing 日期，导致合法的系列移动无法发布本地快照。 | 客户端 `13e698f79` |
| BUG-008 | A05 / 清除重复后重新启用 | 已修复并推送，部署未生效 | 同一事务曾经清除重复，再改日期重新启用时，dev/test 后端返回 `REJECTED/INVALID_REQUEST`；后端提交推送后于 07:57、正常包覆盖安装后于 08:09 再次 Sync，响应仍为旧行为且 pending=1。 | 后端把首次 anchor 锁定到整个 Schedule 生命周期；但 recurrence 清除已经结束旧 occurrence 序列，重新启用应在同一 Schedule identity 下以当前 timing 建立新序列。 | 后端 `32e9fe3`，已推送 `dev/test` |
| BUG-009 | E00-07 / 裸课表 deeplink | 已修复 | 根监听修复后，`cyxbs://course?stuNum=2020214988` 可跳转，但裸 `cyxbs://course` 仍记录“未识别页面协议”并停留原页；覆盖安装修复包后，从清单页发送裸 URI 可进入当前账号第一周课表。 | 导航 KSP 依据字段可空性生成协议，`stuNum: String` 即使业务上可推导仍会被生成为必填 query。现改为可空参数，并只在 `CourseNavEntry` 内把省略值解析为当前登录学号；显式学号行为不变。 | 客户端 `6ad426277` |
| BUG-010 | A12 / 完整课表创建事务 | 已修复 | 主页课表空白处可以创建事务，但裸 deeplink 打开的完整课表对轻击和长按均无响应；修复包覆盖安装后，轻击会生成一小时占位项，点击占位项可打开事务编辑器，并成功创建每日/每周事务。 | `HomeCourseFrame` 注册了 `CreateItemPageDecoration`，重构后的 `AdaptiveCourseFrame` 却遗漏该层；完整课表因此没有任何空白手势接收者。现为自适应课表补充平台 factory 并注册创建 Decoration，不改数据库或协议。 | 客户端 `772b77f78` |
| BUG-011 | K02 / 日历权限永久拒绝引导 | 已修复 | 课表事务详情中永久拒绝日历权限后，再点“去授权”会关闭当前详情且看不到应用的设置引导；修复包中引导保留在当前 Window，点击“去设置”直达应用详情，授权返回后仍停留原事务详情。 | Android actual 原先独立组合 `ScheduleConfirmDialog`，但课表详情由外部 BottomSheet/Window 承载，权限回调与宿主离场会让独立引导状态丢失。现由平台实现提供 overlay 内容，并统一挂到 `EditScheduleDialog` 根层，同一窗口内完成引导和返回。 | 客户端 `ad3ce1ba2` |
| BUG-012 | A14 / ADB 点击重叠事务误判 | 已排除 | 辅助树给两个事务片段返回的中心分别为 `y=815/961`，点击后落到空白创建层；一度误判为切割片段无法接收事件。 | Compose 会为极小 `clickable` 扩大无障碍触摸边界，辅助树中心并不等于真实绘制中心。尺寸日志显示事务真实范围为 `y=870..926`、时间点为 `y=861..906`；点击剩余可见区域 `y=915` 正常打开事务详情，因此无需修改事件分发。 | 无代码改动 |
| BUG-013 | E00-07 / 重复日程 deeplink | 已修复 | 首次携带 `scheduleId` 的 deeplink 能定位 `222`；保持日程页在栈顶、滚动或切换分组移开目标后，再发送完全相同 URI 不再定位。修复包中裸 URI 与系统日历 `v/scope/kind` URI 均可重复定位，且不会堆叠重复页面。 | `AppNavBackStack.push` 会拒绝与栈顶完全相等的参数，而页面已把首次参数标记为 consumed；第二个 Intent 虽被根监听解码，却没有新的导航状态或页面事件。现仅在相同日程参数已位于栈顶时派发页面内重定位请求，其他导航保持原行为。 | 客户端 `b5e8e3c58` |

### 修复批次规则

- 同一链路的 2～5 个小问题可合为一个提交；崩溃、数据丢失、错误删除等高风险问题发现后立即单独修复。
- 每个修复先补最小可复现测试，再执行对应失败用例和受影响的相邻用例。
- Commit 正文记录问题编号、行为变化、数据兼容边界和实际验证命令。
- 每批提交后重新检查 `git status`，测试注入、token 和临时日志不得进入 Git。

## 16. 最终清理与验收

- [ ] Z01 删除所有 `E2E-SV2-0831-` 分类、日程和 occurrence override，不触碰其他数据。
- [ ] Z02 确认本地 pending=0、失败记录无测试残留、后端 Sync 不再下发测试资源。
- [ ] Z03 恢复迁移版本、网络模拟、权限与系统日历测试环境。
- [ ] Z04 汇总通过/失败/跳过数量、所有修复提交和仍需人工确认的视觉项。
