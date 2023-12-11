package com.mredrock.cyxbs.lib.utils.logger.event

/**
 * @author : why
 * @time   : 2023/12/4 18:03
 * @bless  : God bless my code
 */

/**
 * 目前存在的点击类型的埋点
 *
 * - 命名都是直接根据数据中台创建的埋点name改的（懒得想了，新增的时候也可以自己想命名）
 *
 * - 【id】 和 【hash】 据后端说都是唯一，且一一对应，注意埋点平台那边修改的时候需要端上同步修改或添加
 *
 * - 目前暂定统一写进utils模块统一管理，如果后面实在太多可以考虑下沉到各业务模块？
 */
enum class ClickEvent(id: String, hash: String) {

  // 统计“邮乐场“功能的日点击量
  CLICK_YLC_ENTRY("28", "1a3af5f0cc7154fe92154701954f4199eb4019e4f2bfd16a9c5e4207e73691f2"),

  // 统计“课表查询”功能的日点击量
  CLICK_KBCX_ENTRY("30", "95e902e20c9b23072eecf7cc77c698e20643964e0da0dd06feb0e571926dd98b"),

  // 统计“重邮地图”功能的日点击量
  CLICK_CYDT_ENTRY("31", "2ce43ed507664e6b892e2dbb40eac456fe3df7cc29b482ee6c11d5f5c158fcac"),

  // 统计“没课约”功能的日点击量
  CLICK_MKY_ENTRY("32", "c93b7636c34d274d4ba75169830681ba023c4e75bc1f4336de77d51bcb6ed3ff"),

  // 统计“校车轨迹”功能的日点击量
  CLICK_XCGJ_ENTRY("33", "373983c61c065e36afcc30cce7c48fef91fc020cf41da41b365f2c5fc282cd4a"),

  // 统计“邮子清单”功能的日点击量
  CLICK_YZQD_ENTRY("34", "544b4258322f1b9ce943aae9d2f3d463e5b52a863fc680f71964ba3fef6c122f"),

  // 统计“校历”功能的日点击量
  CLICK_YLC_XL_ENTRY("35", "b23867c586832f5c6e2cfba8b1476f25c0d2cbabe40b5eaccfb71e00da5e2112"),

  // 统计“体育打卡”功能的日点击量
  CLICK_YLC_TYDK_ENTRY("36", "7ff806a4eceba54d008a7dff8db283576ad7ecc1beec58be4d10842606e24c79"),

  // 统计“空教室”功能的日点击量
  CLICK_YLC_KJS_ENTRY("37", "8dadbaad8372126e53b7ffc2092cefad87e5ac4583bb090d11a5844b90c96ea8"),

  // 统计“我的考试”功能的日点击量
  CLICK_YLC_WDKS_ENTRY("38", "c6f0b609f352089f59cd16a7dd100bd944db1fc4188d693aca71062b92a577a9"),

  // 统计“消息中心”功能的日点击量
  CLICK_YLC_XXZX_ENTRY("39", "a17c8a39636fb030a3d547cbf7d254f84d72521c2a59338b4e468b5a1cdde2b9"),

  // 统计“邮票中心”功能的日点击量
  CLICK_YLC_YPZX_ENTRY("40", "d226ce1f50d8aba0074ea9f68461f54f36a20064d28e6dfbeca032f16156048a"),

  // 统计“反馈中心”功能的日点击量
  CLICK_YLC_FKZX_ENTRY("41", "c35752bb700b427cba31f58eb75ee8c5b4fb7152f832f8a115297b891c07ef79"),

  // 统计“活动中心”功能的日点击量
  CLICK_YLC_HDZX_ENTRY("42", "70583e03de9f10cb5a45aad8ef3d2f0b030b7bad9dfccef779641980ddee3f5a"),

  // 统计banner位的日点击量
  CLICK_YLC_BANNER_ENTRY("43", "9146c8d71a83c24c0ff6c056c7549f78b5be815596fb35746c952ae52275fe16");

  // 转换成 map 的参数用于表单请求
  val mapParams: Map<String, String> = mapOf(
    "id" to id,
    "hash" to hash
  )
}