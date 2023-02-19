package com.mredrock.cyxbs.lib.course.helper.show

import android.graphics.Point
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.util.forEach
import com.mredrock.cyxbs.lib.course.item.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.ndhzs.netlayout.child.OnChildExistListener
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.pow

/**
 * ## 点击 View 实现 Q 弹动画的事件帮助类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 13:12
 */
open class CourseDownAnimDispatcher(
  val course: ICourseViewGroup
) : IPointerDispatcher {
  
  init {
    course.addChildExistListener(
      object : OnChildExistListener {
        override fun onChildViewAdded(parent: ViewGroup, child: View) {}
        override fun onChildViewRemoved(parent: ViewGroup, child: View) {
          mViewWithRawPointById.forEach { id, pair ->
            if (pair.first === child) {
              endAnim(child, pair.second.x, pair.second.y)
              mViewWithRawPointById.remove(id)
              return
            }
          }
        }
      }
    )
  }
  
  /**
   * 是否需要动画
   */
  open fun isNeedAnim(item: IItem, view: View): Boolean {
    return item is ILessonItem || item is IAffairItem
  }
  
  final override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    return false
  }
  
  final override fun getInterceptHandler(event: IPointerEvent, view: ViewGroup): IPointerTouchHandler? {
    return null
  }
  
  private val mViewWithRawPointById = SparseArray<Pair<View, Point>>()
  
  final override fun onDispatchTouchEvent(event: MotionEvent, view: ViewGroup) {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN,
      MotionEvent.ACTION_POINTER_DOWN -> {
        val index = event.actionIndex
        val id = event.getPointerId(index)
        val x = event.getX(index).toInt()
        val y = event.getY(index).toInt()
        val pair = course.findPairUnderByXY(x, y) ?: return
        if (isNeedAnim(pair.first, pair.second)) {
          startAnim(pair.second, x, y)
          changeView(pair.second, x, y, x, y)
          mViewWithRawPointById[id] = Pair(pair.second, Point(x, y))
        }
      }
      MotionEvent.ACTION_MOVE -> {
        for (index in 0 until event.pointerCount) {
          val id = event.getPointerId(index)
          val pair = mViewWithRawPointById[id]
          if (pair != null) {
            val x = event.getX(index).toInt()
            val y = event.getY(index).toInt()
            val child = pair.first
            val point = pair.second
            val l = child.left
            val r = l + child.width
            val t = child.top
            val b = t + child.height
            if (x in l .. r && y in t .. b) {
              changeView(child, point.x, point.y, x, y)
            } else {
              // 移动到 View 外面就取消动画
              endAnim(child, point.x, point.y)
              mViewWithRawPointById.remove(id)
            }
          }
        }
      }
      MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
        val index = event.actionIndex
        val id = event.getPointerId(index)
        val pair = mViewWithRawPointById[id]
        if (pair != null) {
          endAnim(pair.first, pair.second.x, pair.second.y)
          mViewWithRawPointById.remove(id)
        }
      }
      MotionEvent.ACTION_CANCEL -> {
        for (index in 0 until event.pointerCount) {
          val id = event.getPointerId(index)
          val pair = mViewWithRawPointById[id]
          if (pair != null) {
            endAnim(pair.first, pair.second.x, pair.second.y)
          }
        }
        mViewWithRawPointById.clear()
      }
    }
  }
  
  protected open fun startAnim(view: View, initialX: Int, initialY: Int) {
    view.animate()
      .scaleX(0.85F)
      .scaleY(0.85F)
      .setInterpolator { 1 - 1F / (1F + it).pow(6) }
      .start()
  }
  
  protected open fun changeView(view: View, initialX: Int, initialY: Int, nowX: Int, nowY: Int) {
    view.rotationX = -(nowY - initialY) / view.height.toFloat() * 180 // 上下翻转
    view.rotationY = (nowX - initialX) / view.width.toFloat() * 90 // 左右翻转
  }
  
  protected open fun endAnim(view: View, initialX: Int, initialY: Int) {
    view.animate()
      .scaleX(1F)
      .scaleY(1F)
      .rotationX(0F)
      .rotationY(0F)
      .setInterpolator(OvershootInterpolator)
      .start()
  }
  
  companion object {
    val OvershootInterpolator = OvershootInterpolator()
  }
}