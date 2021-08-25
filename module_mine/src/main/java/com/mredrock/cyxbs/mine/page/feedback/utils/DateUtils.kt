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
}