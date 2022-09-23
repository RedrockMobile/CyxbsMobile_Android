package com.mredrock.cyxbs.lib.utils.extensions

import android.content.res.Configuration
import androidx.lifecycle.LifecycleCoroutineScope
import com.alibaba.android.arouter.facade.template.IProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/7 17:51
 */

/**
 * 不带锁的懒加载，建议使用这个代替 lazy，因为 Android 一般情况下不会遇到多线程问题
 */
fun <T> lazyUnlock(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * IProvider 作为单例类，生命周期与应用保持一致
 */
val IProvider.lifecycleScope: LifecycleCoroutineScope
  get() = processLifecycleScope

fun IProvider.launch(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launch(context, start, block)

/**
 * 是否是日间模式，否则为夜间模式
 */
fun isDaytimeMode(): Boolean {
  val uiMode = appContext.resources.configuration.uiMode
  return (uiMode and  Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
}


