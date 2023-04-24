package com.mredrock.cyxbs.course.page.course.utils.container

import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.lesson.SelfLessonItem
import com.mredrock.cyxbs.course.page.course.utils.container.base.ItemPoolController
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage

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
  val page: ICoursePage
) : ItemPoolController<SelfLessonItem, StuLessonData>(page) {
  
  override fun areItemsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areItemsTheSame(oldItem, newItem)
  }
  
  override fun areContentsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areContentsTheSame(oldItem, newItem)
  }
  
  override val itemClass: Class<SelfLessonItem>
    get() = SelfLessonItem::class.java
  
  override fun onChanged(oldData: StuLessonData, newData: StuLessonData) {
    val item = mOldDataMap[oldData]
    page.changeOverlap(item, false) // 取消重叠
    super.onChanged(oldData, newData)
    page.changeOverlap(item, true) // 恢复重叠
  }
  
  override fun addItem(item: SelfLessonItem) {
    page.addLesson(item)
  }
  
  override fun removeItem(item: SelfLessonItem) {
    page.removeLesson(item)
  }
  
  override fun newItem(data: StuLessonData): SelfLessonItem {
    return SelfLessonItem(data)
  }
  
  override fun onDiffRefreshOver(oldData: List<StuLessonData>, newData: List<StuLessonData>) {
    super.onDiffRefreshOver(oldData, newData)
    page.refreshOverlap() // 刷新重叠
  }
}