package com.mredrock.cyxbs.api.affair.utils

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/24
 * description:
 */

/**
 * 把 [beginLesson] 转换成对应的 row
 */
fun getStartRow(beginLesson: Int): Int {
  return when (beginLesson) {
    in 1..4 -> beginLesson - 1
    in 5..8 -> beginLesson
    in 9..12 -> beginLesson + 1
    -1 -> 4 // 中午
    -2 -> 9 // 傍晚
    else -> throw IllegalArgumentException("出现了未知的时间点，beginLesson = $beginLesson")
  }
}

