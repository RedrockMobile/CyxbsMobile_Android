package com.mredrock.cyxbs.lib.utils.utils.impl

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * 用于接口的默认实现，因为部分接口必须要求实现，但你不想全部实现，就可以使用该方式
 *
 * 使用前：
 * ```
 * object : XXXInterface {
 *
 *     override fun onCall1 { }
 *
 *     override fun onCall2 { }
 *
 *     override fun onCall3 { }
 * }
 * ```
 * 如果我只想实现 onCall1:
 * ```
 * object : XXXInterface by defaultImpl() {
 *     override fun onCall1 {
 *         // 其他方法全部委托给了 defaultImpl() 的动态代理来实现
 *     }
 * }
 * ```
 *
 * Author by OkAndGreat
 * Date on 2022/5/2 14:59.
 * 可以将接口不需要实现的交给此代理
 */
inline fun <reified T : Any> defaultImpl(): T {
  val javaClass = T::class.java
  return Proxy.newProxyInstance(
    javaClass.classLoader, arrayOf(javaClass), NO_OP_HANDLER
  ) as T
}

val NO_OP_HANDLER = InvocationHandler { _, _, _ ->
  // no op
}


//////////////////////////////////////////////////////
//
//    如果可以的话，尽量在下面写默认接口，动态代理有些耗性能
//               默认接口以 Impl 结尾
//
//////////////////////////////////////////////////////


interface ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {
  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
  }
  
  override fun onActivityStarted(activity: Activity) {
  }
  
  override fun onActivityResumed(activity: Activity) {
  }
  
  override fun onActivityPaused(activity: Activity) {
  }
  
  override fun onActivityStopped(activity: Activity) {
  }
  
  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
  }
  
  override fun onActivityDestroyed(activity: Activity) {
  }
}