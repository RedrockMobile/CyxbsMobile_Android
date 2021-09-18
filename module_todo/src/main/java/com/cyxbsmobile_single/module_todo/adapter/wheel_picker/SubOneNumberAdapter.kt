package com.cyxbsmobile_single.module_todo.adapter.wheel_picker

/**
 * Author: RayleighZ
 * Time: 2021-09-18 8:24
 */
class SubOneNumberAdapter(size: Int) : BaseWheelAdapter(size) {
    override fun getTextWithMaximumLength(): String = "24"
    override fun getPositivePosition(position: Int): String = "$position"
}