package com.mredrock.cyxbs.volunteer.utils

import java.util.*

object DateUtils {
    /**
     * 2000-00-00
     * 转换
     * 00-00
     */
    fun separate(origin: String) = origin.substring(4, origin.length)
    fun getYear(origin: String) = if (origin.length > 4) {
        origin.substring(0, 4).toInt()
    } else 0


    /**
     * 判断今年
     */
    fun thisYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun stamp2Date(stamp: String) {

    }
}