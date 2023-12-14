package com.mredrock.cyxbs.lib.course.helper

import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 控制课表滚轴滚动的 Handler
 *
 * 这个类一般用于处理一些特殊情况，
 * 比如：有一个手指的 Handler 处理了事件，但因为长按前移动距离过大，不被视为长按操作，
 * 此时应该表现为滚轴移动。这个类就是用来干这种事情的
 *
 * ## 注意
 * - 需要能收到 UP、CANCEL 的回调。如果你想实现某一段特殊时期进行滚轴移动，请手动调用 scrollCourseBy()，而不是使用该 handler
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 13:58
 */
object ScrollTouchHandler : IPointerTouchHandler {
  
  // 采用队列，滚轴只能交给最先来的手指处理
  private val mIdDeque = ArrayDeque<Int>()
  private val mRawYById = SparseIntArray()
  
  override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
    if (view !is ICourseScrollControl) error("view 必须是 ${ICourseScrollControl::class.simpleName} 的实现类")
    when (event.action) {
      IPointerEvent.Action.DOWN -> mIdDeque.addLast(event.pointerId)
      IPointerEvent.Action.MOVE -> {
        if (!mIdDeque.contains(event.pointerId)) {
          // 此时说明没有经过 Down，直接从 Move 中添加进来的
          mIdDeque.addLast(event.pointerId)
        }
        val nowRawY = event.rawY.toInt()
        val first = mIdDeque.first()
        if (event.pointerId == first) {
          // 只把事件交给第一个手指处理
          val lastRawY = mRawYById.get(event.pointerId, nowRawY)
          view.scrollCourseBy(lastRawY - nowRawY)
        }
        mRawYById.put(event.pointerId, nowRawY)
      }
      IPointerEvent.Action.UP,
      IPointerEvent.Action.CANCEL -> {
        mIdDeque.remove(event.pointerId)
        mRawYById.delete(event.pointerId)
      }
    }
    when (event.event.actionMasked) {
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        mIdDeque.clear()
        mRawYById.clear()
      }
    }
  }
}