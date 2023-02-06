package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.os.Bundle
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.util.forEach
import androidx.core.view.isVisible
import com.mredrock.cyxbs.lib.course.fragment.page.expose.ICourseDefaultTouch
import com.mredrock.cyxbs.lib.course.helper.show.CourseDownAnimDispatcher
import com.mredrock.cyxbs.lib.course.helper.ScrollTouchHandler
import com.mredrock.cyxbs.lib.course.helper.item.ITouchItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 这里面写需要默认添加的触摸事件帮助类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 18:23
 */
abstract class CourseDefaultTouchImpl : AbstractCoursePageFragment(), ICourseDefaultTouch {
  
  override fun getDefaultPointerHandler(
    event: IPointerEvent,
    view: View
  ): IPointerTouchHandler? {
    return DefaultPointerHandler
  }
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    course.setDefaultHandler(this)
    val dispatcher = getCourseDownAnimDispatcher()
    if (dispatcher != null) {
      course.addPointerDispatcher(dispatcher)
    }
  }
  
  override fun getCourseDownAnimDispatcher(): CourseDownAnimDispatcher? {
    return CourseDownAnimDispatcher(course)
  }
  
  /**
   * 在没有 handler 处理时，会将事件分发给 [ITouchItem] 或者是 [ScrollTouchHandler] 处理
   */
  object DefaultPointerHandler : IPointerTouchHandler {
    
    private val mItemByPointerId = SparseArray<Pair<ITouchItem, View>>()
    
    override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
      val x = event.x.toInt()
      val y = event.y.toInt()
      when (event.action) {
        IPointerEvent.Action.DOWN -> {
          val course = view as ICourseViewGroup
          val pair = course.findPairUnderByXY(x, y)
          if (pair != null && pair.first is ITouchItem && pair.second.isVisible) {
            @Suppress("UNCHECKED_CAST")
            mItemByPointerId.put(event.pointerId, pair as Pair<ITouchItem, View>)
            val item = pair.first
            item.touchHelper.onDownTouchEvent(event, view, pair.second, item)
          } else {
            ScrollTouchHandler.onPointerTouchEvent(event, view)
          }
        }
        IPointerEvent.Action.UP -> {
          val pair = mItemByPointerId.get(event.pointerId)
          if (pair != null) {
            pair.first.touchHelper.onUpTouchEvent(event, view, pair.second, pair.first)
            mItemByPointerId.remove(event.pointerId)
          } else {
            ScrollTouchHandler.onPointerTouchEvent(event, view)
          }
        }
        else -> {
          val item = mItemByPointerId.get(event.pointerId)
          if (item != null) {
            /**
             * MOVE 和 CANCEL 交给 [onDispatchTouchEvent] 回调
             * 因为多根手指触摸同个 item 时，MOVE 事件应该只回调一次，而不是回调多次，
             * 这样可以方便处理多指同 item 问题
             */
          } else {
            ScrollTouchHandler.onPointerTouchEvent(event, view)
          }
        }
      }
    }
    
    override fun onDispatchTouchEvent(event: MotionEvent, view: ViewGroup) {
      super.onDispatchTouchEvent(event, view)
      when (event.action) {
        MotionEvent.ACTION_MOVE -> {
          mItemByPointerId.forEach { _, pair ->
            val item = pair.first
            item.touchHelper.onMoveTouchEvent(event, view, pair.second, item)
          }
        }
        MotionEvent.ACTION_CANCEL -> {
          mItemByPointerId.forEach { _, pair ->
            val item = pair.first
            item.touchHelper.onCancelTouchEvent(event, view, pair.second, item)
          }
          mItemByPointerId.clear()
        }
      }
    }
  }
}