package com.mredrock.cyxbs.lib.course.internal.lesson

import com.mredrock.cyxbs.lib.course.internal.day.ISingleDayItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 19:50
 */
interface ILessonItem : ISingleDayItem, ILessonData {
  override val lp: BaseLessonLayoutParams
}