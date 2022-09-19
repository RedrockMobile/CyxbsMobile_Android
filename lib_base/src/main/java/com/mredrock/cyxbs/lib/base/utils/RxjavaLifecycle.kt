package com.mredrock.cyxbs.lib.base.utils

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable

/**
 * 实现该接口，即代表该类支持自动关闭 Rxjava
 *
 * ## 为什么不放到 lib_utils 中？
 * 如果放到 lib_utils 中，在值依赖 lib_base 的时候会出现无法继承 BaseActivity 的情况
 *
 * 所以要求 base 类实现的接口尽量不要放在其他模块内
 *
 * ## 一、示例
 * ```
 * IXXXApiService::class.api
 *     .getXXX()
 *     .safeSubscribeBy {
 *         // 使用 safeSubscribeBy() 将 Rxjava 流与生命周期相关联，实现自动取消
 *     }
 * ```
 *
 *
 *
 *
 */
interface RxjavaLifecycle {
  
  /**
   * 请把他们放在一个数组中，并在合适的时候关闭
   */
  fun onAddRxjava(disposable: Disposable)
  
  fun <T : Any> Single<T>.safeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit = {}
  ): Disposable = subscribe(onSuccess, onError).also { onAddRxjava(it) }
  
  @Deprecated(
    "该类已实现 Rxjava 的生命周期，请使用 safeSubscribeBy() 代替",
    ReplaceWith("safeSubscribeBy()"))
  fun <T : Any> Single<T>.unsafeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit = {}
  ): Disposable = subscribe(onSuccess, onError)
  
  fun <T : Any> Maybe<T>.safeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onSuccess: (T) -> Unit = {}
  ): Disposable = subscribe(onSuccess, onError).also { onAddRxjava(it) }
  
  @Deprecated(
    "该类已实现 Rxjava 的生命周期，请使用 safeSubscribeBy() 代替",
    ReplaceWith("safeSubscribeBy()"))
  fun <T : Any> Maybe<T>.unsafeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onSuccess: (T) -> Unit = {}
  ): Disposable = subscribe(onSuccess, onError, onComplete)
  
  fun <T : Any> Observable<T>.safeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onNext: (T) -> Unit = {}
  ): Disposable = subscribe(onNext, onError, onComplete).also { onAddRxjava(it) }
  
  @Deprecated(
    "该类已实现 Rxjava 的生命周期，请使用 safeSubscribeBy() 代替",
    ReplaceWith("safeSubscribeBy()"))
  fun <T : Any> Observable<T>.unsafeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onNext: (T) -> Unit = {}
  ): Disposable = subscribe(onNext, onError, onComplete)
  
  fun <T : Any> Flowable<T>.safeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onNext: (T) -> Unit = {}
  ): Disposable = subscribe(onNext, onError, onComplete).also { onAddRxjava(it) }
  
  @Deprecated(
    "该类已实现 Rxjava 的生命周期，请使用 safeSubscribeBy() 代替",
    ReplaceWith("safeSubscribeBy()"))
  fun <T : Any> Flowable<T>.unsafeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onNext: (T) -> Unit = {}
  ): Disposable = subscribe(onNext, onError, onComplete)
  
  fun Completable.safeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
  ): Disposable = subscribe(onComplete, onError).also { onAddRxjava(it) }
  
  @Deprecated(
    "该类已实现 Rxjava 的生命周期，请使用 safeSubscribeBy() 代替",
    ReplaceWith("safeSubscribeBy()"))
  fun Completable.unsafeSubscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
  ): Disposable = subscribe(onComplete, onError)
}