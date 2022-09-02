package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.os.Bundle
import android.view.View
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.page.expose.INoLesson
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:59
 */
@Suppress("LeakingThis")
abstract class NoLessonImpl : CourseTouchImpl(), INoLesson {
  
  override val viewNoLesson by R.id.course_view_no_lesson.view<View>()
  
  override fun isExhibitionItem(item: IItem): Boolean {
    return item.isLessonItem || item.isAffairItem
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initNoLessonLogic()
  }
  
  /**
   * 初始化显示没课图片的逻辑
   */
  private fun initNoLessonLogic() {
    course.apply {
      var showItemCount = 0
      addItemExistListener(
        object : IItemContainer.OnItemExistListener {
          override fun onItemAddedAfter(item: IItem) {
            if (isExhibitionItem(item)) showItemCount++
            viewNoLesson.visibility = View.GONE
          }
          
          override fun onItemRemovedAfter(item: IItem) {
            if (isExhibitionItem(item)) showItemCount--
            if (showItemCount == 0) {
              viewNoLesson.visibility = View.VISIBLE
            }
          }
        }
      )
    }
  }
}