package com.mredrock.cyxbs.lib.utils.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mredrock.cyxbs.lib.utils.extensions.ExceptionResult
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import kotlin.reflect.KClass

/**
 *
 * 正常请求后的网络异常
 *
 * ## 一、什么情况下是正常请求？
 * 只有当 Http 状态码为 200 到 299 之间时才能被认为是正常请求，其他状态码会被 Retrofit 之间拦截并抛出 [HttpException]
 *
 * ## 二、什么是 Http 状态码？和 status 这个状态码有什么区别？
 * Http 状态码是公开的规范，各个公司都遵守，返回 200 时表示本次请求成功，
 * 但 status 这个状态码用于判断本次请求的数据是否成功，属于自定义的一种状态码
 *
 * ## 三、有个接口 Http 状态码不返回 200，后端也不去改，该怎么办？
 * 这种情况属于后端的锅，无论数据是否成功，只要与后端服务器连接成功都应该返回 Http 状态码 200（除去部分服务器的锅返回 4xx），
 * 如果返回其他的会导致 Retrofit 直接拦截然后抛出错误
 *
 * 如果你遇到这种情况，首先让后端去改
 *
 * ### 1、在多次交谈无果后，万不得已的情况下，可以采用下面这种写法获取
 * ```
 * .doOnError {
 *     if (it is HttpException) {
 *         val response = it.response()
 *         try {
 *             if (response?.code() == 500) { // 这个不一定是 500，看自己的接口会返回什么
 *                 val data = Gson().fromJson(response.errorBody().toString(), XXXBean::class.java)
 *                 // 然后你就可以对这个 data 进行操作了
 *             }
 *         } catch (e: JsonSyntaxException) {
 *             // Gson 出错
 *         }
 *     }
 * }
 * ```
 * 目前该逻辑已封装进 Rxjava 和 Flow
 * ```
 * .mapOrThrowApiException {
 *     HttpException<XXXBean>(500) {
 *         it.xxx
 *
 *         emit(it) // 也可以重新发送给下游，但注意这个 it 需要与外面的泛型一致才能发送
 *         // Flow 使用 emit()，Rxjava 使用 onSuccess()
 *     }
 * }
 * ```
 *
 * ### 2、一定要让后端先改！！！（有些老接口采用了上面这种写法，为了兼容，所以老接口就算了）
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 23:47
 */
class ApiException(
  val status: Int,
  val info: String
) : RuntimeException("status = $status   info = $info")

@Suppress("FunctionName")
open class ApiExceptionResult<Emitter>(
  throwable: Throwable,
  emitter: Emitter
) : ExceptionResult<Emitter>(throwable, emitter) {
  
  /**
   * 只抓取 [ApiException] 异常
   * @param status 单独处理 [status] 对应的情况。如果传入 -1，则处理全部 status
   */
  inline fun ApiException(status: Int = -1, action: Emitter.(ApiException) -> Unit): KClass<ApiException> {
    return ApiException::class.catch {
      if (status == -1) action.invoke(this, it)
      else if (it.status == status) action.invoke(this, it)
    }
  }
  
  /**
   * 抓取除了 [ApiException] 外的其他异常
   */
  inline fun ApiExceptionExcept(action: Emitter.(Throwable) -> Unit): KClass<ApiException> {
    return ApiException::class.catchOther(action)
  }
  
  /**
   * 如果是 [HttpException]，则反序列化 [Response.errorBody] 为 [T]
   *
   * ## 注意
   * -不是很推荐使用，因为这个情况一般是后端的问题，只要与服务器连接成功都该返回 2xx，
   * 所以请在与后端多次沟通无果后再使用！！！（部分老接口除外）
   * - 反序列化失败会吞异常
   *
   * @param httpCode 想要处理的 Http 状态码
   */
  inline fun <reified T : Any> HttpExceptionData(
    httpCode: Int,
    action: Emitter.(T) -> Unit
  ): KClass<HttpException> = HttpException::class.fromJson(T::class, httpCode, action)
  
  inline fun HttpExceptionBody(
    httpCode: Int,
    action: Emitter.(ResponseBody) -> Unit
  ): KClass<HttpException> = HttpException::class.errorBody(httpCode, action)
  
  /**
   * 用于后端异常返回，Http 状态码不为 2xx 时
   *
   * ## 注意
   * - 不是很推荐使用，因为这个情况一般是后端的问题，只要与服务器连接成功都该返回 2xx，
   * 所以请在与后端多次沟通无果后再使用！！！（部分老接口除外）
   * - 反序列化失败会吞异常
   *
   * @param httpCode 想要处理的 Http 状态码
   */
  inline fun <T : Any> KClass<HttpException>.fromJson(
    clazz: KClass<T>,
    httpCode: Int,
    action: Emitter.(T) -> Unit
  ): KClass<HttpException> {
    errorBody(httpCode) {
      try {
        action.invoke(emitter, Gson().fromJson(it.toString(), clazz.java))
      } catch (e : JsonSyntaxException) {
        e.printStackTrace()
      }
    }
    return this
  }
  
  inline fun KClass<HttpException>.errorBody(
    httpCode: Int,
    action: Emitter.(ResponseBody) -> Unit
  ): KClass<HttpException> {
    if (throwable is HttpException) {
      if (throwable.code() == httpCode) {
        val errorBody = throwable.response()?.errorBody()
        if (errorBody != null) {
          action.invoke(emitter, errorBody)
        }
      }
    }
    return this
  }
}