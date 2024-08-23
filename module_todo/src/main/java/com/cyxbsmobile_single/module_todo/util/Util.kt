package com.cyxbsmobile_single.module_todo.util

import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.lib.utils.extensions.appContext

val weekStringList = listOf(
    "一",
    "二",
    "三",
    "四",
    "五",
    "六",
    "日"
)

fun getColor(id: Int): Int = ContextCompat.getColor(appContext, id)

fun <T> ArrayList<T>.addWithoutRepeat(pos: Int ,t: T) {
    if (!contains(t)) {
        add(pos, t)
    }
}