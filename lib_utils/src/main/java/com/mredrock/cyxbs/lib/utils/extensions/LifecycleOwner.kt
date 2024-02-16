package com.mredrock.cyxbs.lib.utils.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

fun LifecycleOwner.launch(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit,
): Job {
  return lifecycleScope.launch(context, start, block)
}

inline fun <T, R> T.runCatchingCoroutine(block: T.() -> R): Result<R> {
  return try {
    Result.success(block())
  } catch (e: CancellationException) {
    throw e // 协程的取消需要抛出
  } catch (e: Throwable) {
    Result.failure(e)
  }
}


/**
 * 链式处理协程异常
 *
 * 有以下作用：
 * - 返回 [CatchSaver]，如果你想捕获异常，调用 .catch {}
 * 使用方法：
 * ```
 * launchCatch {
 *   // 网络请求
 * }.catch {
 *   // 处理错误
 * }
 * ```
 *
 * ## 背景：
 * 如果你想抓取 launch 的异常，正常写法是：
 * ```
 * lifecycleScope.launch(
 *   CoroutineExceptionHandler { context, exception ->
 *     exception.printStackTrace()
 *   }
 * ) {
 *   // ......
 * }
 * ```
 * 很明显就能看出这样写很不优雅，如果能像 rxjava 一样优雅就好了
 *
 * 那该怎么实现呢？
 *
 * 思路很简单，就是让函数返回一个对象，我们可以在返回的对象中设置异常的捕获，设置后才启动协程
 *
 * # 注意：只有你主动写了 .catch {} 后它才启动协程
 * 如果想直接调用，建议使用前面的 LifecycleOwner.launch
 *
 * ## 为什么不实现只写 launchCatch 就可以调用的效果？
 * 因为 LifecycleOwner 实现的协程调度是 Dispatchers.Main.immediate，
 * 带有 immediate 会使当前协程如果已经在正确的上下文中时立即执行协程
 *
 * 2024.2.16:
 * 其实协程处理异常也可以使用 runCatchingCoroutine
 * ```
 * launch {
 *   runCatchingCoroutine {
 *     // 官方的 runCatching 会拦截 CancellationException 导致协程不被取消
 *   }.onSuccess {
 *     // ...
 *   }.onFailure {
 *     // ...
 *   }
 * }
 * ```
 *
 */
fun LifecycleOwner.launchCatch(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): CatchSaver {
  return CatchSaver(this.lifecycleScope, context, start, block)
}