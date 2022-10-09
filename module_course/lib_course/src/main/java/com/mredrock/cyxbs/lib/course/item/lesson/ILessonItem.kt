package com.mredrock.cyxbs.lib.course.item.lesson

import com.mredrock.cyxbs.lib.course.item.single.ISingleDayItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 19:50
 */
interface ILessonItem : ISingleDayItem, ILessonData {
  
  /**
   * 正确继承写法：
   * ```
   * override val lp = BaseLessonLayoutParams(data)
   * ```
   */
  override val lp: BaseLessonLayoutParams
}