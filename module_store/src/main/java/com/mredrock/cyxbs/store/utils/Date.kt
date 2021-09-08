package com.mredrock.cyxbs.store.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/14 10:11
 */
object Date {
    /**
     * 统一日期格式，如：2021.5.5/2021-5-5 12:00
     */
    private val sdf1 = SimpleDateFormat("yyyy.M.d", Locale.CHINA)
    private val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

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
}