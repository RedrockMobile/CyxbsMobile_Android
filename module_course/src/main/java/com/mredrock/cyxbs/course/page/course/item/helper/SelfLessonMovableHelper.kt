package com.mredrock.cyxbs.course.page.course.item.helper

import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.course.page.course.item.lesson.SelfLessonItem
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 针对于 [SelfLessonItem] 的 [MovableItemHelper] 的代理类
 *
 * @author 985892345
 * 2023/4/18 19:22
 */
class SelfLessonMovableHelper(
  val lesson: SelfLessonItem
) : ITouchItemHelper {
  
  private val mMovableHelper = MovableItemHelper(
    object : IMovableItemConfig {
      override fun isMovableToNewLocation(
        page: ICoursePage, item: ITouchItem,
        child: View, newLocation: LocationUtil.Location
      ): Boolean {
        return false // 课程不能移动到新位置
      }
    }
  ).apply {
    addMovableListener(
      object : IMovableItemListener {
        override fun onLongPressed(
          page: ICoursePage, item: ITouchItem, child: View,
          x: Int, y: Int
        ) {
          super.onLongPressed(page, item, child, x, y)
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