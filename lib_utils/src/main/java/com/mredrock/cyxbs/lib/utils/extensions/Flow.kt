package com.mredrock.cyxbs.lib.utils.extensions

import com.mredrock.cyxbs.lib.utils.network.ApiException
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.rx3.asFlow

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/30 10:19
 */

/**
 * 个人观点：
 *
 * 1、不是很建议大家大量使用 Flow，因为 Flow 目前不是很成熟，有很多 api 还处于实验阶段，而且对于复杂的流水处理比不上 Rxjava
 *
 * 2、目前 Flow 比 Rxjava 好的一点在于可以与生命周期相配合
 *
 * 3、更推荐在 Repository 使用 Rxjava，
 * - Room 层推荐 Rxjava (Single、Observable、Maybe)
 * - 网络层推荐使用 Rxjava 的 Single，(不知道为什么以前的学长很喜欢用 Observable)
 * - ViewModel 层可以 Flow 和 Rxjava 混用，(但 Rxjava 一定要注意及时关闭流，可以使用 BaseViewModel中的 safeSubscribeBy() 方法)
 * - Activity 层更推荐使用 Flow，因为一般不会涉及到很复杂的流处理
 *
 * 4、顺便说一下数据流动的原则（个人观点）：
 * - 推荐 Room 使用 Observable 来观察本地数据，百度 Room 响应式编程
 * - Room 需要的数据类强烈不建议直接使用网络请求的 Bean 类，因为这样以后不好维护
 * - Repository 层用于转换 网络请求的 Bean 数据为 Room 的数据类，然后统一暴露 Room 数据类给 ViewModel
 *       而不是暴露 网络请求的 Bean 类，做到 VM 层与网络层的分离
 * - 如果网络请求数据简单，不需要本地化，可以省去 Repository 层，掌邮里面就基本没有 Repository 层
 */

/**
 * # 示例代码
 * 抓取 http 中的报错，并装换为直接包含 data 的 Flow
 * 建议配合 BaseViewModel 中 collectLaunch 一起食用
 * ```
 * flow {
 *     emit(FindApiServices.INSTANCE.getStudents(stu))
 * }.onStart {                  // 开始
 *     //...
 * }.onCompletion {             // 结束
 *     //...
 * }.mapOrCatchApiException {   // 出错
 *     toast("数据出错了")        // 请注意：这里只会抓取 ApiException，如果是没连接网络而抛出的 HttpException 是不会被抓取的
 * }.collectLaunch {            // 收集
 *     _studentData.emit(it)
 * }
 * ```
 *
 * # 其他注意事项
 * ## Flow 的下游没任何输出（怎么处理断网时的 HttpException）
 *
 * 大概率是你直接用了 [mapOrCatchApiException]，而它只会处理 [ApiException]，如果你要处理其他网络错误，
 * 请把 [mapOrCatchApiException] 替换为 [mapOrThrowApiException]：
 * ```
 *     .mapOrThrowApiException()
 *     .catch {
 *         if (it is ApiException) {
 *             // 处理 ApiException 错误
 *         } else {
 *             // 处理其他网络错误
 *         }
 *     }
 * ```
 *
 * ## 网络请求怎么直接返回 Flow?
 *
 * 由于目前 Retrofit 官方没有直接给出 Flow 的 adapter，如果有必要使用 Flow 的话，
 * 可以暂时使用 Observable 来转成 Flow
 *
 * **需要先引入依赖：dependCoroutinesRx3()**
 * ```
 * FindApiServices.INSTANCE.getStudents(stu)
 *     .subscribeOn(Schedulers.io()) // 线程切换
 *     .asFlow()                     // 转换成 Flow
 *     .mapOrCatchApiException {     // 出错
 *         toast("网络似乎开小差了")
 *     }.collectLaunch {             // 收集
 *         _studentData.emit(it)
 *     }
 * ```
 */
fun <T: ApiStatus> Flow<T>.throwApiExceptionIfFail(): Flow<T> {
  return onEach { it.throwApiExceptionIfFail() }
}

fun <E, T: ApiWrapper<E>> Flow<T>.mapOrThrowApiException(): Flow<E> {
  return throwApiExceptionIfFail()
    .map { it.data }
}

fun <E, T: ApiWrapper<E>> Flow<T>.mapOrCatchApiException(
  func: (ApiException) -> Unit
): Flow<E> {
  return mapOrThrowApiException()
    .catch { if (it is ApiException) func.invoke(it) }
}

fun <T : Any> Single<T>.asFlow(): Flow<T> {
  return toObservable().asFlow()
}

fun <T : Any> Maybe<T>.asFlow(): Flow<T> {
  return toObservable().asFlow()
}
