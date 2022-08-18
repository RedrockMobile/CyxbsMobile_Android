package com.mredrock.cyxbs.lib.course.helper

import android.graphics.Point
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.util.forEach
import com.mredrock.cyxbs.lib.course.view.course.ICourseLayout
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.abs
import kotlin.math.pow

/**
 * ## 点击 View 实现 Q 弹动画的事件帮助类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 13:12
 */
abstract class CourseDownAnimDispatcher(
  val course: ICourseLayout
) : IPointerDispatcher {
  
  /**
   * 是否需要动画
   */
  abstract fun isNeedAnim(item: View): Boolean
  
  override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    return false
  }
  
  override fun getInterceptHandler(event: IPointerEvent, view: ViewGroup): IPointerTouchHandler? {
    return null
  }
  
  private var mTouchSlop = ViewConfiguration.get(course.getContext()).scaledTouchSlop
  private val mViewWithRawPointById = SparseArray<Pair<View, Point>>()
  
  override fun onDispatchTouchEvent(event: MotionEvent, view: ViewGroup) {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN,
      MotionEvent.ACTION_POINTER_DOWN -> {
        val index = event.actionIndex
        val id = event.getPointerId(index)
        val x = event.getX(index).toInt()
        val y = event.getY(index).toInt()
        val rawX = getRawX(index, event).toInt()
        val rawY = getRawY(index, event).toInt()
        val child = course.findItemUnderByXY(x, y) ?: return
        if (isNeedAnim(child)) {
          mViewWithRawPointById[id] = Pair(child, Point(rawX, rawY))
          startAnim(child)
        }
      }
      MotionEvent.ACTION_MOVE -> {
        for (index in 0 until event.pointerCount) {
          val id = event.getPointerId(index)
          val pair = mViewWithRawPointById[id]
          if (pair != null) {
            val child = pair.first
            val point = pair.second
            val rawX = getRawX(index, event)
            val rawY = getRawY(index, event)
            if (abs(rawX - point.x) > mTouchSlop
              || abs(rawY - point.y) > mTouchSlop
            ) {
              recoverAnim(child)
              mViewWithRawPointById.remove(id)
            }
          }
        }
      }
      MotionEvent.ACTION_POINTER_UP,
      MotionEvent.ACTION_UP -> {
        val index = event.actionIndex
        val id = event.getPointerId(index)
        val pair = mViewWithRawPointById[id]
        if (pair != null) {
          val child = pair.first
          recoverAnim(child)
          mViewWithRawPointById.remove(id)
        }
      }
      MotionEvent.ACTION_CANCEL -> {
        mViewWithRawPointById.forEach { _, value ->
          recoverAnim(value.first)
        }
        mViewWithRawPointById.clear()
      }
    }
  }
  
  /**
   * 实现按下后的 Q 弹动画
   */
  private fun startAnim(view: View) {
    view.animate()
      .scaleX(0.85F)
      .scaleY(0.85F)
      .setInterpolator { 1 - 1F / (1F + it).pow(6) }
      .start()
  }
  
  private fun recoverAnim(view: View) {
    view.animate().cancel()
    view.animate()
      .scaleX(1F)
      .scaleY(1F)
      .setInterpolator { 1 - 1F / (1F + it).pow(6) }
      .start()
  }
  
  private fun getRawX(pointerIndex: Int, event: MotionEvent): Float {
    return event.getX(pointerIndex) - event.x + event.rawX
  }
  
  private fun getRawY(pointerIndex: Int, event: MotionEvent): Float {
    return event.getY(pointerIndex) - event.y + event.rawY
  }
}