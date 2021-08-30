package com.cyxbsmobile_single.module_todo.util

/**
 * Author: RayleighZ
 * Time: 2021-08-31 2:17
 * 一些拓展函数
 */
fun <T> ArrayList<T>.addWithoutRepeat(pos: Int ,t: T) {
    if (!contains(t)) {
        add(pos, t)
    }
}