package com.mredrock.cyxbs.course.page.course.item.helper

import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.course.page.course.item.affair.AffairItem
import com.mredrock.cyxbs.course.page.course.item.affair.IMovableAffairManager
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemHelperConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 针对于 [AffairItem] 的 [MovableItemHelper] 的代理类
 *
 * @author 985892345
 * 2023/4/18 19:15
 */
class AffairMovableHelper(
  affair: AffairItem,
  iMovableAffairManager: IMovableAffairManager
) : ITouchItemHelper {
  
  private val mMovableConfig = object : IMovableItemHelperConfig, IMovableListener {
    
    override fun isMovableToNewLocation(
      page: ICoursePage, item: ITouchItem,
      child: View, newLocation: LocationUtil.Location
    ): Boolean {
      return iMovableAffairManager.isMovableToNewLocation(
        page,
        affair,
        child,
        newLocation,
        affair.data
      )
    }
    
    override fun onLongPressStart(
      page: ICoursePage, item: ITouchItem, child: View,
      initialX: Int, initialY: Int, x: Int, y: Int
    ) {
      super.onLongPressStart(page, item, child, initialX, initialY, x, y)
      page.changeOverlap(affair, false) // 暂时取消重叠
      iMovableAffairManager.onLongPressStart(page, affair, child, affair.data)
    }
    
    override fun onOverAnimStart(
      newLocation: LocationUtil.Location?,
      page: ICoursePage, item: ITouchItem, child: View
    ) {
      super.onOverAnimEnd(newLocation, page, item, child)
      page.changeOverlap(affair, true) // 恢复重叠
      iMovableAffairManager.onOverAnimStart(newLocation, page, affair, child, affair.data)
    }
    
    override fun onOverAnimEnd(
      newLocation: LocationUtil.Location?,
      page: ICoursePage,
      item: ITouchItem,
      child: View
    ) {
      super.onOverAnimEnd(newLocation, page, item, child)
      iMovableAffairManager.onOverAnimEnd(newLocation, page, affair, child, affair.data)
    }
  }
  
  private val mMovableHelper = MovableItemHelper(mMovableConfig).apply {
    addMovableListener(mMovableConfig)
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