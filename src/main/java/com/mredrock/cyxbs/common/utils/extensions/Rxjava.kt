package com.mredrock.cyxbs.common.utils.extensions

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.network.exception.DefaultErrorHandler
import com.mredrock.cyxbs.common.network.exception.ErrorHandler
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.network.exception.RedrockApiIllegalStateException
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created By jay68 on 2018/8/10.
 */

/**
 * note：请放在有UI操作的操作符前（map等操作符后）, 否则将抛出异常，原因：{@see <a href="https://www.jianshu.com/p/3e5d53e891db"/>}
 */
fun <T> Observable<T>.setSchedulers(
        subscribeOn: Scheduler = Schedulers.io(),
        unsubscribeOn: Scheduler = Schedulers.io(),
        observeOn: Scheduler = AndroidSchedulers.mainThread()
): Observable<T> = subscribeOn(subscribeOn)
        .unsubscribeOn(unsubscribeOn)
        .observeOn(observeOn)

fun <T> Observable<RedrockApiWrapper<T>>.mapOrThrowApiException(): Observable<T> = map { it.nextOrError() }

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
inline fun <T> Observable<T>.doOnErrorWithDefaultErrorHandler(defaultErrorHandler: ErrorHandler? = DefaultErrorHandler,
                                                              crossinline onError: (Throwable) -> Boolean): Observable<T> = doOnError { if (!onError.invoke(it)) defaultErrorHandler?.handle(it) }

fun <T> Observable<T>.errorHandler(errorHandler: ErrorHandler = DefaultErrorHandler) = doOnError { errorHandler.handle(it) }

/**
 * 未实现onError时不会抛出[io.reactivex.exceptions.OnErrorNotImplementedException]异常
 */
fun <T> Observable<T>.safeSubscribeBy(
        onError: (Throwable) -> Unit = {},
        onComplete: () -> Unit = {},
        onNext: (T) -> Unit = {}
): Disposable = subscribe(onNext, onError, onComplete)