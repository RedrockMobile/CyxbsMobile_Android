package com.mredrock.cyxbs.course.page.course.item.lesson.lp

import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.lib.course.item.lesson.BaseLessonLayoutParams
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 17:21
 */
class SelfLessonLayoutParams(data: LessonData) : BaseLessonLayoutParams(data), ISingleDayRank, IWeek by data {
  
  override val rank: Int
    get() = 1
  
  override fun compareTo(other: NetLayoutParams): Int {
    return if (other is ISingleDayRank) compareToInternal(other) else 1
  }
}