package com.mredrock.cyxbs.lib.base.utils

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable

/**
 * 实现该接口，即代表该类支持自动关闭 Rxjava
 *
 * ## 为什么不放到 lib_utils 中？
 * 如果放到 lib_utils 中，在只依赖 lib_base 的时候会出现无法继承 BaseActivity 的情况
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

  /**
   * RxjavaLifecycle 绑定 Lifecycle
   */
  fun bindLifecycle(disposable: Disposable, lifecycle: Lifecycle) {
    lifecycle.addObserver(
      object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
          if (event.targetState == Lifecycle.State.DESTROYED) {
            source.lifecycle.removeObserver(this)
            disposable.dispose() // 在 DESTROYED 时关掉流
          } else {
            if (disposable.isDisposed) {
              // 如果在其他生命周期时流已经被关了，就取消该观察者
              source.lifecycle.removeObserver(this)
            }
          }
        }
      }
    )
  }
}

fun <T : Any> Single<T>.safeSubscribeBy(
  lifecycle: RxjavaLifecycle,
  onError: (Throwable) -> Unit = {},
  onSuccess: (T) -> Unit = {}
): Disposable = lifecycle.run { safeSubscribeBy(onError, onSuccess) }

fun <T : Any> Observable<T>.safeSubscribeBy(
  lifecycle: RxjavaLifecycle,
  onError: (Throwable) -> Unit = {},
  onComplete: () -> Unit = {},
  onNext: (T) -> Unit = {}
): Disposable = lifecycle.run { safeSubscribeBy(onError, onComplete, onNext) }

fun Completable.safeSubscribeBy(
  lifecycle: RxjavaLifecycle,
  onError: (Throwable) -> Unit = {},
  onComplete: () -> Unit = {},
): Disposable = lifecycle.run { safeSubscribeBy(onError, onComplete) }

/**
 * 关联 View 的生命周期，因为 View 生命周期回调的残缺，只能绑定 OnAttachStateChangeListener ，
 * 但这个其实是不完整的，因为 View 不显示在屏幕上时也会回调
 *
 * ## 所以建议用在只会显示一次的 View 上
 */
fun <T : Any> Single<T>.safeSubscribeBy(
  view: View,
  onError: (Throwable) -> Unit = {},
  onSuccess: (T) -> Unit = {}
): Disposable = subscribe(onSuccess, onError).also {
  view.addOnAttachStateChangeListener(
    object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {}
      override fun onViewDetachedFromWindow(v: View) {
        v.removeOnAttachStateChangeListener(this)
        it.dispose()
      }
    }
  )
}

/**
 * 关联 View 的生命周期，因为 View 生命周期回调的残缺，只能绑定 OnAttachStateChangeListener ，
 * 但这个其实是不完整的，因为 View 不显示在屏幕上时也会回调
 *
 * ## 所以建议用在只会显示一次的 View 上
 */
fun <T : Any> Observable<T>.safeSubscribeBy(
  view: View,
  onError: (Throwable) -> Unit = {},
  onComplete: () -> Unit = {},
  onNext: (T) -> Unit = {}
): Disposable = subscribe(onNext, onError, onComplete).also {
  view.addOnAttachStateChangeListener(
    object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {}
      override fun onViewDetachedFromWindow(v: View) {
        v.removeOnAttachStateChangeListener(this)
        it.dispose()
      }
    }
  )
}

/**
 * 关联 View 的生命周期，因为 View 生命周期回调的残缺，只能绑定 OnAttachStateChangeListener ，
 * 但这个其实是不完整的，因为 View 不显示在屏幕上时也会回调
 *
 * ## 所以建议用在只会显示一次的 View 上
 */
fun Completable.safeSubscribeBy(
  view: View,
  onError: (Throwable) -> Unit = {},
  onComplete: () -> Unit = {},
): Disposable = subscribe(onComplete, onError).also {
  view.addOnAttachStateChangeListener(
    object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {}
      override fun onViewDetachedFromWindow(v: View) {
        v.removeOnAttachStateChangeListener(this)
        it.dispose()
      }
    }
  )
}

