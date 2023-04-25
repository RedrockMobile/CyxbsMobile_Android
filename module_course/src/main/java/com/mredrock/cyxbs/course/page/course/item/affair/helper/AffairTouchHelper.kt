package com.mredrock.cyxbs.course.page.course.item.affair.helper

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.ExpandableItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.IExpandableItemConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.IExpandableItemHandler
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.mredrock.cyxbs.course.page.course.item.affair.AffairItem
import com.mredrock.cyxbs.course.page.course.item.affair.IAffairManager
import com.mredrock.cyxbs.course.page.course.item.affair.helper.listener.*
import com.mredrock.cyxbs.course.page.course.ui.home.HomeWeekFragment
import com.mredrock.cyxbs.course.page.course.item.affair.helper.listener.AffairExpandableHandler
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 针对于 [AffairItem] 的 [ExpandableItemHelper]、[MovableItemHelper] 的代理类
 *
 * 为了处理 [ExpandableItemHelper] 和 [MovableItemHelper] 之间的冲突，所以把它们合并在了一起
 *
 * @author 985892345
 * 2023/4/19 17:48
 */
class AffairTouchHelper(
  iAffairManager: IAffairManager
) : ITouchItemHelper {
  
  // 只允许一个 ITouchItemHelper 处理长按事件
  private var mPointerId = MotionEvent.INVALID_POINTER_ID
  
  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) {
    // Expandable 需要放到 Movable 前面，因为 Movable 触摸范围包含了 Expandable 的范围
    mExpandableItemHelper.onPointerTouchEvent(event, parent, child, item, page)
    mMovableItemHelper.onPointerTouchEvent(event, parent, child, item, page)
    if (event.pointerId == mPointerId) {
      if (event.action == IPointerEvent.Action.UP || event.action == IPointerEvent.Action.CANCEL) {
        // 此时当前手指事件已结束，重置状态
        mPointerId = MotionEvent.INVALID_POINTER_ID
      }
    }
  }
  
  override fun isAdvanceIntercept(): Boolean {
    return mExpandableItemHelper.isAdvanceIntercept() || mMovableItemHelper.isAdvanceIntercept()
  }
  
  
  
  ////////////////////////////////////////
  //
  //              Expandable
  //
  ////////////////////////////////////////
  
  // Expandable 的配置
  private val mExpandableConfig = object : IExpandableItemConfig {
    override fun isLongPress(
      event: IPointerEvent,
      page: ICoursePage,
      item: ITouchItem,
      child: View
    ): Boolean {
      if (super.isLongPress(event, page, item, child)) {
        mPointerId = event.pointerId
        return true
      }
      return false
    }
  
    override fun getExpandableHandler(): IExpandableItemHandler {
      return AffairExpandableHandler(iAffairManager)
    }
  }
  
  // Expandable 的监听
  private val mExpandableListener = AffairExpandableListener(iAffairManager)
  
  // Expandable 的帮助类
  private val mExpandableItemHelper = ExpandableItemHelper(mExpandableConfig).apply {
    addExpandableListener(mExpandableListener)
  }
  
  
  ////////////////////////////////////////
  //
  //               Movable
  //
  ////////////////////////////////////////
  
  // Movable 的配置
  private val mMovableConfig = object : IMovableItemConfig {
    override fun isLongPress(
      event: IPointerEvent,
      page: ICoursePage,
      item: ITouchItem,
      child: View
    ): Boolean {
      if (mPointerId == MotionEvent.INVALID_POINTER_ID) {
        // 只有在 Expandable 不处理时 Movable 才处理
        mPointerId = event.pointerId
        return true
      }
      return false
    }
  
    override fun isMovableToNewLocation(
      page: ICoursePage,
      item: ITouchItem,
      child: View,
      newLocation: LocationUtil.Location
    ): Boolean {
      item as AffairItem
      if (!iAffairManager.isSingleAffair(item.data)) {
        // 单一事务才允许移动
        toast("暂不支持移动重复事务")
        return false
      }
      return page is HomeWeekFragment // 只有周页面才能移动事务，整学期页面不允许移动
    }
  }
  
  // Movable 的监听
  private val mMovableListener = AffairMovableListener(iAffairManager)
  
  // Movable 的帮助类
  private val mMovableItemHelper = MovableItemHelper(mMovableConfig).apply {
    addMovableListener(mMovableListener)
  }
}