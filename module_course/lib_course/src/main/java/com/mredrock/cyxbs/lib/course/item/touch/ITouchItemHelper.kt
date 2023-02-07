package com.mredrock.cyxbs.lib.course.item.touch

import android.view.View
import android.view.ViewGroup
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
   * @param parent 父布局
   * @param child [item] 对应的 View
   * @param item 触摸的 item
   */
  fun onPointerTouchEvent(event: IPointerEvent, parent: ViewGroup, child: View, item: ITouchItem)
}