package com.mredrock.cyxbs.lib.course.helper.item

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
  
  override fun onDownTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem
  ) {
    helpers.forEach {
      it.onDownTouchEvent(event, parent, child, item)
    }
  }
  
  override fun onMoveTouchEvent(
    event: MotionEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem
  ) {
    helpers.forEach {
      it.onMoveTouchEvent(event, parent, child, item)
    }
  }
  
  override fun onUpTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem
  ) {
    helpers.forEach {
      it.onUpTouchEvent(event, parent, child, item)
    }
  }
  
  override fun onCancelTouchEvent(
    event: MotionEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem
  ) {
    helpers.forEach {
      it.onCancelTouchEvent(event, parent, child, item)
    }
  }
}