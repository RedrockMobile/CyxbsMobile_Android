package com.mredrock.cyxbs.lib.course.item.lesson

import com.mredrock.cyxbs.lib.course.item.single.SingleDayLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 19:12
 */
abstract class BaseLessonLayoutParams(
  weekNum: Int,
  startNode: Int,
  length: Int
) : SingleDayLayoutParams(
  weekNum, startNode, length
), ILessonData {
  
  constructor(data: ILessonData) : this(data.weekNum, data.startNode, data.length)
}