package com.mredrock.cyxbs.lib.utils.network

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import java.io.Serializable
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.jvm.Throws

/**
 * [ApiWrapper] 里面封装了 [data]、[status]、[info] 字段，是为了统一网络请求数据的最外层结构
 *
 * ## 注意
 * - 如果你遇到了 json 报错，可能是你数据类写错了，只需要提供 [data] 对应的类即可
 * - 如果你的网络请求数据类有其他变量，请使用 [IApiWrapper] 这个接口
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 23:06
 */
data class ApiWrapper<T>(
  @SerializedName("data")
  override val data: T,
  @SerializedName("status")
  override val status: Int,
  @SerializedName("info")
  override val info: String
) : IApiWrapper<T>

/**
 * 没有 data 字段的接口数据包裹类
 *
 * 该类符合后端的接口规范，最外层字段值包含 [status] 和 [info]
 *
 * 禁止私自添加其他字段
 * - 如果需要添加且不是老接口，那说明是后端没有遵守规范，让后端自己改接口
 * - 如果是老接口，请自己使用 map 操作符判断
 */
data class ApiStatus(
  @SerializedName("status")
  override val status: Int,
  @SerializedName("info")
  override val info: String
) : IApiStatus

interface IApiWrapper<T> : IApiStatus {
  val data: T
}

interface IApiStatus : Serializable {
  val status: Int
  val info: String
  
  /**
   * 数据的状态码
   *
   * 与后端规定了 10000 为 数据请求成功
   *
   * 注意区分：数据 Http 状态码 与 数据状态码
   *
   * 对于 [status] 不为 10000 时，建议采用下面这种写法来处理
   * ```
   *
   * ```
   */
  fun isSuccess(): Boolean {
    // 10000 是 21 年后的新规定，200 是以前老接口的规定
    return status == 10000 || status == 200 // 请不要私自加其他的成功状态！！！
  }
  
  @Throws(ApiException::class)
  fun throwApiExceptionIfFail() {
    // 后端文档：https://redrock.feishu.cn/wiki/wikcnB9p6U45ZJZmxwTEu8QXvye
    if (status == 20003) {
      // token 过期
      if (checkTokenExpired.compareAndSet(false, true)) {
        userTokenService.tokenExpired()
      }
    } else if (status == 20004) {
      // refreshToken 过期
      if (checkRefreshTokenExpired.compareAndSet(false, true)) {
        userTokenService.refreshTokenExpired()
      }
    }
    if (!isSuccess()) throw ApiException(status, info)
  }
}

// 是否触发过一次 refreshToken 过期，设计成全局变量的原因在于下一次重新打开应用失效
private val checkRefreshTokenExpired = AtomicBoolean(false)
private var checkTokenExpired = AtomicBoolean(false)
private val userTokenService by lazy {
  ServiceManager.invoke(IAccountService::class).getUserTokenService()
}
