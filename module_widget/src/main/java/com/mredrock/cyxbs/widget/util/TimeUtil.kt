package com.mredrock.cyxbs.widget.util

import java.util.*

/**
 * Created by zia on 2018/10/12.
 */
object TimeUtil {

    fun calcDayOffset(date1: Date, date2: Date): Int {
        val cal1 = Calendar.getInstance()
        cal1.time = date1

        val cal2 = Calendar.getInstance()
        cal2.time = date2
        val day1 = cal1.get(Calendar.DAY_OF_YEAR)
        val day2 = cal2.get(Calendar.DAY_OF_YEAR)

        val year1 = cal1.get(Calendar.YEAR)
        val year2 = cal2.get(Calendar.YEAR)
        if (year1 != year2) {  //同一年
            var timeDistance = 0
            for (i in year1 until year2) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
                    timeDistance += 366
                } else {  //不是闰年

                    timeDistance += 365
                }
            }
            return timeDistance + (day2 - day1)
        } else { //不同年
            return day2 - day1
        }
    }

    fun calcWeekOffset(startTime: Date, endTime: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = startTime
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        dayOfWeek = dayOfWeek - 1
        if (dayOfWeek == 0) dayOfWeek = 7

        val dayOffset = calcDayOffset(startTime, endTime)

        var weekOffset = dayOffset / 7
        val a: Int
        if (dayOffset > 0) {
            a = if (dayOffset % 7 + dayOfWeek > 7) 1 else 0
        } else {
            a = if (dayOfWeek + dayOffset % 7 < 1) -1 else 0
        }
        weekOffset = weekOffset + a
        return weekOffset
    }
}
