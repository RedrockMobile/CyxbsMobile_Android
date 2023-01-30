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
   * 得到用于移动的 View
   *
   * ## 注意
   * - 该方法返回的 View 要求父布局必须是 [ICourseViewGroup]
   */
  fun getMovableView(): View
}