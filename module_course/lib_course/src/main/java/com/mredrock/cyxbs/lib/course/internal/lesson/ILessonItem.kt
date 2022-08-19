package com.mredrock.cyxbs.lib.course.internal.lesson

import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.lesson.period.IAmLessonItem
import com.mredrock.cyxbs.lib.course.internal.lesson.period.INightLessonItem
import com.mredrock.cyxbs.lib.course.internal.lesson.period.IPmLessonItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 19:50
 */
interface ILessonItem : IItem, ILessonData {
  
  val isAmLessonItem: Boolean
    get() = this is IAmLessonItem
  
  val isPmLessonItem: Boolean
    get() = this is IPmLessonItem
  
  val isNightLessonItem: Boolean
    get() = this is INightLessonItem
}