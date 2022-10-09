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
 * 把 [beginLesson] 转换成对应的 row
 */
fun getStartRow(beginLesson: Int): Int {
  return when (beginLesson) {
    in 1 .. 4 -> beginLesson - 1
    in 5 .. 8 -> beginLesson
    in 9 .. 12 -> beginLesson + 1
    -1 -> 4 // 中午
    -2 -> 9 // 傍晚
    else -> throw IllegalArgumentException("出现了未知的时间点，beginLesson = $beginLesson")
  }
}

/**
 * 得到结尾对应的 row
 */
fun getEndRow(beginLesson: Int, period: Int): Int {
  val start = getStartRow(beginLesson)
  return start + period - 1
}

/**
 * 将 [startRow] 装换为对应的 beginLesson
 */
fun getBeginLesson(startRow: Int): Int {
  return when (startRow) {
    in 0 .. 3 -> startRow + 1
    4 -> -1 // 中午
    in 5 .. 8 -> startRow
    9 -> -2 // 傍晚
    in 10 .. 13 -> startRow - 1
    else -> throw IllegalArgumentException("不存在对应的 beginLesson，startRow = $startRow")
  }
}

/**
 * @param startRow 这个 [startRow] 是调用了 [getStartRow] 转换后的值
 */
fun getStartTime(startRow: Int): Int {
  return when (startRow) {
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
 * @param endRow 这个 [endRow] 是调用了 [getEndRow] 转换后的值，也可以是 row
 */
fun getEndTime(endRow: Int): Int {
  return when (endRow) {
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
    timeInMillis = System.currentTimeMillis()
    val hour = get(HOUR_OF_DAY)
    val minute = get(MINUTE)
    hour * 60 + minute
  }
}

fun getShowTimeStr(beginLesson: Int, period: Int): String {
  val startStr = getShowStartTimeStr(getStartRow(beginLesson))
  val endStr = getShowEndTimeStr(getEndRow(beginLesson, period))
  if (startStr.isNotEmpty() && endStr.isNotEmpty()) {
    return "$startStr-$endStr"
  }
  return ""
}

fun getShowStartTimeStr(startRow: Int): String {
  return when (startRow) {
    0 -> "8:00" // 第一节课开始
    1 -> "8:55" // 第二节课开始
    2 -> "10:15" // 第三节课开始
    3 -> "11:10" // 第四节课开始
    4 -> "11:55" // 中午开始
    5 -> "14:00" // 第五节课开始
    6 -> "14:55" // 第六节课开始
    7 -> "16:15" // 第七节课开始
    8 -> "17:10" // 第八节课开始
    9 -> "17:55" // 傍晚开始
    10 -> "19:00" // 第九节课开始
    11 -> "19:55" // 第十节课开始
    12 -> "20:50" // 第十一节课开始
    13 -> "21:45" // 第十二节课开始
    14 -> "22:30" // 全部课结束，夜晚的开始
    else -> ""
  }
}

fun getShowEndTimeStr(endRow: Int): String {
  return when (endRow) {
    0 -> "8:45" // 第一节课结束
    1 -> "9:40" // 第二节课结束
    2 -> "11:00" // 第三节课结束
    3 -> "11:55" // 第四节课结束
    4 -> "14:00" // 中午结束
    5 -> "14:45" // 第五节课结束
    6 -> "15:40" // 第六节课结束
    7 -> "17:00" // 第七节课结束
    8 -> "17:55" // 第八节课结束
    9 -> "19:00" // 傍晚结束
    10 -> "19:45" // 第九节课结束
    11 -> "20:40" // 第十节课结束
    12 -> "21:35" // 第十一节课结束
    13 -> "22:30" // 第十二节课结束
    14 -> "24:00" // 整天结束
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