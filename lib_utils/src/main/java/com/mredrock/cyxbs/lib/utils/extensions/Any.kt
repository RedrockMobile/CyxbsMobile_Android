package com.mredrock.cyxbs.lib.utils.extensions

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/25 14:37
 */

fun <T> Any.cast(): T = this as T

fun <T> Any?.cast2(): T = this as T