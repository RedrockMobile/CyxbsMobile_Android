package com.cyxbsmobile_single.module_todo.adapter.wheel_picker

/**
 * Author: RayleighZ
 * Time: 2021-09-18 8:24
 */
class RepeatTypeAdapter : BaseWheelAdapter(6) {
    private val dataList = listOf(
        "每天",
        "每周",
        "每月",
        "每年"
    )

    override fun getTextWithMaximumLength(): String = "每年"

    override fun getValue(position: Int): String = when (position) {
        in 0..3 -> {
            getPositivePosition(position % 4)
        }
        else -> {
            ""
        }
    }

    override fun getPositivePosition(position: Int): String = dataList[position]
}