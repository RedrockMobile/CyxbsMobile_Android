package com.mredrock.cyxbs.course.page.course.data

import com.mredrock.cyxbs.api.course.utils.getEnd
import com.mredrock.cyxbs.api.course.utils.getShowEndTimeStr
import com.mredrock.cyxbs.api.course.utils.getShowStartTimeStr
import com.mredrock.cyxbs.api.course.utils.getStart
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonData
import com.mredrock.cyxbs.lib.utils.utils.Num2CN

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/10 21:13
 */
sealed class LessonData : ILessonData {
  abstract val week: Int
  abstract val beginLesson: Int
  abstract val classroom: String
  abstract val course: String
  abstract val courseNum: String
  abstract val day: String // 星期几，这是字符串的星期几：星期一、星期二......
  abstract val hashDay: Int // 星期数，星期一为 0
  abstract val period: Int
  abstract val rawWeek: String
  abstract val teacher: String
  abstract val type: String
  
  override val weekNum: Int
    get() = hashDay + 1
  override val startNode: Int
    get() = getStart(beginLesson)
  override val length: Int
    get() = period
  
  val timeType: Type
    get() {
      return when (beginLesson) {
        in 1 .. 4 -> Type.AM
        in 5 .. 8 -> Type.PM
        in 9 .. 12 -> Type.NIGHT
        else -> throw RuntimeException("未知课程出现在未知时间段！bean = $this")
      }
    }
  
  enum class Type {
    // 暂不存在中午傍晚时间段的课
    AM, PM, NIGHT
  }
  
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
        else -> throw RuntimeException("未知星期数：day = $day   bean = $this")
      }
    }
  
  val durationStr: String
    get() = getShowStartTimeStr(getStart(beginLesson)) + "-" + getShowEndTimeStr(getEnd(beginLesson, period))
}