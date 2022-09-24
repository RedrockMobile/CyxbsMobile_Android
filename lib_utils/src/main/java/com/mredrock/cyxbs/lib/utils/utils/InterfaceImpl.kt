package com.mredrock.cyxbs.lib.utils.utils

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