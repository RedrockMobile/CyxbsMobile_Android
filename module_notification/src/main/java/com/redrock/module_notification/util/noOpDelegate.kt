package com.redrock.module_notification.util

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * Author by OkAndGreat
 * Date on 2022/5/2 14:59.
 * 可以将接口不需要实现的交给此代理
 */

internal inline fun <reified T : Any> noOpDelegate(): T = noOperationDelegate()

internal inline fun <reified T : Any> noOperationDelegate(): T {
    val javaClass = T::class.java
    return Proxy.newProxyInstance(
        javaClass.classLoader, arrayOf(javaClass), NO_OP_HANDLER
    ) as T
}

private val NO_OP_HANDLER = InvocationHandler { _, _, _ ->
    // no op
}