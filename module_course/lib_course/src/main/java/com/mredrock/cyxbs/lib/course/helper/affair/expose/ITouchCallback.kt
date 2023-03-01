package com.mredrock.cyxbs.lib.course.helper.affair.expose

import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * 创建事务的手指滑动的事件回调
 *
 * @author 985892345
 * 2023/1/30 15:55
 */
interface ITouchCallback {
  
  fun onLongPressStart(pointerId: Int, initialRow: Int, initialColumn: Int) {}
  
  /**
   * @param initialRow 初始行
   * @param initialColumn 初始列
   * @param touchRow 当前触摸的值 (这个值并不一定是实际显示的值)
   * @param topRow 实际显示值 (因为有边界值控制)
   * @param bottomRow 实际显示值 (因为有边界值控制)
   */
  fun onTouchMove(
    pointerId: Int,
    initialRow: Int,
    initialColumn: Int,
    touchRow: Int,
    topRow: Int,
    bottomRow: Int
  ) {}
  
  /**
   * 长按激活后的手指抬起或者事件被 CANCEL
   *
   * @param initialRow 初始行
   * @param initialColumn 初始列
   * @param touchRow 当前触摸的值 (这个值并不一定是实际显示的值)
   * @param topRow 实际显示值 (因为有边界值控制)
   * @param bottomRow 实际显示值 (因为有边界值控制)
   */
  fun onTouchEnd(
    pointerId: Int,
    initialRow: Int,
    initialColumn: Int,
    touchRow: Int,
    topRow: Int,
    bottomRow: Int,
    isCancel: Boolean
  ) {}
  
  /**
   * 显示 [ITouchAffairItem] 的回调
   *
   * [onLongPressStart] 调用后会立马调用 [onShowTouchAffairItem]，但用户只是快速的点击并抬手，
   * 就不会回调 [onLongPressStart] 而直接回调 [onShowTouchAffairItem]
   */
  fun onShowTouchAffairItem(
    course: ICourseViewGroup,
    item: ITouchAffairItem,
    initialRow: Int
  ) {}
}