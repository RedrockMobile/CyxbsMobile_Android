package com.mredrock.cyxbs.course.page.course.data

import com.mredrock.cyxbs.api.course.utils.getEndRow
import com.mredrock.cyxbs.api.course.utils.getShowEndTimeStr
import com.mredrock.cyxbs.api.course.utils.getShowStartTimeStr
import com.mredrock.cyxbs.api.course.utils.getStartRow
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayData
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock

/**
 * 课表 item 数据类
 *
 * @author 985892345
 * @date 2022/9/17 17:54
 */
sealed interface ICourseItemData : IWeek, ISingleDayData {
  val hashDay: Int // 星期数，星期一为 0
  val beginLesson: Int // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
  val period: Int // 长度
  
  val weekStr: String
    get() = WeekStr[week]
  
  val weekdayStr: String
    get() = WeekdayStr[hashDay]
  
  val durationStr: String
    get() = getShowStartTimeStr(getStartRow(beginLesson)) + "-" + getShowEndTimeStr(getEndRow(beginLesson, period))
  
  override val weekNum: Int
    get() = hashDay + 1
  override val startNode: Int
    get() = getStartRow(beginLesson)
  override val length: Int
    get() = period
  
  companion object {
    private val WeekStr by lazyUnlock {
      appContext.resources.getStringArray(R.array.course_course_weeks_strings)
    }
    
    private val WeekdayStr by lazyUnlock {
      val resources = appContext.resources
      arrayOf(
        resources.getString(R.string.course_week_mon),
        resources.getString(R.string.course_week_tue),
        resources.getString(R.string.course_week_wed),
        resources.getString(R.string.course_week_thu),
        resources.getString(R.string.course_week_fri),
        resources.getString(R.string.course_week_sat),
        resources.getString(R.string.course_week_sun),
      )
    }
  }
}