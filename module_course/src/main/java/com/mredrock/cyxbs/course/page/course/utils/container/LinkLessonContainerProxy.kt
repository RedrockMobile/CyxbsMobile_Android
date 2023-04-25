package com.mredrock.cyxbs.course.page.course.utils.container

import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.lesson.LinkLessonItem
import com.mredrock.cyxbs.course.page.course.utils.container.base.ItemPoolController
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.utils.extensions.anim

/**
 * 代理添加 [LinkLessonItem]
 *
 * - 提供差分刷新方法 [diffRefresh]
 * - 实现了 [LinkLessonItem] 的回收池用于复用
 * - 监听了使用其他方式删除的 [LinkLessonItem]，用于解决差分旧数据不同步的问题
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/10 18:22
 */
class LinkLessonContainerProxy(
  val page: ICoursePage
) : ItemPoolController<LinkLessonItem, StuLessonData>(page) {
  
  override fun areItemsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areItemsTheSame(oldItem, newItem)
  }
  
  override fun areContentsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areContentsTheSame(oldItem, newItem)
  }
  
  override val itemClass: Class<LinkLessonItem>
    get() = LinkLessonItem::class.java
  
  override fun onChanged(oldData: StuLessonData, newData: StuLessonData) {
    val item = mOldDataMap[oldData]
    page.changeOverlap(item, false) // 取消重叠
    super.onChanged(oldData, newData)
    page.changeOverlap(item, true) // 恢复重叠
  }
  
  override fun addItem(item: LinkLessonItem) {
    page.addLesson(item)
  }
  
  override fun removeItem(item: LinkLessonItem) {
    page.removeLesson(item)
  }
  
  override fun newItem(data: StuLessonData): LinkLessonItem {
    return LinkLessonItem(data)
  }
  
  override fun onDiffRefreshOver(oldData: List<StuLessonData>, newData: List<StuLessonData>) {
    super.onDiffRefreshOver(oldData, newData)
    page.refreshOverlap() // 刷新重叠
  }
  
  private val mStartAnimationRun = Runnable {
    /*
    * 使用 Runnable 的原因
    * 1、因为 [LinkLesson] 是 [IOverlapItem]，它会延迟添加 item，所以需要使用 post 个 Runnable
    * */
    getShowItems().forEachIndexed { index, item ->
      // 因为 Animation 没有公开 clone() 方法，所以只能每次 new 一个新的
      val anim = com.mredrock.cyxbs.lib.course.R.anim.course_anim_entrance.anim
      anim.startOffset = index * 60L
      item.startAnimation(anim)
    }
  }
  
  /**
   * 开启关联人 item 的入场动画
   */
  fun startAnimation() {
    page.course.removeCallbacks(mStartAnimationRun)
    page.course.post(mStartAnimationRun)
  }
}