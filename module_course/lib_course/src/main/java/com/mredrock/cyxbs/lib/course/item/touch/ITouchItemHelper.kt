package com.mredrock.cyxbs.lib.course.item.touch

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * [ITouchItem] 的帮助类
 *
 * 将事件分成多个函数的目的是为了解决 [IPointerTouchHandler] 不好处理多指触摸同个 item 的问题
 *
 * @author 985892345
 * 2023/2/6 13:52
 */
interface ITouchItemHelper {
  
  /**
   * DOWN 事件
   *
   * 在多指触摸时，每根手指的 DOWN 都会回调该函数
   *
   * @param parent 父布局
   * @param child [item] 对应的 View
   * @param item 触摸的 item
   */
  fun onDownTouchEvent(event: IPointerEvent, parent: ViewGroup, child: View, item: ITouchItem)
  
  /**
   * MOVE 事件
   *
   * 多根手指同时移动时将统一分发给该函数，与原生的事件分发处理一致
   *
   * 使用方式：
   * ```
   * for (index in 0 until event.pointerCount) {
   *   val x = event.getX(index)
   *   val y = event.getY(index)
   *   val pointerId = event.getPointerId(index)
   * }
   * ```
   *
   * @param parent 父布局
   * @param child [item] 对应的 View
   * @param item 触摸的 item
   */
  fun onMoveTouchEvent(event: MotionEvent, parent: ViewGroup, child: View, item: ITouchItem)
  
  /**
   * UP 事件
   *
   * 在多指触摸时，每根手指的 UP 都会回调该函数
   *
   * @param parent 父布局
   * @param child [item] 对应的 View
   * @param item 触摸的 item
   */
  fun onUpTouchEvent(event: IPointerEvent, parent: ViewGroup, child: View, item: ITouchItem)
  
  /**
   * CANCEL 事件，用法与 MOVE 基本一致
   *
   * @param parent 父布局
   * @param child [item] 对应的 View
   * @param item 触摸的 item
   */
  fun onCancelTouchEvent(event: MotionEvent, parent: ViewGroup, child: View, item: ITouchItem)
}