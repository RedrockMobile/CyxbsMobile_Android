@file:Suppress("HasPlatformType")

package com.mredrock.cyxbs.lib.utils.network

import com.mredrock.cyxbs.lib.utils.extensions.interceptException
import io.reactivex.rxjava3.core.*
import kotlinx.coroutines.flow.Flow

/**
 * # 网络请求示例代码
 * ```
 * ApiService.INSTANCE.getXXX()
 *     .subscribeOn(Schedulers.io())  // 线程切换
 *     .observeOn(AndroidSchedulers.mainThread())
 *     .mapOrInterceptException {     // 当 status 的值不为成功时或有其他网络异常时
 *
 *         toast("捕捉全部异常")        // 直接写的话可以处理全部异常
 *
 *         emitter.onSuccess(...)     // 手动发送新的值给下游
 *
 *         ApiException {
 *             // 处理全部 ApiException 错误
 *         }.catchOther {
 *             // 处理非 ApiException 的其他异常
 *         }
 *
 *         ApiExceptionExcept {
 *             // 这样写可以直接处理 非 ApiException 的其他异常
 *         }
 *
 *         ApiException(10010) {
 *             // 单独处理 status 为 10010 时的 ApiException
 *         }
 *
 *         HttpExceptionData<XXXBean>(500) {
 *             // 处理 HttpException 并反序列化 json 为 XXXBean
 *
 *             onSuccess(it) // 也可以重新发送 XXXBean 给下游，但注意 XXXBean 需要与数据流中的泛型一致才能发送
 *         }
 *
 *         HttpExceptionBody(500) {
 *             // 处理 HttpException，但只获取 errorBody
 *         }
 *     }
 *     .unsafeSubscribeBy {           // 这里默认处理了所有异常，防止 Rxjava 把异常往默认全局异常处理抛
 *         // 成功的时候                // 因为掌邮没有使用全局异常处理，如果抛给全局异常了，会导致应用崩溃
 *     }
 *     // ViewModel 中带有的自动回收，直接使用 ViewModel 里面的 safeSubscribeBy 方法即可
 * ```
 *
 * 除了 mapOrInterceptException() 方法，还有 throwApiExceptionIfFail() 和 mapOrThrowApiException()
 * 后者都是简单的抛出异常但不拦截
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/30 10:12
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
fun <Data : Any, T : IApiWrapper<Data>> Single<T>.mapOrInterceptException(
  action: ApiExceptionResult<SingleEmitter<Data>>.(Throwable) -> Unit
) : Single<Data> {
  return mapOrThrowApiException().onErrorResumeNext { error ->
    Single.create {
      ApiExceptionResult(error, it).action(error)
    }
  }
}

fun <T : IApiStatus> Single<T>.throwOrInterceptException(
  action: ApiExceptionResult<SingleEmitter<T>>.(Throwable) -> Unit
) : Single<T> {
  return throwApiExceptionIfFail().onErrorResumeNext { error ->
    Single.create {
      ApiExceptionResult(error, it).action(error)
    }
  }
}


fun <Data : Any, T : IApiWrapper<Data>> Observable<T>.mapOrInterceptException(
  action: ApiExceptionResult<ObservableEmitter<Data>>.(Throwable) -> Unit
) : Observable<Data> {
  return mapOrThrowApiException().onErrorResumeNext { error ->
    Observable.create {
      ApiExceptionResult(error, it).action(error)
    }
  }
}

fun <T : IApiStatus> Observable<T>.throwOrInterceptException(
  action: ApiExceptionResult<ObservableEmitter<T>>.(Throwable) -> Unit
) : Observable<T> {
  return throwApiExceptionIfFail().onErrorResumeNext { error ->
    Observable.create {
      ApiExceptionResult(error, it).action(error)
    }
  }
}



/**
 * 装换为 [Data] 并检查数据中的状态码是否有问题，有就抛出异常
 *
 * 注意：数据中的状态码是 status 字段，不是 Http 状态码，这两个状态码不要混淆了
 */
fun <Data : Any, T : IApiWrapper<Data>> Single<T>.mapOrThrowApiException(): Single<Data> {
  return throwApiExceptionIfFail()
    .map {
      it.data
    }
}

fun <T : IApiStatus> Single<T>.throwApiExceptionIfFail(): Single<T> {
  return doOnSuccess {
    it.throwApiExceptionIfFail()
  }
}


fun <Data : Any, T : IApiWrapper<Data>> Observable<T>.mapOrThrowApiException(): Observable<Data> {
  return throwApiExceptionIfFail()
    .map {
      it.data
    }
}
fun <T : IApiStatus> Observable<T>.throwApiExceptionIfFail(): Observable<T> {
  return doOnNext {
    it.throwApiExceptionIfFail()
  }
}
