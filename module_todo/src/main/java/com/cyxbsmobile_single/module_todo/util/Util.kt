package com.cyxbsmobile_single.module_todo.util

import androidx.core.content.ContextCompat
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
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

fun <T> ArrayList<T>.addWithoutRepeat(pos: Int, t: T) {
    if (!contains(t)) {
        add(pos, t)
    }
}

fun transformRepeat(selectRepeatTimeList: List<String>, repeatMode: Int): ArrayList<String> =
    when (repeatMode) {
        RemindMode.WEEK -> {
            selectRepeatTimeList.map {
                "周${digitToChinese(it)}"
            } as ArrayList<String>
        }

        RemindMode.MONTH -> {
            selectRepeatTimeList.map {
                "每月${digitToChinese(it)}日"
            } as ArrayList<String>
        }

        else -> {
            mutableListOf("每天") as ArrayList<String>
        }
    }

fun digitToChinese(digit: String): String {
    return when (digit) {
        "1" -> "一"
        "2" -> "二"
        "3" -> "三"
        "4" -> "四"
        "5" -> "五"
        "6" -> "六"
        "7" -> "日"
        "8" -> "八"
        "9" -> "九"
        else -> ""
    }
}
