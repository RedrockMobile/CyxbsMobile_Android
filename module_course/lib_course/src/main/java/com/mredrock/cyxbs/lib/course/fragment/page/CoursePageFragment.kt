package com.mredrock.cyxbs.lib.course.fragment.page

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.base.PageTimelineImpl
import com.mredrock.cyxbs.lib.course.helper.CourseDownAnimDispatcher
import com.mredrock.cyxbs.lib.course.helper.ScrollTouchHandler
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 17:35
 */
abstract class CoursePageFragment : PageTimelineImpl() {
  
  final override fun initCourseInternal() {
    super.initCourseInternal()
    course {
      initNoLessonLogic()
      // 设置默认的手指触摸处理者
      setDefaultHandler { event, _ ->
        // 如果是第一根手指的事件应该交给 ScrollView 拦截，而不是自身处理
        if (event.pointerId != 0) ScrollTouchHandler else null
      }
      // Q弹动画的实现
      addPointerDispatcher(CourseDownAnimDispatcher(this))
    }
    initCourse()
  }
  
  abstract fun initCourse()
  
  /**
   * 初始化显示没课图片的逻辑
   */
  protected open fun initNoLessonLogic() {
    course {
      var showItemCount = 0
      addItemExistListener(
        object : IItemContainer.OnItemExistListener {
          override fun onItemAddedAfter(item: IItem) {
            if (isExhibitionItem(item)) showItemCount++
            mViewNoLesson.visibility = View.GONE
          }
      
          override fun onItemRemovedAfter(item: IItem) {
            if (isExhibitionItem(item)) showItemCount--
            if (showItemCount == 0) {
              mViewNoLesson.visibility = View.VISIBLE
            }
          }
        }
      )
    }
  }
  
  /**
   * 是否是用于展示的 item，会影响没课图片的显示
   * @return 返回 true，则表示该 [item] 是用于展示的
   */
  protected open fun isExhibitionItem(item: IItem): Boolean {
    return item.isLessonItem || item.isAffairItem
  }
}