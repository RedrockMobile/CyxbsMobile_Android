package com.mredrock.cyxbs.course.page.course.utils.container

import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.lesson.SelfLesson
import com.mredrock.cyxbs.course.page.course.utils.container.base.CourseContainerProxy
import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer
import com.mredrock.cyxbs.lib.course.internal.item.IItem

/**
 * 由于内部在 init 使用了 course，所以外面生成对象时候需要使用 by lazyUnlock
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/10 17:27
 */
class SelfLessonContainerProxy(
  val container: ICourseContainer
) : CourseContainerProxy<SelfLesson, StuLessonData>(container) {
  
  override fun areItemsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areItemsTheSame(oldItem, newItem)
  }
  
  override fun areContentsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areContentsTheSame(oldItem, newItem)
  }
  
  override fun checkItem(item: IItem): Boolean {
    return item is SelfLesson
  }
  
  override fun addItem(item: SelfLesson) {
    container.addLesson(item)
  }
  
  override fun removeItem(item: SelfLesson) {
    container.removeLesson(item)
  }
  
  override fun newItem(data: StuLessonData): SelfLesson {
    return SelfLesson(data)
  }
}