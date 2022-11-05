package com.mredrock.cyxbs.lib.utils.network

import com.mredrock.cyxbs.lib.utils.extensions.interceptException
import kotlinx.coroutines.flow.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/28 15:16
 */

/**
 * # 网络请求示例代码
 * 抓取 http 中的报错，并装换为直接包含 data 的 Flow
 * 建议配合 BaseViewModel 中 collectLaunch 一起食用
 * ```
 * flow {
 *     emit(FindApiServices.INSTANCE.getStudents(stu))
 * }.onStart {                  // 开始
 *     //...
 * }.onCompletion {             // 结束
 *     //...
 * }.mapOrInterceptException {  // 当 status 的值不为成功时或有其他网络异常时
 *
 *     toast("捕捉全部异常")      // 直接写的话可以处理全部异常
 *
 *     emitter.emit(...)        // 手动发送新的值给下游
 *
 *     ApiException {
 *         // 处理全部 ApiException 错误
 *     }.catchOther {
 *         // 处理非 ApiException 的其他异常
 *     }
 *
 *     ApiExceptionExcept {
 *         // 这样写可以直接处理  ApiException 的其他异常
 *     }
 *
 *     ApiException(10010) {
 *         // 单独处理 status 为 10010 时的 ApiException
 *     }
 *
 *     HttpExceptionData<XXXBean>(500) {
 *         // 处理 HttpException 并反序列化 json 为 XXXBean
 *
 *         emit(it) // 也可以重新发送 XXXBean 给下游，但注意 XXXBean 需要与数据流中的泛型一致才能发送
 *     }
 *
 *     HttpExceptionBody(500) {
 *         // 处理 HttpException，但只获取 errorBody
 *     }
 *
 * }.collectLaunch {            // 收集，注意：collectLaunch() 是专门封装了的一个方法
 *     _studentData.emit(it)
 * }
 * ```
 *
 * ## 网络请求怎么直接返回 Flow ?
 *
 * 由于目前 Retrofit 官方没有直接给出 Flow 的 adapter，如果有必要使用 Flow 的话，
 * 可以暂时使用 Observable 来转成 Flow (Flow 很多 api 处于测试阶段，不是很推荐)
 *
 * **需要先引入依赖：dependCoroutinesRx3()**
 * ```
 * FindApiServices.INSTANCE.getStudents(stu)
 *     .subscribeOn(Schedulers.io()) // 线程切换（必要）
 *     .asFlow()                     // 转换成 Flow
 * ```
 */

/**
 * 转换为 data 并使用 DSL 写法来处理异常
 *
 * # 详细用法请查看该文件上面的注释
 * # 更多注意事项可以查看 [Flow.interceptException]
 *
 * - [IApiWrapper] 推荐使用 [mapOrInterceptException] 方法
 * - [IApiStatus] 使用 [throwOrInterceptException] 方法
 */
fun <Data, T : IApiWrapper<Data>> Flow<T>.mapOrInterceptException(
  action: suspend ApiExceptionResult<FlowCollector<Data>>.(Throwable) -> Unit
): Flow<Data> {
  return mapOrThrowApiException()
    .catch {
      ApiExceptionResult(it, this).action(it)
    }
}

fun <T : IApiStatus> Flow<T>.throwOrInterceptException(
  action: suspend ApiExceptionResult<FlowCollector<T>>.(Throwable) -> Unit
): Flow<T> {
  return throwApiExceptionIfFail()
    .catch {
      ApiExceptionResult(it, this).action(it)
    }
}




/**
 * 装换为 [Data] 并检查数据中的状态码是否有问题，有就抛出异常
 *
 * 注意：数据中的状态码是 status 字段，不是 Http 状态码，这两个状态码不要混淆了
 */
fun <Data, T : IApiWrapper<Data>> Flow<T>.mapOrThrowApiException(): Flow<Data> {
  return throwApiExceptionIfFail()
    .map {
      it.data
    }
}

fun <T : IApiStatus> Flow<T>.throwApiExceptionIfFail(): Flow<T> {
  return onEach {
    it.throwApiExceptionIfFail()
  }
}
