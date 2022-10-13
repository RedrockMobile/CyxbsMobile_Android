package com.mredrock.cyxbs.lib.utils.extensions

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/25 20:58
 */

/**
 * 注释查看：LifecycleOwner 的 [LifecycleOwner.launchCatch]
 */
fun CoroutineScope.launchCatch(block: suspend CoroutineScope.() -> Unit): CatchSaver {
  return CatchSaver(this, block)
}

class CatchSaver(
  private val lifecycleScope: CoroutineScope,
  private val block: suspend CoroutineScope.() -> Unit
) {
  
  /**
   * 直接运行，不抓取任何错误
   */
  fun runWithoutCatch(): Job {
    return lifecycleScope.launch(block = block)
  }
  
  /**
   * 抓取错误
   */
  fun catch(catch: CoroutineContext.(Throwable) -> Unit): Job {
    return lifecycleScope.launch(
      CoroutineExceptionHandler(catch),
      block = block
    )
  }
}