package com.mredrock.cyxbs.lib.course.internal.view.scroll.base

import android.content.Context
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl
import com.mredrock.cyxbs.lib.course.utils.forEachReversed

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
  
  final override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
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
  
  final override fun scrollCourseBy(dy: Int) {
    scrollBy(0, dy)
  }
  
  final override fun scrollCourseY(y: Int) {
    scrollY = y
  }
  
  final override fun getScrollCourseY(): Int {
    return scrollY
  }
  
  final override fun getAbsoluteY(pointerId: Int): Int {
    return mAbsoluteYById.get(pointerId)
  }
  
  final override fun getScrollOuterHeight(): Int {
    return height
  }
  
  final override fun getScrollInnerHeight(): Int {
    return getChildAt(0).height
  }
  
  final override fun canCourseScrollVertically(direction: Int): Boolean {
    return canScrollVertically(direction)
  }
  
  final override fun addOnScrollYChanged(l: ICourseScrollControl.OnScrollYChangedListener) {
    mOnScrollYChangedListeners.add(l)
  }
  
  final override fun removeOnScrollYChanged(l: ICourseScrollControl.OnScrollYChangedListener) {
    mOnScrollYChangedListeners.remove(l)
  }
  
  private val mOnScrollYChangedListeners = arrayListOf<ICourseScrollControl.OnScrollYChangedListener>()
  
  final override fun onScrollChanged(scrollX: Int, scrollY: Int, oldScrollX: Int, oldtScrollY: Int) {
    super.onScrollChanged(scrollX, scrollY, oldScrollX, oldtScrollY)
    mOnScrollYChangedListeners.forEachReversed {
      it.onScrollYChanged(oldtScrollY, scrollY)
    }
  }
}