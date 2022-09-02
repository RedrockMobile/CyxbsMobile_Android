package com.mredrock.cyxbs.api.course.utils

import java.util.Calendar.*
import java.util.regex.Pattern

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/12 16:42
 */

/**
 * 把 [beginLesson] 转换成 0 - 12 的数字，为了好排序
 */
fun getStart(beginLesson: Int): Int {
  return when (beginLesson) {
    in 1..4 -> beginLesson - 1
    in 5..8 -> beginLesson
    in 9..12 -> beginLesson + 1
    -1 -> 4 // 中午
    -2 -> 9 // 傍晚
    else -> throw RuntimeException("出现了未知的时间点！")
  }
}

/**
 * 得到 0 - 12 中对应的数字
 */
fun getEnd(beginLesson: Int, period: Int): Int {
  val start = getStart(beginLesson)
  return start + period - 1
}

/**
 * @param start 这个 [start] 是调用了 [getStart] 转换后的值，也可以是 row
 */
fun getStartTime(start: Int): Int {
  return when (start) {
    0 -> 8 * 60
    1 -> 8 * 60 + 55
    2 -> 10 * 60 + 15
    3 -> 11 * 60 + 10
    4 -> 11 * 60 + 55
    5 -> 14 * 60
    6 -> 14 * 60 + 55
    7 -> 16 * 60 + 15
    8 -> 17 * 60 + 10
    9 -> 19 * 60
    10 -> 19 * 60 + 55
    11 -> 20 * 60 + 50
    12 -> 21 * 60 + 45
    13 -> 22 * 60 + 30
    else -> 0
  }
}

/**
 * @param end 这个 [end] 是调用了 [getEnd] 转换后的值，也可以是 row
 */
fun getEndTime(end: Int): Int {
  return when (end) {
    0 -> 8 * 60 + 45
    1 -> 9 * 60 + 40
    2 -> 11 * 60
    3 -> 11 * 60 + 55
    4 -> 14 * 60
    5 -> 14 * 60 + 45
    6 -> 15 * 60 + 40
    7 -> 17 * 60
    8 -> 17 * 60 + 55
    9 -> 19 * 60 + 45
    10 -> 20 * 60 + 40
    11 -> 21 * 60 + 35
    12 -> 22 * 60 + 30
    13 -> 24 * 60
    else -> 0
  }
}

private val Calendar = getInstance()

/**
 * 得到当前时间
 */
fun getNowTime(): Int {
  return Calendar.run {
    val hour = get(HOUR_OF_DAY)
    val minute = get(MINUTE)
    hour * 60 + minute
  }
}

fun getShowTimeStr(beginLesson: Int, period: Int): String {
  val startStr = getShowStartTimeStr(getStart(beginLesson))
  val endStr = getShowEndTimeStr(getEnd(beginLesson, period))
  if (startStr.isNotEmpty() && endStr.isNotEmpty()) {
    return "$startStr-$endStr"
  }
  return ""
}

fun getShowStartTimeStr(start: Int): String {
  return when (start) {
    0 -> "8:00"
    1 -> "8:55"
    2 -> "10:15"
    3 -> "11:10"
    4 -> "11:55"
    5 -> "14:00"
    6 -> "14:55"
    7 -> "16:15"
    8 -> "17:10"
    9 -> "19:00"
    10 -> "19:55"
    11 -> "20:50"
    12 -> "21:45"
    13 -> "22:30"
    else -> ""
  }
}

fun getShowEndTimeStr(end: Int): String {
  return when (end) {
    0 -> "8:45"
    1 -> "9:40"
    2 -> "11:00"
    3 -> "11:55"
    4 -> "14:00"
    5 -> "14:45"
    6 -> "15:40"
    7 -> "17:00"
    8 -> "17:55"
    9 -> "19:45"
    10 -> "20:40"
    11 -> "21:35"
    12 -> "22:30"
    13 -> "24:00"
    else -> ""
  }
}

/*
*
* 下面的找到 classRoom 是以前学长的老代码
*
* */

// group(1)匹配到的是非字母和数字；group(2)匹配到的是数字、字母以及/。在group(1)中不能把/添加，这里需要匹配
// 的/是夹在数字中间的/。
private const val mRegex = "([^[a-zA-Z0-9]]*)([a-zA-Z0-9|/]*)"
private val mPattern = Pattern.compile(mRegex)

/**
 * @param classRoom 原始的教室字符串
 */
fun parseClassRoom(classRoom: String): String {
  // 短教室直接返回
  if (classRoom.length <= 7) {
    return classRoom
  }
  
  val matcher = mPattern.matcher(classRoom)
  val parseStrings = mutableListOf<String>()
  
  // 找到字符串中所有的长度大于3的连续数字或字符。
  while (matcher.find()) {
    val group = matcher.group(2)
    if (group != null) {
      if (group.length > 3) {
        parseStrings.add(group)
      }
    }
  }
  // 如果没有前面没有匹配到就返回原始字符串；如果匹配到了就进行链接操作。
  return if (parseStrings.isEmpty()) {
    classRoom
  } else {
    val linkStringSb = StringBuilder()
    for ((index, str) in parseStrings.withIndex()) {
      linkStringSb.append(str)
      if (index < (parseStrings.size - 1)) {
        linkStringSb.append("/")
      }
    }
    linkStringSb.toString()
  }
}