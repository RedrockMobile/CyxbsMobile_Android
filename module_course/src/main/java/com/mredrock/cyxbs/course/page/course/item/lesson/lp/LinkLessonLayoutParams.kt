package com.mredrock.cyxbs.course.page.course.item.lesson.lp

import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.IRank
import com.mredrock.cyxbs.lib.course.internal.lesson.BaseLessonLayoutParams
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 17:20
 */
class LinkLessonLayoutParams(data: LessonData) : BaseLessonLayoutParams(data), IRank, IWeek by data {
  
  override val rank: Int
    get() = 3
  
  override fun compareTo(other: NetLayoutParams): Int {
    return if (other is IRank) compareToInternal(other) else 1
  }
}