package com.mredrock.cyxbs.lib.utils.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.jvm.Throws

/**
 * [ApiWrapper] 里面封装了 [data]、[status]、[info] 字段，是为了统一网络请求数据的最外层结构，
 * 如果你遇到了 json 报错，可能是你数据类写错了，只需要提供 [data] 对应的类即可
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 23:06
 */
open class ApiWrapper<T> (
  @SerializedName("data")
  val data: T,
) : Serializable, ApiStatus()

/**
 * 没有 data 字段的接口数据包裹类
 *
 * 该类符合后端的接口规范，最外层字段值包含 [status] 和 [info]
 *
 * 禁止私自添加其他字段
 * - 如果需要添加且不是老接口，那说明是后端没有遵守规范，让后端自己改接口
 * - 如果是老接口，请使用
 */
open class ApiStatus(
  @SerializedName("errorCode")
  val status: Int = -1,
  @SerializedName("errorMsg")
  val info: String = ""
) : Serializable {
  
  /**
   * 数据的状态码
   *
   * 与后端规定了 10000 为 数据请求成功
   *
   * 注意区分：数据 Http 状态码 与 数据状态码
   */
  fun isSuccess(): Boolean {
    return status == 10000 // 请不要私自加其他的成功状态！！！
  }
  
  @Throws(ApiException::class)
  fun throwApiExceptionIfFail() {
    if (!isSuccess()) throw ApiException(status, info)
  }
}
