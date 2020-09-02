package com.mredrock.cyxbs.discover.news.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Create By Hosigus at 2019/4/2
 */
object TimeFormatHelper {
    fun format(str: String): String =
            if (str.contains(":")) {
                SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(str))
            } else {
                SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(SimpleDateFormat("yyyy.MM.dd", Locale.CHINA).parse(str))
            }
}