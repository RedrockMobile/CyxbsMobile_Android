package com.mredrock.cyxbs.lib.course.internal.view.scroll.base

import android.content.Context
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 14:21
 */
abstract class ScrollControlImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet?,
  defStyleAttr: Int = 0
) : AbstractCourseScrollView(context, attrs, defStyleAttr), ICourseScrollControl {
  
  private val mAbsoluteYById = SparseIntArray()
  
  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    when (ev.actionMasked) {
      MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
        val id = ev.getPointerId(ev.actionIndex)
        val y = ev.getY(ev.actionIndex).toInt()
        mAbsoluteYById.put(id, y)
      }
      MotionEvent.ACTION_MOVE -> {
        for (index in 0 until ev.pointerCount) {
          val id = ev.getPointerId(index)
          val y = ev.getY(index).toInt()
          mAbsoluteYById.put(id, y)
        }
      }
    }
    return super.dispatchTouchEvent(ev)
  }
  
  override fun scrollCourseBy(dy: Int) {
    scrollBy(0, dy)
  }
  
  override fun scrollCourseY(y: Int) {
    scrollY = y
  }
  
  override fun getScrollCourseY(): Int {
    return scrollY
  }
  
  override fun getAbsoluteY(pointerId: Int): Int {
    return mAbsoluteYById.get(pointerId)
  }
  
  override fun getScrollHeight(): Int {
    return height
  }
  
  override fun canCourseScrollVertically(direction: Int): Boolean {
    return canScrollVertically(direction)
  }
}