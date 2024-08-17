package com.cyxbsmobile_single.module_todo.util



val weekStringList = listOf(
    "一",
    "二",
    "三",
    "四",
    "五",
    "六",
    "日"
)


fun <T> ArrayList<T>.addWithoutRepeat(pos: Int ,t: T) {
    if (!contains(t)) {
        add(pos, t)
    }
}