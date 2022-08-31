@file:Suppress("HasPlatformType")

package com.mredrock.cyxbs.lib.utils.extensions

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.Flow

/**
 * 如果你要看网络请求相关的示例代码，请移步于 network/ApiRxjava
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/30 10:12
 */


/**
 * 使用优雅的 DSL 来拦截异常
 *
 * # 如果你是想看网络请求的用法，请移步与 network/ApiRxjava.kt
 * # 该 api 的详细用法请查看 [Flow.interceptException]，注意事项也与 Flow 一致
 */
fun <T : Any> Single<T>.interceptException(
  action: ExceptionResult<SingleEmitter<T>>.(Throwable) -> Unit
) : Single<T> {
  return onErrorResumeNext { throwable ->
    Single.create {
      ExceptionResult(throwable, it).action(throwable)
    }
  }
}
fun <T : Any> Maybe<T>.interceptException(
  action: ExceptionResult<MaybeEmitter<T>>.(Throwable) -> Unit
) : Maybe<T> {
  return onErrorResumeNext { throwable ->
    Maybe.create {
      ExceptionResult<MaybeEmitter<T>>(throwable, it).action(throwable)
    }
  }
}
fun <T : Any> Observable<T>.interceptException(
  action: ExceptionResult<ObservableEmitter<T>>.(Throwable) -> Unit
) : Observable<T> {
  return onErrorResumeNext { throwable ->
    Observable.create {
      ExceptionResult<ObservableEmitter<T>>(throwable, it).action(throwable)
    }
  }
}
fun Completable.interceptException(
  action: ExceptionResult<CompletableEmitter>.() -> Unit
) : Completable {
  return onErrorResumeNext { throwable ->
    Completable.create {
      ExceptionResult<CompletableEmitter>(throwable, it).action()
    }
  }
}


/**
 * 使用 [Result] 来包裹一层数据，你可以配合网络请求使用
 *
 * 详细用法请看 [Flow.interceptExceptionByResult]
 */
fun <T : Any> Single<T>.interceptExceptionByResult(
  action: ExceptionResult<SingleEmitter<Result<T>>>.(Throwable) -> Unit
) : Single<Result<T>> = map { Result.success(it) }.interceptException(action)
fun <T : Any> Maybe<T>.interceptExceptionByResult(
  action: ExceptionResult<MaybeEmitter<Result<T>>>.(Throwable) -> Unit
) : Maybe<Result<T>> = map { Result.success(it) }.interceptException(action)
fun <T : Any> Observable<T>.interceptExceptionByResult(
  action: ExceptionResult<ObservableEmitter<Result<T>>>.(Throwable) -> Unit
) : Observable<Result<T>> = map { Result.success(it) }.interceptException(action)


/**
 * 这个命名与之前的 lib_common 中有些区别，common 中那个 safe 的意思是如果直接使用一个形参的 subscribe(onSuccess)，
 * 在收到上游错误时 Rxjava 会把错误直接抛给整个应用来处理，如果你没有配置 Rxjava 的全局报错，应用会直接闪退，
 * common 中 safe 就是指上述安全问题
 *
 * 目前这个 safe 表示带有生命周期的安全订阅
 */

fun <T : Any> Single<T>.unsafeSubscribeBy(
  onError: (Throwable) -> Unit = {},
  onSuccess: (T) -> Unit = {}
): Disposable = subscribe(onSuccess, onError)
fun <T : Any> Maybe<T>.unsafeSubscribeBy(
  onError: (Throwable) -> Unit = {},
  onComplete: () -> Unit = {},
  onSuccess: (T) -> Unit = {}
): Disposable = subscribe(onSuccess, onError, onComplete)
fun <T : Any> Observable<T>.unsafeSubscribeBy(
  onError: (Throwable) -> Unit = {},
  onComplete: () -> Unit = {},
  onNext: (T) -> Unit = {}
): Disposable = subscribe(onNext, onError, onComplete)
fun <T : Any> Flowable<T>.unsafeSubscribeBy(
  onError: (Throwable) -> Unit = {},
  onComplete: () -> Unit = {},
  onNext: (T) -> Unit = {}
): Disposable = subscribe(onNext, onError, onComplete)
fun Completable.unsafeSubscribeBy(
  onError: (Throwable) -> Unit = {},
  onComplete: () -> Unit = {},
): Disposable = subscribe(onComplete, onError)
