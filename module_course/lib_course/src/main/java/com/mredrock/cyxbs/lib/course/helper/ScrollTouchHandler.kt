package com.mredrock.cyxbs.lib.course.helper

import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseScrollControl
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 是课表滚轴滚动的 Handler
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 13:58
 */
object ScrollTouchHandler : IPointerTouchHandler {
  
  private val mIdDeque = ArrayDeque<Int>()
  private val mRawYById = SparseIntArray()
  
  override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
    if (view !is ICourseScrollControl) error("view 必须是 ${ICourseScrollControl::class.simpleName} 的实现类")
    when (event.action) {
      IPointerEvent.Action.DOWN -> mIdDeque.addLast(event.pointerId)
      IPointerEvent.Action.MOVE -> {
        if (mIdDeque.isEmpty()) {
          // 此时说明没有经过 Down，直接从 Move 中添加进来的
          mIdDeque.add(event.pointerId)
        }
        val first = mIdDeque.first()
        if (event.pointerId == first) {
          // 只把事件交给第一个手指处理
          val nowRawY = event.rawY.toInt()
          val lastRawY = mRawYById.get(event.pointerId, nowRawY)
          view.scrollCourseBy(lastRawY - nowRawY)
          mRawYById.put(event.pointerId, nowRawY)
        }
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