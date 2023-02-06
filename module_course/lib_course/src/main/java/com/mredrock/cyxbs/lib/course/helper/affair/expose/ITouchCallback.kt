package com.mredrock.cyxbs.lib.course.helper.affair.expose

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
    bottomRow: Int
  ) {}
}