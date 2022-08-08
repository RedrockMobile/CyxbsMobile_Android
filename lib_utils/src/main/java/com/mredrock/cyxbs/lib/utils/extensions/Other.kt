package com.mredrock.cyxbs.lib.utils.extensions

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


