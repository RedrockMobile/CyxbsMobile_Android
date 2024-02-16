package com.mredrock.cyxbs.lib.utils.extensions

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/25 20:58
 */

/**
 * 注释查看：LifecycleOwner 的 [LifecycleOwner.launchCatch]
 */
fun CoroutineScope.launchCatch(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): CatchSaver {
  return CatchSaver(this, context, start, block)
}

class CatchSaver(
  private val lifecycleScope: CoroutineScope,
  private val context: CoroutineContext,
  private val start: CoroutineStart,
  private val block: suspend CoroutineScope.() -> Unit
) {

  /**
   * 抓取错误
   */
  fun catch(catch: CoroutineContext.(Throwable) -> Unit): Job {
    return lifecycleScope.launch(
      CoroutineExceptionHandler(catch) + context, start, block
    )
  }
}