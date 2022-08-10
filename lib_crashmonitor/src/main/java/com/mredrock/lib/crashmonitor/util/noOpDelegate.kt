package com.mredrock.lib.crashmonitor.util

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/5
 * @Description:  通过代理去掉不需要的空方法（摘自leakCanary源码）
 */
internal inline fun <reified T : Any> noOpDelegate(): T {
    val javaClass = T::class.java
    return Proxy.newProxyInstance(
        javaClass.classLoader, arrayOf(javaClass), NO_OP_HANDLER
    ) as T
}
private val NO_OP_HANDLER = InvocationHandler { _, _, _ ->
    // 空实现不需要的方法
}