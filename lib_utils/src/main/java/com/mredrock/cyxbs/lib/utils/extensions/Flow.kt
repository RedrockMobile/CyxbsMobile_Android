package com.mredrock.cyxbs.lib.utils.extensions

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.rx3.asFlow

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/30 10:19
 */

/**
 * 如果你要看网络请求相关的示例代码，请移步 network/ApiFlow
 *
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
 *
 * 5、请小心使用 flowWithLifecycle()、lifecycle.repeatOnLifecycle()，他们会导致数据倒灌，
 *    不适用于大部分场景！！！（别看到很多博客推荐就直接拿来用！！！）
 */



/**
 * 使用优雅的 DSL 来拦截异常
 *
 * ## 注意
 * - 使用该方法后，下游将不会收到任何异常，即异常在此步被全部拦截，除非你手动调用 throw 或 onError() 再次抛出异常
 * - 网络请求中请使用 mapOrInterceptException 或 throwOrInterceptException 方法
 * ```
 *   .interceptException {
 *
 *       toast("捕获全部异常") // 直接写的话是处理全部异常
 *
 *       RuntimeException::class.catch {
 *           // 捕获 RuntimeException
 *
 *           val exception = it // 闭包中 it 为当前异常
 *           val emitter = this // 闭包中 this 为 发射器，可用于发送值给下游
 *       }
 *
 *       NullPointerException::class.catch {
 *           // 捕获 NullPointerException
 *       }
 *
 *       IndexOutOfBoundsException::class.catchOther { // 使用 catchOther 捕获除自身以外的其他异常
 *           throw it // 你甚至可以把异常再次抛给下游（这里使用 catchOther 取了一个反，就会把其他异常抛到下游去）
 *       }
 *   }
 * ```
 */
fun <T> Flow<T>.interceptException(
  action: ExceptionResult<FlowCollector<T>>.() -> Unit
) : Flow<T> {
  return catch {
    ExceptionResult(it, this).action()
  }
}

fun <T : Any> Single<T>.asFlow(): Flow<T> {
  return toObservable().asFlow()
}

fun <T : Any> Maybe<T>.asFlow(): Flow<T> {
  return toObservable().asFlow()
}
