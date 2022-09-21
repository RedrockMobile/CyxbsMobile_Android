package com.mredrock.cyxbs.course.page.course.data

import com.mredrock.cyxbs.api.course.utils.getEndRow
import com.mredrock.cyxbs.api.course.utils.getShowEndTimeStr
import com.mredrock.cyxbs.api.course.utils.getShowStartTimeStr
import com.mredrock.cyxbs.api.course.utils.getStartRow
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayData
import com.mredrock.cyxbs.lib.utils.utils.Num2CN

/**
 *
 *
 * @author 985892345
 * @date 2022/9/17 17:54
 */
sealed interface ICourseData : IWeek, ISingleDayData {
  val hashDay: Int
  val beginLesson: Int
  val period: Int
  
  val weekStr: String
    get() = "第${Num2CN.number2ChineseNumber(week.toLong())}周"
  
  val weekdayStr: String
    get() {
      return when (hashDay) {
        0 -> "周一"
        1 -> "周二"
        2 -> "周三"
        3 -> "周四"
        4 -> "周五"
        5 -> "周六"
        6 -> "周日"
        else -> throw RuntimeException("未知星期数：hashDay = $hashDay   bean = $this")
      }
    }
  
  val durationStr: String
    get() = getShowStartTimeStr(getStartRow(beginLesson)) + "-" + getShowEndTimeStr(getEndRow(beginLesson, period))
  
  override val weekNum: Int
    get() = hashDay + 1
  override val startNode: Int
    get() = getStartRow(beginLesson)
  override val length: Int
    get() = period
}