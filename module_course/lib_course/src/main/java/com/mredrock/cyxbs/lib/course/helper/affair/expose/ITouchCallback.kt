package com.mredrock.cyxbs.lib.course.helper.affair.expose

import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * 创建事务的手指滑动的事件回调
 *
 * @author 985892345
 * 2023/1/30 15:55
 */
interface ITouchCallback {
  
  /**
   * @param row 初始行
   * @param column 初始列
   */
  fun onLongPressed(pointerId: Int, row: Int, column: Int) {}
  
  /**
   * @param row 初始行
   * @param touchRow 当前触摸的值 (这个值并不一定是实际显示的值)
   * @param showRow 实际显示的行 (因为有边界值控制)
   */
  fun onTouchMove(
    pointerId: Int,
    row: Int,
    touchRow: Int,
    showRow: Int
  ) {
  }
  
  /**
   * 长按激活后的手指抬起或者事件被 CANCEL
   *
   * @param row 初始行
   * @param touchRow 当前触摸的值 (这个值并不一定是实际显示的值)
   * @param showRow 实际显示的行 (因为有边界值控制)
   * @param isCancel 这次事件是否完整，如果是 DOWN -> UP 的事件则是完整的事件
   */
  fun onTouchEnd(
    pointerId: Int,
    row: Int,
    touchRow: Int,
    showRow: Int,
    isCancel: Boolean
  ) {
  }
  
  /**
   * 显示 [ITouchAffairItem] 的回调
   *
   * [onLongPressed] 调用后会立马调用 [onShowTouchAffairItem]，但用户只是快速的点击并抬手，
   * 就不会回调 [onLongPressed] 而直接回调 [onShowTouchAffairItem]
   */
  fun onShowTouchAffairItem(course: ICourseViewGroup, item: ITouchAffairItem) {
  }
}