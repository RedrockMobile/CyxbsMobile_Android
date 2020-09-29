package com.mredrock.cyxbs.qa.utils

import io.reactivex.Observable
import io.reactivex.Observable.error
import io.reactivex.ObservableSource
import java.util.concurrent.TimeUnit


/**
 * @Author: xgl
 * @ClassName: RetryWithDelay
 * @Description:rxjava 重试
 * @Date: 2020/9/27 22:45
 */
class RetryWithDelay(val maxRetries: Int, val retryDelayMillis: Int) : io.reactivex.functions.Function<Observable<out Throwable?>?, Observable<*>?> {
    private var retryCount: Int = 0

    override fun apply(attempts: Observable<out Throwable?>): Observable<*>? {
        return attempts
                .flatMap(object : io.reactivex.functions.Function<Throwable?, ObservableSource<out Any>> {

                    override fun apply(throwable: Throwable): ObservableSource<out Any> {
                        if (++retryCount <= maxRetries) {
                            // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                            return Observable.timer(retryDelayMillis.toLong(),
                                    TimeUnit.MILLISECONDS)
                        }
                        // Max retries hit. Just pass the error along.
                        return error(throwable)
                    }
                })
    }

}