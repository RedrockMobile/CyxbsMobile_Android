package com.mredrock.cyxbs.mine.page.sign

import java.util.*

/**
 * Created by roger on 2019/11/19
 */
class WeekGenerator constructor(val serialDays: Int, val isSign: Boolean) {
    init {
        val dayArr = arrayOfNulls<Boolean>(7)
        val dividerArr = arrayOfNulls<Boolean>(6)
        //得到当前weekDay，
        val toDay = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7


    }
}