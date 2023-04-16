package com.mredrock.cyxbs.lib.course.item.touch.helper.expand

import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 双指放大缩小事务的帮助类
 *
 * todo 未实现
 *
 * @author 985892345
 * 2023/2/6 21:35
 */
class ExpandableItemHelper : ITouchItemHelper {
  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) {
  }
}