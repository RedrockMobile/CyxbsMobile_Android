package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.page.expose.INoLesson
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible

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
    initNoLessonLogic(savedInstanceState)
  }
  
  /**
   * 初始化显示没课图片的逻辑
   */
  private fun initNoLessonLogic(savedInstanceState: Bundle?) {
    course.apply {
      var showItemCount = 0
      addItemExistListener(
        object : IItemContainer.OnItemExistListener {
          override fun onItemAddedAfter(item: IItem) {
            if (isExhibitionItem(item)) showItemCount++
            viewNoLesson.gone()
          }
          
          override fun onItemRemovedAfter(item: IItem) {
            if (isExhibitionItem(item)) showItemCount--
            if (showItemCount == 0) {
              viewNoLesson.visible()
            }
          }
        }
      )
    }
    /**
     * VP 中 Fragment 离开远了，再划回去的时候会重新加载，
     * 此时如果本来就有 item 显示，会出现没课图片闪动一下的场景，
     * 所以需要保存一下没课图片的显示状况
     */
    if (savedInstanceState != null) {
      if (savedInstanceState.getBoolean("没课图片之前是否显示")) {
        viewNoLesson.visible()
      } else {
        viewNoLesson.gone()
      }
    }
  }
  
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean("没课图片之前是否显示", viewNoLesson.isVisible)
  }
}