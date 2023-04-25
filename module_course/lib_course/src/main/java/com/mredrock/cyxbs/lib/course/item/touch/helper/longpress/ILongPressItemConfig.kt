package com.mredrock.cyxbs.lib.course.item.touch.helper.longpress

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * [AbstractLongPressItemHelper] 的相关配置
 *
 * @author 985892345
 * 2023/4/19 13:06
 */
interface ILongPressItemConfig {
  
  /**
   * 当前 [event] 是否允许长按
   *
   * 只能有一个手指能允许触摸长按，直到该手指抬起或者 CANCEL 才会再次回调该方法
   *
   * @param child [item] 对应的 View
   */
  fun isLongPress(
    event: IPointerEvent,
    page: ICoursePage,
    item: ITouchItem,
    child: View
  ): Boolean
  
}