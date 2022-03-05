package com.mredrock.cyxbs.common.utils.extensions

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.network.exception.DefaultErrorHandler
import com.mredrock.cyxbs.common.network.exception.ErrorHandler
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.network.exception.RedrockApiIllegalStateException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Created By jay68 on 2018/8/10.
 *
 * ```
 * 示例代码：
 * ApiGenerator.getApiService(ApiService::class.java)
 *     .getXXX()
 *     .mapOrThrowApiException() // 用于检测 statue 的值
 *     .setSchedulers() // 设置线程切换
 *     .safeSubscribeBy(
 *         onError = { // 失败的时候
 *             if (it is RedrockApiException) {
 *                 // 这里是请求成功，但 statue 不为 200 或 10000
 *                 // 注意：RedrockApiException 中有几个参数用于方便使用，包含了 info 和 statue
 *             } else {
 *                 // 这里是其他网络错误
 *             }
 *         }
 *         onNext = { } // 成功的时候
 *     )
 *     .lifeCycle() // ViewModel 中带有的自动回收，建议加上
 *
 * 注意事项：
 * 1、端上与后端约定了只要能成功连接，网络状态码返回 200（网络状态码与 statue 不同）
 * 2、如果这次数据请求合理，后端应该返回 statue = 200
 *    （10000 也可以，此约定必须遵守，如果在刚开始开发时后端老哥给的接口不是 200，直接让他改接口）
 * 3、上面两条约定，如果后端老哥都没有遵守，请在项目还没有发布前让他修改
 * ```
 */

/**
 * note：请放在有UI操作的操作符前（map等操作符后）, 否则将抛出异常，原因：{@see <a href="https://www.jianshu.com/p/3e5d53e891db"/>}
 */
@CheckReturnValue
fun <T: Any> Observable<T>.setSchedulers(
    subscribeOn: Scheduler = Schedulers.io(),
    unsubscribeOn: Scheduler = Schedulers.io(),
    observeOn: Scheduler = AndroidSchedulers.mainThread()
): Observable<T> = subscribeOn(subscribeOn)
    .unsubscribeOn(unsubscribeOn)
    .observeOn(observeOn)

fun <T: Any> Observable<RedrockApiWrapper<T>>.mapOrThrowApiException(): Observable<T> =
    map { it.nextOrError() }

fun Observable<RedrockApiStatus>.checkError(): Observable<RedrockApiStatus> = map {
    if (it.isSuccessful) {
        it
    } else {
        throw RedrockApiException(it.info, it.status)
    }
}

fun <T> RedrockApiWrapper<T>.nextOrError() =
    if (isSuccessful) {
        data ?: throw RedrockApiIllegalStateException()
    } else {
        throw RedrockApiException(info, status, null)
    }

/**
 * 异常处理（不会消费onError事件，仍会回调观察者的onError）
 * 优先使用
 */
inline fun <T : Any> Observable<T>.doOnErrorWithDefaultErrorHandler(
    defaultErrorHandler: ErrorHandler? = DefaultErrorHandler,
    crossinline onError: (Throwable) -> Boolean
): Observable<T> = doOnError {
    if (!onError.invoke(it)) defaultErrorHandler?.handle(it)
}

fun <T : Any> Observable<T>.errorHandler(errorHandler: ErrorHandler = DefaultErrorHandler) =
    doOnError { errorHandler.handle(it) }

/**
 * 未实现onError时不会抛出[io.reactivex.exceptions.OnErrorNotImplementedException]异常
 */
fun <T : Any> Observable<T>.safeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onNext: (T) -> Unit = {}
): Disposable = subscribe(onNext, onError, onComplete)