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
    78300000, // "21:45"
  )

  /**
   * 得到开始时间
   */
  fun getBegin(startRow: Int, weekNum: Int): Long {
    // 在拿不到开学第一天时间戳时，返回 2022 年 9 月 5 号，但这个是不可能出现的情况
    val tmp = SchoolCalendar.getFirstMonDayTimestamp() ?: 1662307200000
    // 得到确切的时间 开学第一天+星期*8640+选择的开始时间
    return tmp + LESSON_TIME_ARRAY[startRow] + weekNum * 86400000
  }

  /**
   * 将持续时间转化为rule持续时间
   */
  fun getDuration(period: Int): String {
    return when (period) {
      1 -> "P45M"
      2 -> "PT1H40M"
      3 -> "PT2H35M"
      4 -> "PT3H25M"
      else -> "P1M"
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
   * 设置提醒时间
   */
  fun getRemind(remind: Int): Int {
    return when (remind) {
      1 -> 5
      2 -> 10
      3 -> 20
      4 -> 30
      5 -> 60
      else -> 0
    }
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