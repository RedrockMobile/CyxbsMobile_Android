package com.mredrock.cyxbs.qa.utils

import com.mredrock.cyxbs.common.utils.LogUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created By jay68 on 2018/9/20.
 */
fun Date.toFormatString(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.CHINA)
    return simpleDateFormat.format(this)
}

fun String.toDate(format: String = "yyyy-MM-dd HH:mm:ss"): Date {
    try {
        val simpleDateFormat = SimpleDateFormat(format, Locale.CHINA)
        return simpleDateFormat.parse(this)
    } catch (e: ParseException) {
        LogUtils.e("Date", e.message ?: "", e)
    }
    return Date()
}

fun timeDescription(current: Long, other: Long): String {
    val oneMinute = 60000
    val oneHour = 60 * oneMinute
    val oneDay = 24 * oneHour
    val differ = Math.abs(other - current)
    return when {
        differ < oneHour -> "${differ / oneMinute}分钟"
        differ < oneDay -> "${differ / oneHour}小时"
        differ < 7 * oneDay -> "${differ / oneDay}天"
        else -> {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            format.format(Date(other))
        }
    }
}