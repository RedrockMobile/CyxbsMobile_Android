package com.mredrock.cyxbs.lib.utils.network

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 23:47
 */
class ApiException(
  val status: Int,
  val info: String
) : RuntimeException("status = $status   info = $info")