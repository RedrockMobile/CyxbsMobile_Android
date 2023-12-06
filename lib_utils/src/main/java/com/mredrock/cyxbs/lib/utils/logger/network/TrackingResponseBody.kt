package com.mredrock.cyxbs.lib.utils.logger.network

/**
 * 埋点接口返回数据
 */
data class TrackingResponseBody(
  /**
   * 三种情况：
   * 1. `success` 成功
   * 2. `ID wrong` 没有与请求的id参数相同的埋点
   * 3. `hash wrong` 请求对应的埋点的hash值不匹配
   */
  val status: String
)