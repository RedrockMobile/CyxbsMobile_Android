package com.mredrock.cyxbs.lib.course.item.touch.helper

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * .
 *
 * @author 985892345
 * 2023/2/6 21:34
 */
class MovableItemHelper : ITouchItemHelper {
  
  override fun onDownTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem
  ) {
    TODO("Not yet implemented")
  }
  
  override fun onMoveTouchEvent(
    event: MotionEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem
  ) {
    TODO("Not yet implemented")
  }
  
  override fun onUpTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem
  ) {
    TODO("Not yet implemented")
  }
  
  override fun onCancelTouchEvent(
    event: MotionEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem
  ) {
    TODO("Not yet implemented")
  }
}