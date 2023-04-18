package com.mredrock.cyxbs.course.page.course.item.helper

import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.course.page.course.item.lesson.LinkLessonItem
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemHelperConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 针对于 [LinkLessonItem] 的 [MovableItemHelper] 的代理类
 *
 * @author 985892345
 * 2023/4/18 19:22
 */
class LinkLessonMovableHelper(
  val lesson: LinkLessonItem
) : ITouchItemHelper {
  
  private val mMovableHelper = MovableItemHelper(
    object : IMovableItemHelperConfig {
      override fun isMovableToNewLocation(
        page: ICoursePage, item: ITouchItem,
        child: View, newLocation: LocationUtil.Location
      ): Boolean {
        return false // 课程不能移动到新位置
      }
    }
  ).apply {
    addMovableListener(
      object : IMovableListener {
        override fun onLongPressStart(
          page: ICoursePage, item: ITouchItem, child: View,
          initialX: Int, initialY: Int, x: Int, y: Int
        ) {
          super.onLongPressStart(page, item, child, initialX, initialY, x, y)
          page.changeOverlap(lesson, false) // 暂时取消重叠
        }
      
        override fun onOverAnimStart(
          newLocation: LocationUtil.Location?,
          page: ICoursePage, item: ITouchItem, child: View
        ) {
          super.onOverAnimEnd(newLocation, page, item, child)
          page.changeOverlap(lesson, true) // 恢复重叠
        }
      }
    )
  }
  
  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) {
    mMovableHelper.onPointerTouchEvent(event, parent, child, item, page)
  }
  
  override fun isAdvanceIntercept(): Boolean {
    return mMovableHelper.isAdvanceIntercept()
  }
}