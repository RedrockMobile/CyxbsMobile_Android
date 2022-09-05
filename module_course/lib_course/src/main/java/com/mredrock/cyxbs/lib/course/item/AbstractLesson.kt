package com.mredrock.cyxbs.lib.course.item

import com.mredrock.cyxbs.lib.course.fragment.item.ILesson
import com.mredrock.cyxbs.lib.course.internal.lesson.BaseLessonLayoutParams
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 16:54
 */
abstract class AbstractLesson(
  private val data: ILessonData
) : AbstractOverlapItem(data), ILesson {
  abstract override val lp: BaseLessonLayoutParams
}