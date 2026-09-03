# Schedule Android 端到端验收清单

## 1. 目标与执行约定

本清单以重构前 194 项真机验收为基线，覆盖清单、课表事务、同步、系统日历、旧数据迁移和本轮新协议。
除被新数据结构明确替代的旧断言外，不删除原有测试维度。每完成一项立即把 `- [ ]` 改为 `- [x]`；失败项保持未勾选，并在“问题记录与修复批次”登记触发路径、根因和修复提交。

- 测试设备：小米 `2211133C / fuxi`，ADB serial `3ad7ce04`；执行期间保持设备连接和解锁，不关闭会影响小米互联的系统连接。
- 测试应用：`com.mredrock.cyxbs.test`，只使用项目 `.codex/scripts/install-test-debug.sh` 覆盖安装；不得卸载应用或执行整包 `pm clear`。
- 测试后端：测试包当前配置的 dev/test 环境。允许读取测试包内的登录凭证、token 和账号标识，仅用于本轮测试请求；不得在日志、文档、提交或最终报告中输出完整值。
- 数据权限：允许读取、创建、修改和删除测试包内的测试数据，并可操控测试包内任何功能以完成验收；不触碰测试包之外的个人应用和数据。
- 当前应用内均为可清理测试数据，不备份、不长期保存。新增资源统一使用 `E2E-SCHEDULE-0903-` 前缀，便于远端核对和最终清理。
- 每个远端写操作至少核对：界面结果、本地 Room/pending、Schedule 网络日志、服务端回包和下一次 Sync 收敛结果。
- 测试期间可读取 Pandora、Room/WAL、系统日历 Provider 和脱敏网络日志；临时导出只放本机临时目录，验收后清理。
- 迁移版本、应用级网络故障注入和其他测试开关属于临时代码，验证结束必须还原，不得进入正式提交。
- 修复按问题相关性分批提交；崩溃、数据丢失、错误删除等高风险问题立即单独修复。测试文档勾选不与无关功能混入同一提交。
- 不测试多设备并发修改、请求中途杀进程等低频极端场景。账号隔离和换设备远端恢复只做代码链路与自动化测试。
- 离线场景优先使用不影响 ADB/小米互联的应用级网络故障注入；如果没有安全注入方式则标记跳过，不关闭设备网络。
- 系统日历授权若必须由用户手动确认可标记跳过；不得为此中断其余不依赖权限的用例。
- 若单项需要新的数据表、全新协议或用户授权，先跳过并记录；若该阻塞影响大量后续用例，则暂停目标并向用户汇报。
- 不以截图作为完成证据；记录可复现的触发路径、日志摘要、数据库状态和代码根因。

## 2. 环境与安全基线

- [x] E01 ADB 只连接目标测试设备，设备保持解锁，使用项目配置的覆盖安装脚本。
- [x] E02 确认启动的是正式 `cyxbs://schedule` 页面，而不是内部 Preview。
- [x] E03 记录脱敏账号、客户端 commit、后端环境和初始本地/远端数量。
- [x] E04 检查日志不输出 token、Authorization、Cookie、完整请求体或用户敏感描述。
- [x] E05 首次 Sync 成功，`confirmedResults/discoveredResults/upsertResults/deleteResults` 与请求位置对应。
- [x] E06 代码审查账号 repository、AccountSession tag、迁移设置和系统日历所有权均按账号隔离。
- [x] E07 验证 `cyxbs://schedule`、`schedule/settings`、`schedule/category`、`schedule/failures` 冷启动和热启动路由。
- [x] E08 重复投递同一 Schedule deeplink 能再次定位目标，不在导航栈堆叠重复页面。
- [x] E09 记录本轮统一测试前缀，并确认最终可按远端 ID 精确清理而不误删其他数据。
- [x] E10 确认当前测试包、客户端 commit 和后端部署 commit 与记录一致。

## 3. 分类管理

- [x] C01 新建分类，名称、配色、排序保存成功；客户端 local UUID 不出现在服务端响应和数据库。
- [x] C02 新建分类和引用它的日程一次提交，服务端使用 `categoryLocalId` 正确建立远端引用。
- [x] C03 新建同名分类时客户端阻止；绕过客户端提交时服务端合并到已有分类并返回 canonical ID。
- [x] C04 分类改名后，已有 Schedule 和单次调整仍引用同一远端分类。
- [x] C05 分类更新为同名时服务端返回 `REJECTED / DUPLICATE_CATEGORY_NAME`，其他资源仍可成功。
- [ ] C06 修改配色 JSON 后，清单时间轴和课表投影使用新背景色/文字色。
- [x] C07 拖动分类顺序，重启和 Sync 后顺序保持。
- [x] C08 删除未引用分类，本地和服务端均物理删除。
- [x] C09 删除仍被日程或单次调整引用的分类时客户端拦截；直连接口返回 `CATEGORY_IN_USE`。
- [ ] C10 分类名称空白、首尾空格和常用中文符号处理一致。
- [ ] C11 连续多次改名/换色/排序只保留最终 pending，不生成重复分类。
- [ ] C12 网络失败时分类本地立即可见，恢复后 Sync 收敛。
- [x] C13 固定分类“学习/生活/其他”不提供删除入口，自定义分类提供删除入口。
- [ ] C14 逐一抽查全部候选配色，背景色/文字色 JSON 在 Room、wire 和远端往返后字段完整。
- [ ] C15 分类被多个 TODO 与 AFFAIR 引用时，改名、换色和排序不改变引用 ID。
- [x] C16 创建分类回包的 canonical 远端 ID 正确绑定本地 UUID，后续更新不再携带 `categoryLocalId`。
- [ ] C17 两个本地分类绕过校验提交同名创建时，均映射到同一 canonical 分类并合并本地引用。
- [x] C18 分类更新同时发生名称冲突与颜色变更时整条分类更新拒绝，不产生半更新。
- [ ] C19 分类被引用数量在新增、解绑、删除日程后即时刷新，删除拦截结果正确。

## 4. 清单日程创建矩阵

- [x] T01 创建时间点日程，日期和分钟准确。
- [x] T02 创建时间段日程，开始/结束和最短 30 分钟约束准确。
- [x] T03 创建单日全天日程，不显示伪造的 00:00 时间段。
- [x] T04 创建准时提醒，wire 中为 `minutesBefore=0`。
- [x] T05 创建提前 10 分钟提醒，列表、详情和 wire 文案一致。
- [ ] T06 创建关联课表的时间点、时间段和全天日程，课表点击均打开同一 Schedule ID。
- [x] T07 新建 UI 不提供无日期日程；旧 `UNSCHEDULED` 能正常展示。
- [x] T08 `UNSCHEDULED` 点击提醒或关联课表时只提示先设置时间，不产生更新。
- [x] T09 空标题和纯空格标题无法保存；绕过客户端时服务端逐项拒绝。
- [x] T10 未填写内容点击外部或下拉直接放弃，不弹未保存确认。
- [x] T11 已填写内容点击外部或下拉立即弹确认；继续编辑回弹并保留草稿。
- [x] T12 创建 00:00、23:59 时间点，不跨天。
- [x] T13 创建 00:00–00:30、23:29–23:59 时间段，两端滚轮均可继续操作。
- [x] T14 调整开始或结束导致区间不足 30 分钟时，只自动移动另一端。
- [ ] T15 标题和备注含中文、英文、数字、emoji、换行，列表/详情/同步不乱码。
- [x] T16 两条日程允许同标题，UUID 不同且互不覆盖。
- [x] T17 快速连续点击保存只产生一次命令和一条 Schedule。
- [ ] T18 创建失败或连接失败后本地仍显示并保留 pending；失败记录可定位到编辑弹窗。
- [ ] T19 一次创建同时设置新分类、提醒、重复和课表关联，分类先解析后日程引用正确。
- [x] T20 只含标题时 UI 要求先选择日期/时间，不主动创建新的 `UNSCHEDULED`。
- [ ] T21 从旧数据恢复的 `UNSCHEDULED` 能补充日期成为全天，或补充钟点成为时间点/时间段。
- [x] T22 创建同标题但不同时间的两条日程，固定长度 Schedule UUID 不同且互不覆盖。
- [ ] T23 创建请求成功但响应丢失后允许出现重复普通日程，不把 requestId 误当资源幂等键。
- [x] T24 创建新分类失败时，同请求中引用其 localId 的日程被拒绝，其他独立资源仍成功。
- [ ] T25 创建 TODO 默认有完成态；创建 AFFAIR 默认无完成态且不展示分类入口。
- [x] T26 创建有日期但无钟点的日程只能表达为单日全天，不生成跨天全天区间。

## 5. 重复规则与单次调整

- [x] R01 创建每日重复，分别验证永不结束、一次、多次、截止日期。
- [ ] R02 创建每周多星期重复，只生成选中的星期；起始日不必属于选择集合。
- [x] R03 创建每月 29/30/31 日重复，短月跳过而非夹到月末。
- [x] R04 创建每年 2 月 29 日重复，平年跳过且后续闰年继续。
- [x] R05 全天、时间点、时间段分别创建重复日程，类型和实例 identity 正确。
- [x] R06 完成一个重复实例只影响该次；取消完成后恢复。
- [ ] R07 仅此次修改标题、备注、分类、日期、时间、提醒，各字段独立保存。
- [x] R08 已有单次时间修改后只改单次日期，时间 patch 保留；反向同理。
- [x] R09 删除仅此次保存 `CANCELLED` 且旧 patch 清空，其他实例不受影响。
- [x] R10 还原单次调整后物理删除 adjustment，该次完全继承父系列。
- [x] R11 同一 Schedule 和原始日期的两个首次创建请求最终合并成一个远端 adjustment ID。
- [x] R12 已确认 adjustment 更新始终使用相同远端 ID，版本递增；本地 UUID 不变。
- [x] R13 修改整个系列标题/分类后，单次状态保留且未覆盖字段继承新值。
- [x] R14 修改整个系列日期/时间后，单次显式 date/time 保持，`originalOccurrenceDate` 不变。
- [x] R15 修改规则使原始槽暂时消失，单次调整不展示；规则改回后同一调整恢复。
- [x] R16 关闭重复规则后所有 adjustment 被物理删除；重新开启不会复活旧调整。
- [ ] R17 “此次及以后”截断旧系列并创建 UUID v7 新系列，边界前后实例和调整归属正确。
- [ ] R18 删除“此次及以后”正确截断；从第一项执行时直接删除系列。
- [x] R19 有限系列只剩最后可见实例时删除该次，父 Schedule 一并删除。
- [x] R20 每周选择集合从周一/周三/周五改为周二/周四后，旧实例消失且新实例无重复。
- [ ] R21 日、周、月、年重复分别验证 count=1、count>1、until 和 never 的结束边界。
- [x] R22 单次仅修改标题时，备注、分类、日期、时间、提醒和完成态继续继承父系列。
- [x] R23 单次仅修改日期时已有 time Patch 保留；仅修改时间时已有 date Patch 保留。
- [x] R24 单次调整的本地 UUID 不上传服务端；首次创建后绑定服务端 adjustment ID。
- [ ] R25 两台逻辑客户端对同一 `scheduleId + originalOccurrenceDate` 首次新增时，服务端字段级合并并返回同一 canonical ID（自动化/直连验证）。
- [ ] R26 已确认单次调整的并发字段修改按各 AtomicField.modifiedAt 合并，不以整行覆盖。
- [ ] R27 删除仅此次后再修改该槽提醒/标题，新的 adjustment 不复活删除前的旧 Patch。
- [x] R28 还原单次调整只在最终确认保存后生效；确认前课表、清单和网络均不变化。
- [x] R29 还原后再次仅此次修改，继承父系列当前字段，而不是历史单次字段。
- [x] R30 单次完成后再调整时间，随后还原内容 Patch 时完成态保留；取消完成后不复活旧 Patch。
- [x] R31 整体移动父系列日期时，`originalOccurrenceDate` identity 保持原槽；显式单次 date 仍保持不动。
- [x] R32 父规则使原槽休眠时，该调整不出现在“可还原的单次调整”列表；规则恢复后重新生效。
- [x] R33 关闭重复规则物理删除该系列全部 adjustment；同一日期重新开启重复不会复活旧调整。
- [ ] R34 删除整个系列物理删除父 Schedule 与其全部 adjustment，不保留服务端墓碑。

## 6. 普通修改、完成与删除

- [x] U01 修改标题、备注、分类，每个 AtomicField 的 `modifiedAt` 只在对应字段变化时更新。
- [x] U02 时间点、时间段、全天三种 timing 互转，日期保持且多余字段不会残留。
- [ ] U03 `UNSCHEDULED` 显示“未设置日期/时间”，选择日期可变全天，选择钟点可变时间点/时间段。
- [x] U04 提醒按“不提醒 → 准时 → 提前 10 分钟 → 不提醒”往返。
- [x] U05 开关课表关联后投影新增/移除，清单本体不丢失。
- [x] U06 改为“未分组”上传 `categoryId.data=null` 并正常发起更新。
- [x] U07 打开但不修改直接保存或返回，不发 UPDATE、不增加本地 revision。
- [x] U08 完成普通清单后从未完成区移除，七天内在已完成区显示，超过七天隐藏。
- [x] U09 已完成普通清单继续编辑时不意外恢复完成态。
- [x] U10 删除日程出现二次确认；确认后本地和远端物理删除，重复删除仍视为成功。
- [x] U11 请求期间继续修改同一资源，旧响应只清旧 revision，新修改仍为 pending。
- [x] U12 业务拒绝后修改成功或删除资源，关联失败记录自动清理。
- [x] U13 清空备注、分类和提醒分别生成明确空值语义，不误清其他 AtomicField。
- [x] U14 修改日期跨周、跨月、跨年时标题、提醒、重复、分类和来源不被重置。
- [x] U15 全天 → 时间点/时间段使用原日期；切回全天只移除钟点语义。
- [x] U16 调整开始时间不足 30 分钟时只推后结束；调整结束时间时只提前开始。
- [x] U17 在 00:30 和 23:29 边界调整，滚轮不被另一端错误限制且结果不跨日。
- [ ] U18 TODO ↔ 关联课表、AFFAIR ↔ 关联清单的切换保持来源语义与完成态规则。
- [x] U19 删除重复系列的“仅此次”确认文案说明可在重复设置中还原；“此次及以后/全部”说明不可恢复。
- [x] U20 删除服务端不存在但客户端确认过的资源按已删除处理，并清除本地 pending/失败记录。
- [x] U21 同一资源连续修改时只发送最终本地状态；旧回包不得覆盖请求期间的新 revision。
- [x] U22 修改保存成功后编辑弹窗切回详情态而非直接关闭；创建事务成功后同窗口切为详情。

## 7. 清单列表、时间轴与 Feed

- [ ] L01 未完成排序为超期、置顶、临期、普通、无时间；各组内部稳定。
- [ ] L02 置顶/取消置顶写入账号 Settings，删除日程后自动清理置顶 ID。
- [ ] L03 左滑置顶、删除、恢复图标及按钮范围正常；完成移动有动画。
- [ ] L04 批量全选已置顶项时按钮显示取消置顶，勾选样式一致。
- [ ] L05 分类横向列表能滑到自定义分类并正确筛选。
- [x] L06 列表和时间轴切换状态持久化；两种视图使用同一 repository 数据。
- [ ] L07 时间轴使用分类配色；时间点、时间段、全天和提醒信息展示正确。
- [ ] L08 空未完成、空已完成状态图及高度适配浅色/深色和系统栏。
- [x] L09 Feed 卡片左右滑置顶/删除，时间、提醒和课表关联图标与清单页一致。
- [ ] L10 Feed 点击后进入清单并短暂高亮对应项，不露出侧滑操作区。
- [ ] L11 临期和超期的背景、文字、旗帜图标在浅色/深色下符合模块配色。
- [x] L12 时间点、时间段、全天、无时间和有提醒项在列表中使用正确摘要；时间段不得只显示开始时间。
- [x] L13 提醒文本展示为“准时提醒/提前 X 分钟提醒”，与时间左侧图标和课表关联图标对齐。
- [x] L14 已完成普通清单仅展示 7 天内记录；边界当天不被提前隐藏。
- [ ] L15 完成/取消完成、置顶/取消置顶和高亮均有动画，动画不把侧滑按钮一起显示。
- [ ] L16 列表空态、时间轴空态及分组筛选空态分别正确，不复用错误高度或图片。
- [ ] L17 页面右下角悬浮按钮在有/无系统导航栏时均保持设计间距，不被底部导航遮挡。
- [ ] L18 列表与时间轴切换图标、标题栏、返回键和细线在浅色/深色下对齐。
- [x] L19 Feed 顶部到期横幅文案、箭头、间距和点击跳转正确；没有待办时显示约定空文案。
- [x] L20 Feed 卡片完成圆圈颜色、时间图标、提醒和课表关联图标与清单页一致。

## 8. 课表事务与投影

- [ ] A01 仅当前登录账号自己的主页课表展示日程并允许创建，其他人的课表不展示也不允许创建。
- [ ] A02 课表空白处创建事务，保存为 `AFFAIR + TIMED + linkedToCourse=true`。
- [ ] A03 新事务保存后创建弹窗原位切换为已保存详情。
- [ ] A04 事务默认不显示分类、不属于清单；关联清单后出现完成按钮和默认分类。
- [ ] A05 事务关联清单后仍保留 AFFAIR 条纹，底色使用分类配色。
- [ ] A06 事务取消关联清单后去掉完成态与分类显示，但继续留在课表。
- [ ] A07 TODO 完成后不再投影课表；AFFAIR 关联清单后完成仍保留课表投影。
- [ ] A08 Deadline 使用最小高度并切开重叠时间段，点击后置顶展开。
- [ ] A09 Timed 与课程/其他事务重叠时分栏、点击切换和底部详情正确。
- [ ] A10 AllDay 位于课程/事务底层、只铺当天列，可点击且不阻断空白长按创建。
- [ ] A11 长按拖动已有 Schedule 只提供视觉反馈，松手回原位，不修改时间。
- [ ] A12 同一 Schedule 不会因学期 dayIndex 换算出现在相邻两个 page。
- [ ] A13 编辑弹窗展开日历、重复和提醒区域时高度自适应，动画期间不透出课表背景。
- [ ] A14 键盘弹起只按目标区域上移标题/信息/描述，课表滚轴不发生反向滚动。
- [ ] A15 仅主页当前登录学号课表注册创建 Decoration；他人课表不展示本人 Schedule，也不能创建事务。
- [ ] A16 课表普通点击空白生成一小时临时事务，长按创建手势只在支持的主页课表生效。
- [ ] A17 同一时段两个或三个事务/课程重叠时，点击下层项后以该项为锚点展开并可左右切换详情。
- [ ] A18 时间点优先级高于时间段，参与重叠切割且上下保留 8dp 文字空间，不遮挡相邻内容。
- [ ] A19 全天日程左右 padding、圆角、层级和点击区域与普通课表项协调，不覆盖上层课程。
- [ ] A20 事务关联清单后从清单页编辑，再回课表仍保留 AFFAIR 条纹和分类配色。
- [ ] A21 课表详情弹窗导航栏区域被同色填充，不压缩内容高度；信息区图标适配深色模式。
- [ ] A22 编辑区打开/关闭日历、重复、提醒时按内容自适应高度，动画期间圆角与顶部阴影不透底色。
- [ ] A23 日程拖动松手后回原位置，不向 repository 提交时间修改。
- [ ] A24 课表点击删除二次确认、修改无变化不弹范围选择、取消编辑先完整折叠再关闭。

## 9. 同步、部分成功与失败记录

- [ ] S01 空本地首次 Sync 能恢复分类、四种 timing、重复、提醒、完成态、关联和单次调整。
- [x] S02 confirmed 同版本返回 `CONFIRMED`，版本不同时返回 `CHANGED + resource`，缺失返回 `DELETED`。
- [x] S03 远端独有数据通过 `discoveredResults` 下发并创建本地 UUID 映射。
- [x] S04 同一请求中一项 `REJECTED`、其他项成功；客户端先应用成功项，只保留失败项 pending。
- [x] S05 新分类失败时只拒绝引用其 localId 的 Schedule/adjustment，其他资源不受影响。
- [x] S06 更新一个远端已删除资源返回 `DELETED`，客户端视为删除并清除 pending。
- [ ] S07 DELETE 目标不存在仍返回 `SUCCESS`，客户端完成清理。
- [x] S08 网络恢复只在本地有 pending 时自动 Sync；无 pending 不额外发请求。
- [x] S09 结构畸形响应、结果数量或位置不匹配时整次 fail-closed，不把结果写给错误资源。
- [ ] S10 失败记录卡片包含操作时间、资源摘要和安全原因；点击打开对应编辑页。
- [ ] S11 失败记录不保存或展示 token、完整 payload、数据库错误或服务端堆栈。
- [ ] S12 同一资源连续失败更新为最新源数据和失败原因，不堆积过时副本。
- [x] S13 CREATE/UPDATE/DELETE/SYNC 均返回 HTTP 200 + data/status/info；业务拒绝不使用 HTTP 400。
- [x] S14 请求中的 confirmed/upserts/deletes 与响应 confirmedResults/upsertResults/deleteResults 按位置一一对应。
- [ ] S15 `discoveredResults` 只下发客户端未声明的远端资源，不与 confirmedResults 重复。
- [x] S16 分类、Schedule、adjustment 的某一项失败不使同请求其他合法项回滚；依赖失败仅传播到引用项。
- [x] S17 分类同名创建返回合并后的 canonical 资源；同名更新返回 `DUPLICATE_CATEGORY_NAME`。
- [x] S18 相同 Schedule/原始日期的 adjustment 创建合并后，客户端以双 ID 绑定本地 UUID 与远端 ID。
- [x] S19 服务端物理删除后 Sync 的 confirmed 缺失返回 DELETED；客户端不会用旧 pending 复活。
- [x] S20 DELETE 不上传版本，重复删除按 SUCCESS 处理；删除与更新竞态按最终服务端结果收敛。
- [ ] S21 业务拒绝 reason/info 只包含业务字段路径和安全描述，不回显实际输入、凭证、SQL 或堆栈。
- [ ] S22 未知错误若属于纯业务可安全下发；畸形 JSON/未知字段只说明位置，不携带请求值。
- [x] S23 本地 pending 为零时首次进入和网络变化不产生多余 mutation；有 pending 时网络恢复触发 Sync。
- [x] S24 响应中 canonical 资源版本连续增长；字段 modifiedAt 合并后不会倒退。
- [ ] S25 Room destructive migration 后本地临时状态允许丢弃，首次 Sync 可从远端完整重建。

## 10. 系统日历与提醒权限

- [ ] K01 首次开启提醒先说明依赖系统日历，确认后请求权限。
- [ ] K02 拒绝后回到不提醒；永久拒绝时引导到应用设置。
- [ ] K03 远端提醒在未授权设备显示“准时/提前 X 分钟（未授权）”，非编辑态点击可授权。
- [x] K04 时间点写为零时长事件，时间段和单日全天边界准确。
- [x] K05 删除受管事件后重新进入，应用对账能恢复缺失事件。
- [x] K06 用户创建的同名或所有权不匹配事件被忽略，不覆盖、不删除。
- [ ] K07 日、周、月、年规则以及单次取消/调整与应用内实例一致。
- [x] K08 `UNSCHEDULED` 不写系统日历。
- [x] K09 受管日历名称与内部识别标记均为“掌邮日程”约定，不再创建“邮子清单”。
- [ ] K10 已授权后撤销权限，应用不崩溃；远端提醒转为“未授权”并可重新申请。
- [x] K11 完成普通 TODO 后移除其系统日历投影；恢复未完成后按当前状态重新导出。
- [x] K12 完成/取消重复实例只影响对应 occurrence，不删除系列其他实例。
- [x] K13 全天事件使用单日半开区间，系统日历不多显示或少显示一天。
- [ ] K14 修改父规则、仅此次、此次及以后后，Provider 主事件和 exception 与应用内实例一致。
- [x] K15 系统日历存在应用旧格式行时，显式覆盖机制可重建而不会永久卡在对账失败。

## 11. 旧数据迁移

- [ ] M01 临时提高 migrationVersion 后覆盖安装，只触发一轮；完成后写账号级版本。
- [x] M02 旧清单无时间、有截止、仅通知时间和非法时间分别按迁移文档映射。
- [x] M03 旧清单准时/提前/晚于截止提醒映射正确。
- [x] M04 旧清单完成态、重复、固定分类和置顶顺序正确。
- [x] M05 旧清单日/周/月/年规则能无损时保留，不能无损时安全降级。
- [x] M06 旧 Transaction 全学期、指定周和多个时间位置映射为独立 AFFAIR。
- [x] M07 脏标题、周次、星期、节次只跳过对应项，不阻塞合法项。
- [ ] M08 首次迁移后端不可用时本地保存 pending，完成标记与后续恢复一致。
- [x] M09 重复执行迁移时 UUID v5 保证 Schedule 不重复创建。
- [x] M10 远端已有相同迁移 UUID 时复用现有数据。
- [x] M11 Android 与 iOS 调用同一 commonMain 迁移逻辑，截止时间后不再请求旧接口。
- [x] M12 旧清单 `is_done=1`：非重复迁移为 COMPLETED，重复系列保持父 PENDING 并记录对应实例状态。
- [x] M13 学习/生活/其他/未知/空分类映射正确，已有同名分类优先绑定 canonical 远端分类。
- [x] M14 多条旧置顶清单迁移顺序稳定，并与 Settings 现有置顶项合并而不是覆盖。
- [x] M15 旧提醒晚于截止时丢弃；非法时间且无备用日期时安全迁移为 `UNSCHEDULED`。
- [x] M16 旧日重复、多星期周重复、29/30/31 月重复分别映射到当前日/周/月规则。
- [x] M17 旧年重复可无损表达时保留 YEARLY，无法无损表达时降为下一次单次日程。
- [x] M18 Transaction `week=[0]` 映射整学期 WEEKLY；连续周和稀疏周生成明确实例。
- [x] M19 一个旧事务含多个 AtWhatTime/同日节次时，每个位置生成独立稳定 Schedule UUID v5。
- [ ] M20 旧接口任一读取失败时不写迁移完成版本；下次启动可重试并以 UUID v5 去重。
- [x] M21 远端已存在相同 UUID v5 时复用已有 Schedule，不创建重复数据。
- [x] M22 迁移截止时间已过时 Android/iOS 都不再访问旧接口，也不改变迁移版本。

## 12. 远端恢复代码链路审查

- [x] N01 空 Room 首次 Sync 将远端分类、Schedule 和 adjustment 通过 discoveredResults 投影到本地。
- [ ] N02 恢复时间点、时间段、全天、无时间、重复、提醒、完成态、分类、来源、关联和字段 Patch。
- [ ] N03 Settings 专属状态（置顶、视图模式）不进入服务端资源，也不被远端数据伪造。
- [x] N04 旧数据 UUID v5 在远端已存在时复用；普通新建 UUID v7 仍保持客户端稳定 identity。
- [x] N05 服务端物理删除事实阻止 confirmed 旧资源和 stale pending 复活。
- [ ] N06 首次 Sync 失败不写虚假已同步状态；后续成功 Sync 可完整恢复。
- [ ] N07 远端恢复后触发当前设备系统日历对账，但 repository 初始化不依赖日历权限。
- [x] N08 账号切换会先停止旧 session 收集并发布空快照，迟到回包无法写入新账号。

## 13. 自动化与代码链路回归

- [x] Q01 客户端 `:cyxbs-pages:schedule:desktopTest` 全部通过。
- [x] Q02 客户端 Android、iOS、Desktop 相关 source set 编译通过。
- [x] Q03 课表 overlap、PageDecoration 和 Schedule service 聚焦测试通过。
- [x] Q04 后端 `schedule`、`schedulewire`、`dao` 和 Schedule service 聚焦测试通过。
- [x] Q05 客户端和后端 `git diff --check` 通过，旧业务命名和废弃协议字段扫描为空。
- [ ] Q06 最终清理测试数据，确认 pending 和失败记录无测试残留。
- [x] Q07 客户端本地 reducer、capture、response applier、snapshot projector 和 Room repository 全量测试通过。
- [x] Q08 客户端 Schedule wire、领域 mapper、重复引擎、迁移、日历 projection 聚焦测试通过。
- [x] Q09 Android test debug 覆盖安装成功，正式清单入口与课表入口均可运行。
- [x] Q10 后端 decode、领域合并、DAO、handler、mutation/sync、部分成功和安全错误测试通过。
- [x] Q11 客户端/后端源码中业务命名不再含 V2；允许历史测试变量或第三方资源中的非业务文本。
- [x] Q12 检查账号隔离、服务端 owner 校验、adjustment.ownerId 和跨账号资源拒绝链路。

## 14. 问题记录与修复批次

### 14.1 本轮基线

- 客户端分支/commit：`guoxiangrui/feature/schedule` / `2300f789b`。
- 后端分支/commit：`dev/test` / `2809c71`，已推送并由 dev/test CI 部署。
- 应用、版本、后端 host、账号脱敏摘要：`com.mredrock.cyxbs.test` / `6.10.6-alpha (94)` / dev/test / `2020****88`。
- 初始状态：当前正式库 1 个分类、1 条 `UNSCHEDULED` 日程、0 个单次调整；首次修复前 pending=2，成功 Sync 后 pending=0。旧开发数据库已经停用，当前 `schedule.db` 的文件名、表名和代码符号均不再携带版本代号。
- 自动化基线：客户端 desktopTest、Android host test、Android/iOS 编译通过；后端 Schedule 聚焦测试通过。

| ID | 用例 | 状态 | 现象与证据 | 根因 | 修复提交 |
|---|---|---|---|---|---|
| SCHED-E2E-001 | E02/E05/S25 | 已修复 | 覆盖安装后正式清单页误报只读，旧库存在但新结构无法初始化 | 未上线期间重写 Room schema 却继续使用 version=1，Room 不会触发 destructive migration，而是直接 identity 校验失败 | `c0f34cf86` |
| SCHED-E2E-002 | E05/S14 | 已修复 | 服务端成功创建分类与日程，但客户端未落库成功并保留 pending | 无失败项时仍提前构建全部失败快照，且分类解析器写死为 `{ null }`，带分类日程必然抛错 | `c0f34cf86` |
| SCHED-E2E-003 | T02/T03 | 已修复 | 无日期草稿进入时间设置后默认高亮“时间段”，再次点击却仍无法保存 | UI 预选了时间段模式，但领域状态仍为 `UNSCHEDULED`；同模式点击被直接忽略，没有把默认时分写入草稿 | `42db235f6` |
| SCHED-E2E-004 | C16/U01/U06 | 已修复 | 日程改到已同步分类后，本地保存为 pending，构造 UPDATE 时崩溃；重启后的 Sync 才能补传 | 日常请求只把本次有 pending 的分类交给序列化器，因而无法把无 pending 分类的本地 UUID 解析为远端 ID | `42db235f6` |
| SCHED-E2E-005 | R09/R10/R12/R24 | 已修复 | 单次修改、删除及还原请求实际成功，但原诊断日志把 adjustment 请求和响应都显示为 `EMPTY` | 日志只覆盖 Schedule 列表，遗漏 occurrence adjustment 四组列表；已补只含 identity、版本、状态和 Patch 模式的脱敏摘要 | `15a5fb0c3` |
| SCHED-E2E-006 | E06/L06/N08/Q09 | 已修复 | 冷启动正式清单后真实 Sync 已成功且数据可见，但页面持续显示“当前没有可编辑的登录账号”，新增和编辑入口被关闭 | façade 的普通 `mutationMode` getter 依赖 `initializationCompleted`；初始化完成只改变内部布尔值且未产生新快照，Compose 没有重组。现在精确账号 delegate 绑定后立即公开 local-first，真正命令仍在仓库边界等待初始化 | `2300f789b` |
| SCHED-E2E-007 | R06/U08 | 已修复 | 每日重复日程完成 9 月 3 日实例后，未完成区直接展示 9 月 4 日，但已完成区没有 9 月 3 日，用户无法取消本次完成 | 清单投影对每个系列只保留一张卡片并优先选择 `ACTIVE`；现在未完成区仍只取下一实例，已完成区独立展示七天内真实完成实例 | 本提交 |
| SCHED-E2E-008 | C09/C19 | 已修复 | 父日程解绑分类后，仍有单次调整引用该分类，但管理页显示“0 项日程”并错误开放删除确认入口 | 管理页只统计父 Schedule 的分类字段，遗漏单次调整的分类 Patch；现在合并两种引用并按 Schedule ID 去重，与仓库删除边界一致 | 本提交 |

### 14.2 真机验收证据

- T04：创建 `E2E-SCHEDULE-0903-DAILY`，设置每日重复、时间点、准时提醒并关联课表；CREATE 返回 `SUCCESS/version=1`，本地 pending 归零。系统日历对应行使用 `duration=PT0M`、`RRULE=FREQ=DAILY`，Reminder 为 `minutes=0`。
- T05：从课表投影打开同一日程，把整个系列改为提前 10 分钟；UPDATE 返回 `SUCCESS/version=2`，列表与详情均显示“提前10分钟（提醒）”，系统日历同一 Event ID 的 Reminder 更新为 `minutes=10`。
- R09/R10/R28：9 月 7 日“仅删除此次”后，服务端 adjustment 变为 `CANCELLED/version=3`，所有内容 Patch 均为 `INHERIT`，本周课表仅剩 9 月 8～13 日六项；重复设置展示“9月7日 13:49 → 已删除”。点击还原但未保存时无网络请求且课表不变，最终确认后发送 adjustment DELETE，服务端返回 `SUCCESS`，本周七项恢复。
- R12/R22/R24/R29：9 月 7 日首次仅改标题时请求携带本地 UUID、服务端返回自增 ID=2/version=1，其他 Patch 均为 `INHERIT`；再次修改沿用 ID=2 并递增到 version=2。还原后重新修改生成新的本地 UUID 和远端 ID，内容从父系列重新继承，未复活旧标题 Patch。
- E06/N08/Q12：`AccountSwitchingScheduleRepositoryTest` 覆盖登录、切号、登出、初始化失败和迟到快照/日历事件隔离；Room 四类状态以 `account_id` 为联合主键前缀，Android/iOS 日历设置使用账号级 Settings。后端三张表均保存 `owner_id`，DAO 所有查询、更新和删除都带 owner 条件；服务编排测试固定三类增删改和读取只转发同一个认证 owner。
- L06/Q09：时间轴模式下覆盖安装并冷启动 `cyxbs://schedule`，页面仍显示“切换到清单列表”且使用原有 Room 数据；安装脚本构建、覆盖安装和正式入口启动均成功。修复后再做一次真正冷启动，错误只读提示未出现。
- T08：打开迁移得到的 `UNSCHEDULED` 日程 `222`，分别点击提醒和关联课表；界面依次提示“请先设置时间后再开启提醒”“请先设置时间后再关联课表”，期间没有本地持久化或网络更新日志。
- T09：真机输入纯空格标题并点击保存，编辑器保持打开且无本地/网络写入。后端 `TestScheduleMutationRejectsBlankTitleOnly` 进一步固定绕过客户端时当前项返回 `REJECTED / INVALID_REQUEST`，同请求有效日程仍成功。
- T10/T11：空白新建弹窗点击外部会直接关闭；输入标题后按返回会立即显示未保存确认，点击“继续编辑”后标题仍在，再次返回并“放弃”后关闭且没有写入日程。
- T20：新建日程只填写标题 `E2E-SCHEDULE-0903-TITLEONLY` 后点击保存，编辑器继续显示“未设置日期 / 未设置时间”，本地和网络日志均无 CREATE/UPDATE；随后通过未保存确认放弃草稿。
- U04：同一远端每日日程依次保存为“不提醒”“准时”“提前10分钟”，三次整系列 UPDATE 均成功、版本从 2 递增至 5、pending 均归零，最终恢复原测试值。
- U05：关闭课表关联后版本从 5 递增至 6，当前账号课表不再包含该日程；重新开启后版本递增至 7，课表重新生成每日投影，清单本体和其他字段保持不变。
- U10：删除 `E2E-SCHEDULE-0903-POINTx` 前出现“删除后将无法恢复”的二次确认；确认后本地先进入 `PendingDelete`，DELETE 返回 `SUCCESS` 且 pending 归零。随后冷启动 Sync 不再 confirmed 或 discovered 该 ID，确认远端物理删除且不会复活。
- U01：把非重复时间段日程改为 `E2E-SCHEDULE-0903-FIELDS` 并写入测试备注后，UPDATE 成功且版本 3→4。Room 中仅 `title.modifiedAt`、`description.modifiedAt` 更新，分类、时间、重复、提醒和课表关联时间戳保持原值；此前 U06 的分类变更也只更新分类字段。
- U13：在同一日程上清空备注后版本 4→5，Room 保存明确空字符串且只有 `description.modifiedAt` 再次更新；结合 U06 清空分类和 U04 清空提醒的独立请求，均未误清其他 AtomicField。
- U02/U15：同一日程按 `TIMED 12:23–13:23 → DEADLINE 13:23 → ALL_DAY → TIMED 14:49–15:49` 逐步保存，版本从 5 递增至 8；四次快照始终保持 9 月 3 日，日志中的 timing 每次只有当前类型字段，未残留上一类型的钟点或区间语义。
- U08：普通 TODO 完成后版本 8→9，立即从“未完成”移到“已完成”，对应课表投影消失；取消完成后版本 9→10 并回到未完成。`ScheduleTodoUiStateTest` 同时覆盖恰好七天仍展示、七天零一分钟即隐藏的边界。
- U09：完成 `E2E-SCHEDULE-0903-FIELDS` 后进入详情编辑并追加标题后缀，UPDATE 成功且详情仍提供“恢复未完成”而非“标记完成”；随后单独取消完成，确认标题修改与完成状态字段互不覆盖。
- R06：完成 `E2E-SCHEDULE-0903-DAILY` 的 9 月 3 日实例后，未完成区同时展示 9 月 4 日下一实例，已完成区保留 9 月 3 日并提供恢复入口。点击恢复发送同一 adjustment ID=4、version=1 的 `ACTIVE` 更新，服务端返回 version=2，本地 pending 归零；界面回到 9 月 3 日未完成且不影响原有 9 月 7 日完成实例。
- C01/C07：新建 `E2E-CATEGORY-0903` 并选用珊瑚配色，服务端分配自增 ID=4，本地保留 UUID 映射且 pending 归零；改名为 `E2E-CATEGORY-0903-RENAMED` 并换为电紫配色后版本 1→2。拖动到“其他”之前会同步四项最终顺序，冷启动和 Sync 后仍保持“学习、生活、测试分类、其他”。
- C03/C09：再次输入同名分类时界面显示“已存在同名分组”且保存按钮禁用；测试分类被一条日程引用时点击删除只提示“仍有 1 项日程使用该分组”，两次拦截均无本地或网络写入。服务端合并与 `CATEGORY_IN_USE` 兜底仍待自动化/直连验证，故对应整项暂不勾选。
- C08：把引用日程改回未分组后，分类数量即时降为 0；确认删除会先写入 `PendingDelete`，DELETE 成功后 pending 归零，随后冷启动 Sync 未再恢复该分类。
- E08：从分组管理页连续两次投递 `cyxbs://schedule?scheduleId=<UUID>` 定位同一日程，第二次由当前单例页面消费定位请求；随后只按一次返回键即回到分组管理页，确认导航栈未重复压入清单页。`ScheduleId` 是内联值类，深链直接传规范 UUID，不额外套 JSON 对象。
- T12：真机分别创建 `E2E-SCHEDULE-0903-BOUNDARY-0000` 与 `E2E-SCHEDULE-0903-BOUNDARY-2359`，CREATE 请求、成功回包和 Room 均保持同日 `DEADLINE 00:00`、`DEADLINE 23:59`，列表摘要也未跨日。
- T13/U17：真机逐格操作四个时间滚轮，`E2E-SCHEDULE-0903-RANGE-0000-0030` 成功保存为同日 `TIMED 00:00..00:30`。另一条草稿只把开始小时从 15 调至 23，编辑器自动收敛为 `23:29–23:59` 且结束滚轮未锁死；最终 CREATE、回包和 Room 均保存该同日区间。`ScheduleEditNoOpTest.intervalAdjustmentHandlesDayBoundary` 同时固定起止两端的边界算法。
- T14/U16：在已保存的 `14:49–15:49` 时间段中只向后调整开始小时，结果成为 `16:49–17:19`，原结束端自动补足到 30 分钟；再只向前调整结束小时，结果成为 `15:49–16:19`，结束值保持而开始端自动前移。放弃编辑后列表仍为原区间且没有网络写入；聚焦测试覆盖开始分钟、结束端和小时优先保留规则。
- T16/T22/T26：连续创建两条同名 `E2E-SCHEDULE-0903-DUPLICATE`，第一条为单日 `ALL_DAY 2026-09-03`，第二条为 `DEADLINE 2026-09-03T16:41`；服务端分别接受两个不同的规范 UUID v7，回包版本均为 1，Room 同时保留两条且 pending=0。全天项的 wire 只有单个日期，没有跨天结束日期字段。
- T17：新建 `E2E-SCHEDULE-0903-DOUBLE-SAVE` 时对保存区域连续发送两次点击，日志只出现一次本地 `Create`、一次 CREATE 请求和一个 UUID；成功回包后版本为 1、pending=0，没有生成重复日程。
- C04：新建分类 `E2E-CATEGORY-0903-REFERENCE` 后，先让普通日程与每日重复日程 9 月 3 日的单次调整共同引用远端分类 ID=7；随后把分类改名为 `E2E-CATEGORY-0903-REF-RENAMED` 并从天蓝改为珊瑚配色，分类版本 1→2。Room 复核显示普通日程仍引用 ID=7，单次调整仍为同一远端 adjustment ID=4/version=3 且其分类 Patch 仍指向 ID=7，三类记录均无 pending。
- C09/C19（部分）：普通日程解绑 ID=7 后，父 Schedule 已变为未分组，9 月 3 日单次调整仍单独引用该分类。修复前管理页即时错误降为“0 项日程”；覆盖安装修复包后恢复为“1 项日程”，点击删除只执行 UI 拦截，没有弹删除确认，也没有产生本地或网络删除命令。服务端 `CATEGORY_IN_USE` 直连兜底及解绑/删除日程后的完整数量变化仍待后续验证。
- R03/R04/R05/R20：运行 `RecurrenceEngineTest`、`RecurrenceEditModelTest` 与 `ScheduleEditNoOpTest` 聚焦测试通过。断言覆盖月重复 31 日在二月/四月等无效日期跳过、2 月 29 日只在闰年生成、全天/时间点/时间段周重复均保留 timing 类型并生成互异的稳定 occurrence identity，以及周选择集合从周一/周三/周五替换为周二/周四后只生成新集合、无旧实例残留。
- C02/T24/S04/S05/S16：客户端 `SchedulePlannerApplierTest`、`ScheduleDailyMutationBridgeTest` 与后端 `TestScheduleMutationResolvesNewCategoryLocalID`、`TestScheduleMutationRejectsOnlyDependentResource` 聚焦测试通过。同请求先用瞬时 `categoryLocalId` 解析服务端分类 ID，临时 ID 不进入存储；分类失败仅拒绝依赖日程，其他日程继续成功；客户端逐位置应用结果并只保留拒绝项 pending。
- U11：`SchedulePlannerApplierTest.acceptedRequestDoesNotClearNewerLocalChange` 固定 R→U 场景：请求捕获 revision=4 后本地产生 revision=5，旧成功回包只更新远端快照，revision=5 与最新内容继续保留为 pending。
- S03/N01：新增 `SchedulePlannerApplierTest.discoveredResourcesRestoreEmptyLocalStateAndReferences`，以空本地 capture 同时接收远端分类、日程和单次调整，验证分类与调整生成本地 UUID、日程保留稳定 UUID、两类分类引用均映射到同一本地 identity，且三类资源均无虚假 pending。
- S19/N05：新增 `SchedulePlannerApplierTest.confirmedDeletedResourceWinsOverCapturedPendingUpdate`，验证请求捕获时即使存在本地更新，confirmed 与 upsert 同时返回 `DELETED` 后仍物理移除该日程，不把旧 pending 改写为新建而复活远端数据。
- M02～M07/M12～M19：完整运行 `LegacyScheduleMapperTest` 与旧接口 DTO 测试。覆盖无时间/截止/提醒矩阵，完成态、固定分类和置顶，日周月年规则及安全降级，Transaction 全学期/指定周/多位置拆分，月末、年重复、非法标题/周次/星期/节次逐项跳过。
- M09/M10/M13/M14/M21：`LegacyScheduleMigrationPersistenceTest` 验证确定性 UUID v5 在同批重复、重试及快照已存在时均只保留一条；默认分类按固定 identity 或去空白同名复用，缺失分类同批只创建一次；迁移置顶追加到 Settings 既有顺序且按首次出现去重。
- M11/M22：Android 与 iOS 的 `ScheduleCalendarExportInitializer` 均只调用 commonMain `LegacyScheduleMigrationCoordinator.start`；`LegacyScheduleMigrationWindowTest` 验证 2028-09-01T00:00:00Z 起窗口关闭，旧接口请求前和等待学期锚点后均再次门禁。
- K04/K08/K11～K13/K15：完整运行 `domain.calendar` 聚焦测试。`CalendarProviderTimingCanonicalizerTest` 固定时间点 `PT0M`、时间段分钟精度和全天整日边界；`ScheduleCalendarProjectionTest` 验证无时间及完成普通 TODO 不生成投影、重复实例完成/取消只生成对应 occurrence 的取消操作、全天始终投影为单日；`CalendarExportPlannerTest` 验证旧格式 Provider 行触发显式重建预检，避免混合增删改半执行后永久卡住。
- S02/S06/S09：`TestScheduleSyncAlignsConfirmedDiscoveredAndMutationResults` 固定服务端 confirmed 的 `CHANGED + resource`、`DELETED` 与 discovered 分流；新增 `confirmedChangedReplacesRemoteSnapshotWithoutPending`、`alreadyDeletedResultCompletesLocalDelete` 和 `mismatchedSuccessfulUpsertIdentityFailsClosed`，验证客户端更新远端快照、删除远端已不存在资源，并在成功资源身份错位时整次拒绝应用。原有数量错位测试继续覆盖结果数组缺项。
- S08/S23：运行 `ScheduleNetworkRecoverySyncTriggerTest` 聚焦测试通过。断言首次进入会执行一次 Sync；网络变化在没有 pending 时不触发额外请求，而本地存在 pending 时仅在网络重新可用后触发 Sync，不生成多余的日常 mutation。
- R08/R13/R14/R19/R23/R31/U14：复跑 `ScheduleEditNoOpTest` 与 `ScheduleLocalCommandReducerTest` 聚焦测试通过。断言单次 date/time Patch 相互独立，系列字段更新不覆盖完成态或显式单次时间，`originalOccurrenceDate` 始终保持原槽；有限系列最后实例删除父资源，跨周/月/年改日期也不重置其他字段。
- C03/C09：使用测试包凭证直连 dev/test。以 version=0 和新的 localId 再次创建 `E2E-CATEGORY-0903-REF-RENAMED`，服务端返回既有 canonical ID=7/version=2，且较旧字段时间未覆盖现值；随后创建专用分类 ID=9 并绑定 E2E 日程，删除请求逐项返回 `REJECTED / CATEGORY_IN_USE` 与当前分类快照。解除引用后同一分类删除返回 `SUCCESS`，专用数据已清理。
- C05/C18/S17：在 dev/test 创建专用分类 A/B 后，把 B 的名称和配色同时更新为“名称=A、配色=新值”。服务端返回 `REJECTED / DUPLICATE_CATEGORY_NAME`，并回传 B 的 version=1、原名和原配色，确认整条分类更新未产生半写入；两个专用分类随后均物理删除成功。
- S13/S20/S24：dev/test 的 CREATE、UPDATE、DELETE、SYNC 实测均返回 HTTP 200 和统一 `data/status/info`；业务拒绝留在逐项结果。目标日程临时绑定/解绑分类时 canonical version 按 1→2→3 递增，较旧字段时间未覆盖新值；再次删除已物理清理的分类 ID=9 仍返回 `SUCCESS`，请求只携带 ID、不携带版本。客户端冷启动后通过 `CHANGED/version=3` 收敛，pending=0。
- R11/S18：向 dev/test 同一请求提交两条 `scheduleId + originalOccurrenceDate=2026-09-04` 相同、localId 不同的 version=0 adjustment。两项均返回 canonical ID=5，版本依次为 1、2，较新的标题与描述 Patch 按字段合并保留且响应不含 localId；客户端双 ID 绑定由 `SchedulePlannerApplierTest` 覆盖。专用 adjustment 随后物理删除成功。
- L12/L13/L14：真机列表同时核对时间点 `13:49`、时间段 `00:00–00:30`/`23:29–23:59`、单日全天和旧 `UNSCHEDULED` 的摘要；提前 10 分钟提醒与课表关联图标同排展示，时间段未退化为只显示开始时间。`ScheduleTodoUiStateTest` 进一步固定已完成普通清单恰好七天仍展示、七天零一分钟后隐藏的边界。
- L09/L19/L20：真机主页 Feed 的 E2E 卡片左滑正常露出“置顶/删除”并可右滑复位，完成圆圈、时间、提醒和课表关联状态与清单页一致。滚回顶部后横幅正确显示 9 项临期/超期及设计箭头，点击进入正式清单页，并保持端上记录的时间轴视图；无待办空文案由 `ScheduleFeedUiStateTest.completedOnlySnapshotIsEmpty` 固定。
- R01：完整 `desktopTest` 中 `RecurrenceEditModelTest` 逐一断言 `Never`、`Count(1)`、`Count(6)` 与 `Until` 的编辑模型映射，`RecurrenceEngineTest.dailyWeeklyMonthlyYearlyAndCount` 进一步验证每日重复按 count 展开，避免把“重复一次”误判为关闭重复。
- U19：真机删除每日重复实例时，“仅删除此次”的二次确认明确提示可在重复设置中恢复；展开单次调整后确实显示“还原此次调整”。“删除整个系列”的二次确认明确提示无法恢复；两种弹窗均已取消或按对应路径完成，不存在文案与行为倒置。
- U22：真机打开重复日程后进入编辑、未改动保存，弹窗回到同一日程详情而未关闭；`ScheduleEditNoOpTest.affairCreationReturnsCreatedScheduleForDetailTransition` 同时固定新事务保存返回已落库的同一 Schedule，供创建弹窗原位切换详情。
- K05/K06/K09：在测试设备运行 `persistentAndroidDeviceTest`，15 项全部通过。真实 Provider 测试确认受管 Calendar row 名称/显示名均为“掌邮日程”，使用随机账号、LOCAL 类型、owner 与 ownership token 的严格联合身份；创建、更新、删除及重新创建均只命中该身份，owner、URI、row incarnation 漂移会安全拒绝。结合 `CalendarExportPlannerTest.newProjectionProducesCreate` 与协调器初始全量对账，Provider 中缺失的受管事件会重新计划创建，同名非受管行不会被当作当前账号资源覆盖或删除。
- N04：`LegacyScheduleMapperTest.deterministicUuid_isStableUuidV5AndSeparatesResources`、`LegacyScheduleMigrationPersistenceTest` 的已存在/同批重复/远端失败重试用例，以及 `UuidV7GeneratorTest` 在完整桌面测试中通过。旧源 identity 始终映射相同 UUID v5 并由本地/远端快照幂等复用；普通创建使用规范 UUID v7，服务端响应不改写 Schedule identity。
- Q03：运行 `:cyxbs-pages:course:view:desktopTest` 通过；Schedule 完整 `desktopTest` 同时覆盖课表可见性与投影服务，课表 overlap、PageDecoration 及 Schedule service 的当前聚焦回归均无失败。
- U12：`ScheduleRoomRepositoryDesktopTest` 新增两条仓库级回归。被业务拒绝的新建在用户修正内容并重试成功后，会移除同一日程的旧失败记录且保留修正后的内容；用户删除该本地日程时，同样立即清除失败记录，并向远端发送不带版本的幂等 DELETE，以收敛“服务端可能已成功但响应丢失”的不确定状态。聚焦测试全部通过。
- R15/R32：`ScheduleSnapshotProjectorTest.recurrenceMembershipControlsVisibleAdjustments` 依次投影“每周仅周五”和恢复后的每日规则。同一批单次调整中，周六原槽在前者休眠并同时从业务快照及可还原列表数据源消失，规则恢复命中后同一原槽重新出现，期间没有删除或改写 adjustment。
- R16/R33：`ScheduleRoomRepositoryDesktopTest.disablingAndReenablingRecurrenceDoesNotRestoreDeletedAdjustments` 从已同步父系列和 adjustment 出发，关闭重复后验证服务端成功回包会把子记录从 Room 物理移除；随后给同一日程重新开启原规则，本地快照和 Room 均保持无 adjustment，不会复活旧单次状态或 Patch。
- U20：`SchedulePlannerApplierTest.alreadyDeletedResultCompletesLocalDelete` 固定服务端已不存在时 DELETE 仍按 `SUCCESS` 收敛；`ScheduleRoomRepositoryDesktopTest.deletingConfirmedScheduleAfterRejectedUpdateClearsLocalFailureState` 再从有远端版本且更新被拒绝的状态出发，验证幂等删除成功后 Schedule、pending 与关联失败记录同时从 Room/业务快照清除。
- U21：扩充 `SchedulePlannerApplierTest.acceptedRequestDoesNotClearNewerLocalChange`。revision=4 的旧请求返回成功时，revision=5 的本地修改仍作为 effective pending；下一轮 capture 只上传最终值 `U`，并以刚确认的远端 version=4 为基线，不重放已被后续编辑覆盖的 `R`。
- R30：修复完成态与内容 Patch 未完全正交的问题。`ScheduleEditNoOpTest.restoreOccurrenceAdjustmentPreservesIndependentCompletionState` 验证已完成实例还原单次时间/标题等内容时只清空 Patch、继续保持 COMPLETED；随后取消完成会物理删除已无业务含义的 adjustment，因此旧 Patch 不会复活。ACTIVE 修改和 CANCELLED 删除的还原仍直接物理删除资源。

### 14.3 修复批次规则

- 同一链路的 2～5 个小问题可合为一个提交；崩溃、数据丢失、错误删除等高风险问题发现后立即单独修复。
- 每个修复先补最小可复现自动化测试，再执行失败用例和相邻用例。
- Commit 正文记录问题编号、行为变化、兼容边界和实际验证命令。
- 每批提交后复核两个仓库状态；测试注入、token、数据库导出和临时日志不得进入 Git。

## 15. 最终清理与验收

- [ ] Z01 删除本轮 `E2E-SCHEDULE-0903-` 分类、Schedule 和 adjustment，不触碰其他测试数据。
- [ ] Z02 确认本地 pending=0、失败记录无本轮残留、后端 Sync 不再下发本轮测试资源。
- [ ] Z03 恢复迁移版本、网络故障注入、权限和系统日历测试环境，并覆盖安装正式测试构建。
- [ ] Z04 汇总通过、失败、跳过数量、所有修复提交和仍需用户手动确认的项目。
