package com.mredrock.cyxbs.qa.utils

import com.mredrock.cyxbs.common.utils.LogUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

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

fun dynamicTimeDescription(current: Long, other: Long): String {
    val oneMinute = 60000
    val oneHour = 60 * oneMinute
    val oneDay = 24 * oneHour
    val differ = abs(other - current)
    return when {
        differ < oneHour / 2 -> "刚刚"
        differ < oneDay - other % oneDay -> "今天"
        else -> {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            format.format(Date(other))
        }
    }
}

fun timeDescription(current: Long, date: String): String {
    val other = date.toDate().time
    val oneMinute = 60000
    val differ = abs(other - current)
    //"created_at":"2020-01-23 20:45:03"
    val day = date.split(" ")[0]
    val hourAndMin = date.split(" ")[1].substring(0, 5)
    val now = Date().toFormatString().split(" ")[0]
    return when {
        differ < oneMinute * 5 -> "刚刚"
        day == now -> "今天$hourAndMin"
        else -> {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            format.format(Date(other))
        }
    }
}