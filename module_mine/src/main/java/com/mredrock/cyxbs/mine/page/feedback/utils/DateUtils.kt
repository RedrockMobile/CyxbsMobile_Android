package com.mredrock.cyxbs.mine.page.feedback.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:36
 *@signature 我们不明前路，却已在路上
 */
object DateUtils {
    @JvmStatic
    fun longToDate(format: String, timePill: Long): String {
        if (timePill == 0L) return ""
        val date = Date(timePill)
        val simpleFormat = SimpleDateFormat(format, Locale.CHINA)
        return simpleFormat.format(date) ?: ""
    }

    @JvmStatic
    fun strToLong(mStr: String): Long {
        val timePills = if (mStr.length >= 15) {
            mStr.substring(0..9).replace('-', '/') + " " + mStr.substring(11..15)
        } else {
            ""
        }
        val date = SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA).parse(timePills)
        return date.time
    }
}