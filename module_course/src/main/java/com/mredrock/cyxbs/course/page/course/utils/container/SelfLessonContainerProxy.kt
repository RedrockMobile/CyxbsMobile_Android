package com.mredrock.cyxbs.course.page.course.utils.container

import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.lesson.SelfLessonItem
import com.mredrock.cyxbs.course.page.course.utils.container.base.ItemPoolController
import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer

/**
 * 代理添加 [SelfLessonItem]
 *
 * - 提供差分刷新方法 [diffRefresh]
 * - 实现了 [SelfLessonItem] 的回收池用于复用
 * - 监听了使用其他方式删除的 [SelfLessonItem]，用于解决差分旧数据不同步的问题
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/10 17:27
 */
class SelfLessonContainerProxy(
  val container: ICourseContainer
) : ItemPoolController<SelfLessonItem, StuLessonData>(container) {
  
  override fun areItemsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areItemsTheSame(oldItem, newItem)
  }
  
  override fun areContentsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areContentsTheSame(oldItem, newItem)
  }
  
  override val itemClass: Class<SelfLessonItem>
    get() = SelfLessonItem::class.java
  
  override fun addItem(item: SelfLessonItem) {
    container.addLesson(item)
  }
  
  override fun removeItem(item: SelfLessonItem) {
    container.removeLesson(item)
  }
  
  override fun newItem(data: StuLessonData): SelfLessonItem {
    return SelfLessonItem(data)
  }
}