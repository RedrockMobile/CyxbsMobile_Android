package com.mredrock.cyxbs.lib.course.item.touch

import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * [ITouchItem] 的帮助类
 *
 * ## 注意
 * - 存在多跟手指跟同一个 item 进行绑定的情况，因此 [onPointerTouchEvent] 会回调多次
 *
 * @author 985892345
 * 2023/2/6 13:52
 */
interface ITouchItemHelper {
  
  /**
   * ## 注意
   * 为了与子 View 的点击事件共存，该方法能接收到 DOWN 事件(因此肯定会收到 UP、CANCEL)，
   * 但接收不到 MOVE 中相邻移动距离小于 touchSlop 的事件。
   *
   * 如果移动距离一旦超过 touchSlop，就会拦截子 View 事件，然后交给该方法处理
   *
   * 当然，存在子 View 调用 requestDisallowInterceptTouchEvent(true)，
   * 此时因为该方法接受到 DOWN 事件，所以稍后一定会收到一个 CANCEL 事件
   *
   * @param parent 父布局
   * @param child [item] 对应的 View
   * @param item 触摸的 item
   */
  fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  )
  
  /**
   * 是否提前拦截
   *
   * 在 MOVE 中，如果同时满足以下三者的条件
   * - abs(x - mLastMoveX) <= touchSlop
   * - abs(y - mLastMoveY) <= touchSlop
   * - !isAdvanceIntercept()
   * 则该 MOVE 事件会被分配给子 View (如果从某时起其中一条不满足，则子 View 事件被取消，之后的事件都被分发给该 [ITouchItemHelper])
   *
   * 常用于需要长按才会处理的 [ITouchItemHelper] 中
   */
  fun isAdvanceIntercept(): Boolean = false
}