package com.cyxbsmobile_single.module_todo.adapter.wheel_picker

import com.cyxbsmobile_single.module_todo.util.weekStringList

/**
 * Author: RayleighZ
 * Time: 2021-09-18 8:23
 */
class WeekAdapter : BaseWheelAdapter(7) {
    override fun getTextWithMaximumLength(): String = "周天"
    override fun getPositivePosition(position: Int): String = "周" + weekStringList[position]
}