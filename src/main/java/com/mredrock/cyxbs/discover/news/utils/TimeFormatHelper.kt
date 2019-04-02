package com.mredrock.cyxbs.discover.news.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Create By Hosigus at 2019/4/2
 */
object TimeFormatHelper {
    fun format(str: String): String = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(SimpleDateFormat("yyyy.MM.dd", Locale.CHINA).parse(str))
}