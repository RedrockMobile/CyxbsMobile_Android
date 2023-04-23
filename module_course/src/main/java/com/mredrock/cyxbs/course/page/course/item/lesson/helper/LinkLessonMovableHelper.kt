package com.mredrock.cyxbs.course.page.course.item.lesson.helper

import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.course.page.course.item.lesson.LinkLessonItem
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 针对于 [LinkLessonItem] 的 [MovableItemHelper] 的代理类
 *
 * @author 985892345
 * 2023/4/18 19:22
 */
class LinkLessonMovableHelper : ITouchItemHelper {
  
  private val mMovableConfig = object : IMovableItemConfig {
    override fun isMovableToNewLocation(
      page: ICoursePage, item: ITouchItem,
      child: View, newLocation: LocationUtil.Location
    ): Boolean {
      return false // 课程不能移动到新位置
    }
  }
  
  private val mMovableListener = object : IMovableItemListener {
    override fun onLongPressed(
      page: ICoursePage,
      item: ITouchItem,
      child: View,
      x: Int,
      y: Int,
      pointerId: Int
    ) {
      item as LinkLessonItem
      page.changeOverlap(item, false) // 暂时取消重叠
    }
    
    override fun onOverAnimStart(
      newLocation: LocationUtil.Location?,
      page: ICoursePage, item: ITouchItem, child: View
    ) {
      item as LinkLessonItem
      page.changeOverlap(item, true) // 恢复重叠
    }
  }
  
  private val mMovableHelper = MovableItemHelper(mMovableConfig).apply {
    addMovableListener(mMovableListener)
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