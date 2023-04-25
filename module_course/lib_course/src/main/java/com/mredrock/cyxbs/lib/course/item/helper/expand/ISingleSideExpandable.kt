package com.mredrock.cyxbs.lib.course.item.helper.expand

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * 单边扩展 Item 的帮助类接口
 *
 * ## 注意
 * - 内部通过修改 [IItem.lp] 的 topMargin、bottomMargin、startRow、endRow 实现扩展
 *
 * @author 985892345
 * 2023/4/19 21:05
 */
interface ISingleSideExpandable {
  
  /**
   * 是否已经做好了移动的准备
   */
  fun isMoveStart(): Boolean
  
  /**
   * 移动准备开始时的回调
   */
  fun onMoveStart(course: ICourseViewGroup, item: IItem, child: View)
  
  /**
   * 单边移动时的回调
   *
   * ## 注意
   * - 如果存在 translationY，请调用方自己减去该值
   *
   * @param row 固定的那一边的行数
   * @param y 另一边的 y 值，相对于 course
   */
  fun onSingleMove(course: ICourseViewGroup, item: IItem, child: View, row: Int, y: Int)
  
  /**
   * 移动结束时的回调
   *
   * @param isValid 移动是否有效，传入 false 则将还原至 [onMoveStart] 时的状态，当前状态可直接通过 [IItem.lp] 获得
   */
  fun onMoveEnd(course: ICourseViewGroup, item: IItem, child: View, isValid: Boolean)
  
  /**
   * 添加行改变的监听
   */
  fun addOnRowChangedListener(l: OnRowChangedListener)
  
  /**
   * 移除行改变监听
   */
  fun removeOnRowChangedListener(l: OnRowChangedListener)
  
  /**
   * [onMoveEnd] 中执行的动画监听
   */
  fun addAnimListener(l: OnAnimListener)
  
  /**
   * 移除动画结束监听
   */
  fun removeAnimListener(l: OnAnimListener)
  
  // 行改变监听
  interface OnRowChangedListener {
    /**
     * 行发生改变的监听
     */
    fun onChanged(
      course: ICourseViewGroup,
      item: IItem,
      child: View,
      oldTopRow: Int,
      oldBottomRow: Int,
      newTopRow: Int,
      newBottomRow: Int,
    ) {}
  }
  
  // 动画结束监听
  interface OnAnimListener {
    fun onAnimStart(side: ISingleSideExpandable, course: ICourseViewGroup, item: IItem, child: View) {}
    fun onAnimEnd(side: ISingleSideExpandable, course: ICourseViewGroup, item: IItem, child: View) {}
  }
}