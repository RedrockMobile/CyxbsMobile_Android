package com.cyxbsmobile_single.module_todo.adapter.wheel_picker

import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.util.weekStringList

/**
 * Author: RayleighZ
 * Time: 2021-09-18 8:26
 */
class DateAdapter(private val dateBeenList: List<DateBeen>) :
    BaseWheelAdapter(dateBeenList.size) {
    override fun getTextWithMaximumLength(): String = "12月31日 周天"

    override fun getValue(position: Int): String {
        if (position < 0) {
            return ""
        }
        return getPositivePosition(position % dateBeenList.size)
    }

    override fun getPositivePosition(position: Int): String {
        if (position == 0) {
            return "今天"
        }
        val curDateBeen = dateBeenList[(position)]
        return "${curDateBeen.month}月${curDateBeen.day}日 周${weekStringList[curDateBeen.week - 1]}"
    }
}