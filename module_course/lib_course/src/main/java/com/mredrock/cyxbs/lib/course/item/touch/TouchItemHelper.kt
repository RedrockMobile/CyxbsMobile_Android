package com.mredrock.cyxbs.lib.course.item.touch

import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 用于组合多个 [ITouchItemHelper]
 *
 * @author 985892345
 * 2023/2/6 15:29
 */
class TouchItemHelper(
  private vararg val helpers: ITouchItemHelper
) : ITouchItemHelper {
  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    course: ICoursePage
  ) {
    repeat(helpers.size) {
      val helper = helpers[it]
      helper.onPointerTouchEvent(event, parent, child, item, course)
    }
  }
}