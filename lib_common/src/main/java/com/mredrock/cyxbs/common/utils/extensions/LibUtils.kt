package com.mredrock.cyxbs.common.utils.extensions

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import com.alibaba.android.arouter.facade.template.IProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 * 这里面放的是 lib_utils 中的部分扩展
 *
 * 因为 lib_common 模块目前逻辑太多，还不能反向依赖 lib_utils，
 * 所以为了给之前的模块做好兼容，强烈建议把新的扩展统一写在一起
 *
 * 但请注意，并不是所有的扩展都是必要的！！！
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/14 20:34
 */


/**
 * IProvider 作为单例类，生命周期与应用保持一致
 */
val IProvider.lifecycleScope: LifecycleCoroutineScope
  get() = ProcessLifecycleOwner.get().lifecycle.coroutineScope

fun IProvider.launch(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launch(context, start, block)