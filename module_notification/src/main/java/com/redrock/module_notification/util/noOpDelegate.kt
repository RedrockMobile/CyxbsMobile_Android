package com.redrock.module_notification.util

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * Author by OkAndGreat
 * Date on 2022/5/2 14:59.
 * 可以将接口不需要实现的交给此代理
 */

/**
使用实例：
使用前：
val lifecycleCallbacks =
object : Application.ActivityLifecycleCallbacks{
override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
TODO("Not yet implemented")
}

override fun onActivityStarted(activity: Activity) {
TODO("Not yet implemented")
}

override fun onActivityResumed(activity: Activity) {
TODO("Not yet implemented")
}

override fun onActivityPaused(activity: Activity) {
TODO("Not yet implemented")
}

override fun onActivityStopped(activity: Activity) {
TODO("Not yet implemented")
}

override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
TODO("Not yet implemented")
}

override fun onActivityDestroyed(activity: Activity) {
TODO("Not yet implemented")
}
}

使用后：
val lifecycleCallbacks =
object : Application.ActivityLifecycleCallbacks by noOpDelegate() {
override fun onActivityDestroyed(activity: Activity) {
//to do:...}
}
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