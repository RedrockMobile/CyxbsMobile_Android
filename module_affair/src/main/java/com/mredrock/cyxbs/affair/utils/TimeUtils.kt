package com.mredrock.cyxbs.affair.utils

import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.config.config.SchoolCalendar

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/14
 * description:
 */
object TimeUtils {

  private val WEEK_RULE_ARRAY = arrayOf("MO,", "TU,", "WE,", "TH,", "FR,", "SA,", "SU,")
  private val LESSON_TIME_ARRAY = arrayOf(
    28800000, // "8:00",
    32100000, // "8:55",
    36900000, // "10:15",
    40200000, // "11:10",
    43200000, // "12:00",
    50400000, // "14:00",
    53700000, // "14.55",
    58500000, // "16:15",
    61800000, // "17:10",
    64800000, // "18:00",
    68400000, // "19:00",
    71700000, // "19:55",
    75000000, // "20:50",
    78300000, // "21:45",
    81000000, // "22:30"
  )

  /**
   * 得到开始时间(重复事件,开始时间固定在该学期的第一周)
   */
  fun getBegin(startRow: Int, day: Int): Long {
    // 在拿不到开学第一天时间戳时，返回 2022 年 9 月 5 号，但这个是不可能出现的情况
    val tmp = SchoolCalendar.getFirstMonDayTimestamp() ?: 1662307200000
    // 得到确切的时间 开学第一天+星期*8640+选择的开始时间
    return tmp + LESSON_TIME_ARRAY[startRow] + day * 86400000
  }

  /**
   * 得到开始时间(一次性事件,开始时间在具体的某一周)
   */
  fun getBegin(startRow: Int, day: Int, weekNum: Int): Long {
    // 在拿不到开学第一天时间戳时，返回 2022 年 9 月 5 号，但这个是不可能出现的情况
    val tmp = SchoolCalendar.getFirstMonDayTimestamp() ?: 1662307200000
    // 得到确切的时间 开学第一天+星期数*8640+选择的开始时间+(周数-1)*604800000
    return tmp + LESSON_TIME_ARRAY[startRow] + day * 86400000 + (weekNum.toLong() - 1) * 604800000
  }

  /**
   * 得到结束时间(只有一次性事件)
   */
  fun getEnd(startRow: Int, day: Int, weekNum: Int, endRow: Int): Long {
    return getBegin(
      startRow,
      day,
      weekNum
    ) + LESSON_TIME_ARRAY[endRow + 1] - LESSON_TIME_ARRAY[startRow]
  }

  /**
   * 将持续时间转化为rule持续时间
   */
  fun getDuration(startRow: Int, endRow: Int): String {
    val duration = LESSON_TIME_ARRAY[endRow + 1] - LESSON_TIME_ARRAY[startRow]
    return if (duration < 60000) {
      "P${duration / 60000}M"
    } else {
      "PT${duration / 3660000}H${duration % 3600000 / 60000}M"
    }
  }

  /**
   * 将周数转换为Rule规则,这个是将所有的周数连在一起
   */
  fun getRRule(week: List<Int>): String {
    val weekCount = week.size
    var str = "FREQ=WEEKLY;COUNT=${weekCount * 21};BYDAY="
    week.forEach { str += WEEK_RULE_ARRAY[it] }
    // 去除最后一个,
    str = str.substring(0, str.length - 1)
    return str
  }

  /**
   * 将周数转换为Rule规则,只添加单个周数
   */
  fun getRRule(week: Int): String {
    var str = "FREQ=WEEKLY;COUNT=21;BYDAY="
    str += WEEK_RULE_ARRAY[week]
    // 去除最后一个,
    str = str.substring(0, str.length - 1)
    return str
  }

  /**
   * 将星期数合成一个list
   */
  fun atWhatTimeToWeekList(rawList: List<AffairEntity.AtWhatTime>): List<Int> {
    val set = mutableSetOf<Int>()
    rawList.forEach { set.add(it.day) }
    return set.toList()
  }
}