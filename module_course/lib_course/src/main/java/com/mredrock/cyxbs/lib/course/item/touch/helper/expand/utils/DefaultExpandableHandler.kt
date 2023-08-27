package com.mredrock.cyxbs.lib.course.item.touch.helper.expand.utils

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.helper.expand.DoubleSideExpandableHelper
import com.mredrock.cyxbs.lib.course.item.helper.expand.ISingleSideExpandable
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.IExpandableItemHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 默认的处理 View 扩展的工具类
 *
 * @author 985892345
 * 2023/4/20 18:42
 */
open class DefaultExpandableHandler : IExpandableItemHandler {
  
  /**
   * 重新设置初始值
   * @param y 相对于 course 的 y 值
   */
  open fun resetInitialValue(page: ICoursePage, item: ITouchItem, child: View, y: Int) {
    if (mDownTopOrBottom) {
      mInitialRow = item.lp.endRow
      mBoundaryDistance = child.top - y
    } else {
      mInitialRow = item.lp.startRow
      mBoundaryDistance = child.bottom - y
    }
  }
  
  /**
   * 生成一个新的 [ISingleSideExpandable]
   */
  open fun newSingleSIdeExpandable(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent
  ): ISingleSideExpandable {
    return DoubleSideExpandableHelper()
  }
  
  private var mIsRunning = false
  
  protected var mSideExpandable: ISingleSideExpandable? = null
    private set
  
  protected var mInitialRow = 0
    private set
  
  // 触摸点距离边界的值
  protected var mBoundaryDistance = 0
    private set
  
  // Down 时触摸的上边界还是下边界
  protected var mDownTopOrBottom = false
    private set
  
  override fun onDown(page: ICoursePage, item: ITouchItem, child: View, event: IPointerEvent) {
    check(!mIsRunning) { "不支持多指并发" }
    mIsRunning = true
    var side = mSideExpandable
    if (side == null) {
      side = newSingleSIdeExpandable(page, item, child, event)
      mSideExpandable = side
    }
    side.onMoveStart(page.course, item, child)
    val y = event.y.toInt()
    mDownTopOrBottom = y < (item.lp.constraintTop + item.lp.constraintBottom) / 2F
    resetInitialValue(page, item, child, y)
  }
  
  override fun onMove(page: ICoursePage, item: ITouchItem, child: View, x: Int, y: Int) {
    val side = mSideExpandable
    check(side != null) { "未初始化，mSideExpandable 变量为 null" }
    val course = page.course
    // y 是当前手指触摸值，需要跟 mBoundaryDistance 进行计算才能得出边界值
    // 所以在这个 centerY 没有设置对时，会出现在某个值左右移动时出现鬼畜的效果
    val centerY = child.y + child.height / 2
    if (mDownTopOrBottom) {
      // 说明初始时是触摸的上方
      if (y > centerY) {
        // 当前在下方移动
        side.onSingleMove(course, item, child, mInitialRow, y - mBoundaryDistance)
      } else {
        // 当前在上方移动
        side.onSingleMove(course, item, child, mInitialRow, y + mBoundaryDistance)
      }
    } else {
      // 说明初始时是触摸的下方
      if (y > centerY) {
        // 当前在下方移动
        side.onSingleMove(course, item, child, mInitialRow, y + mBoundaryDistance)
      } else {
        // 当前在上方移动
        side.onSingleMove(course, item, child, mInitialRow, y - mBoundaryDistance)
      }
    }
  }
  
  override fun onEventEnd(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent,
    isInLongPress: Boolean?,
    isCancel: Boolean
  ) {
    val side = mSideExpandable
    check(side != null) { "未初始化，mSideExpandable 变量为 null" }
    side.onMoveEnd(
      page.course,
      item,
      child,
      getIsMoveValid(page, item, child, event, isInLongPress, isCancel)
    )
    mSideExpandable = null
    mIsRunning = false
  }
  
  /**
   * 移动是否有效，如果返回 false 则将自动还原到最开始的状态
   */
  protected open fun getIsMoveValid(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent,
    isInLongPress: Boolean?,
    isCancel: Boolean
  ): Boolean {
    return true
  }
}