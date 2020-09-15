package com.mredrock.cyxbs.volunteer.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val dayMillis: Long = 24 * 60 * 60 * 1000

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

    /**
     * 时间戳
     * 转换
     * 2020.09.10
     */
    @SuppressLint("SimpleDateFormat")
    fun stamp2Date(stamp: Long) = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(stamp * 1000)).substring(0, 10).replace('-', '.')

    /**
     * 时间戳
     * 今天到截至时间的天数
     */
    fun lastDay(stamp: Long): Int = ((stamp * 1000 - System.currentTimeMillis()) / dayMillis).toInt()
}