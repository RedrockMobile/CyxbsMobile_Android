package com.mredrock.cyxbs.lib.course.helper.base

import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler

/**
 * ## 该 handler 有如下特点
 * - 能够接受到 DOWN 事件
 * - 如果长按处于延时内，则不会收到 MOVE 事件（即只有长按触发后才会收到 MOVE 事件）
 * - 如果长按处于延时内，任何终止长按的操作会收到 CANCEL 事件（包括长按延时内的手指抬起、长按延时内的移动距离过大）
 *
 * ## 总结
 * 只会有三种情况
 *
 * ### 1. DOWN -> MOVE -> UP
 * 正常情况，此时 MOVE 一定处于长按已经触发了的状态（因为长按未触发时的 MOVE 都被拦截了）
 *
 * ### 2. DOWN -> CANCEL
 * 长按触发失败的情况，包括：包括长按延时内的手指抬起、长按延时内的移动距离过大、被父 View 或者被前面的事件分发监听拦截
 *
 * ### 3. DOWN -> MOVE -> CANCEL
 * 正常情况中的特殊情况，此时 MOVE 也是处于长按已经触发了的状态。CANCEL 表示被父 View 或者被前面的事件分发监听拦截
 *
 * @author 985892345
 * 2023/2/3 10:39
 */
interface ILongPressTouchHandler : IPointerTouchHandler {
  /**
   * 长按激活时回调
   */
  fun onLongPressed()
}