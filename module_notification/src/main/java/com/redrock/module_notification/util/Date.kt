package com.redrock.module_notification.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

/**
 * Author by OkAndGreat
 * Date on 2022/5/2 16:46.
 *
 */
object Date {
    /**
     * 统一日期格式，如：2021.5.5/2021-5-5 12:00
     */
    private val sdf1 = SimpleDateFormat("yyyy.M.d", Locale.CHINA)
    private val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    private val sdf3 = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    /**
     * 根据时间戳获取日期, 形式为 yyyy.M.d
     *
     * **NOTE:** 时间单位是毫秒
     */
    fun getTime(time: Long): String {
        val date = Date(time * 1000)
        return sdf1.format(date)
    }

    fun getTime(date: Date): String {
        return sdf1.format(date)
    }

    /**
     * 根据时间戳获取日期, 形式为 yyyy-MM-dd HH:mm
     *
     * **NOTE:** 时间单位是毫秒
     */
    fun getExactTime(time: Long): String {
        val date = Date(time * 1000)
        return sdf2.format(date)
    }

    /**
     * 根据时间戳获取日期, 形式为 yyyy-MM-dd
     *
     * **NOTE:** 时间单位是毫秒
     */
    fun getUnExactTime(time: Long): String {
        val date = Date(time * 1000)
        return sdf3.format(date)
    }
}