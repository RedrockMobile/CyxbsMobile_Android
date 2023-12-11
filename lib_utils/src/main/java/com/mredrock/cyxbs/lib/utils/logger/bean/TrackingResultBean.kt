package com.mredrock.cyxbs.lib.utils.logger.bean

/**
 * @author : why
 * @email  : why_wanghy@qq.com
 * @time   : 2023/12/5 19:16
 * @bless  : God bless my code
 */
enum class TrackingResultBean(val status: String, val msg: String) {
  // "success"
  SUCCESS("success", "成功"),
  // "ID wrong"
  ID_WRONG("ID wrong", "没有与请求的id参数相同的埋点"),
  // "hash wrong"
  HASH_WRONG("hash wrong", "请求对应的埋点的hash值不匹配")
}