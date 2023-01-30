package com.mredrock.cyxbs.lib.course.helper.move.expose

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

/**
 * 支持长按移动的 item
 *
 * 如果你的 item 需要进行长按移动功能，需要实现该接口
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/8 7:46
 */
interface IMovableItem : IItem {
  
  val move: IMove
  
  /**
   * 父布局
   */
  val parent: ICourseViewGroup
  
  /**
   * 取消自身 View 的显示
   */
  fun hideView()
  
  /**
   * 显示 View
   */
  fun showView()
  
  /**
   * 得到用于移动的 View
   *
   * ## 注意
   * - 该方法返回的 View 可以不用返回 [IItem.initializeView]，返回其他 View 也是可行的，但你应该返回相同样子的 View
   */
  fun createMovableView(): View
}