package com.mredrock.cyxbs.lib.course.helper.move

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import com.mredrock.cyxbs.lib.course.helper.move.expose.IMovableItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.*

/**
 * TODO 长按移动待完成中
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/4 19:58
 */
class MoveTouchDispatcher private constructor(
  val course: ICourseViewGroup,
  val handle: IMovableItem.(View) -> MoveTouchHandler?
): IPointerDispatcher {
  
  companion object {
    fun attach(
      course: ICourseViewGroup,
      handle: IMovableItem.(View) -> MoveTouchHandler?
    ): MoveTouchDispatcher {
      return MoveTouchDispatcher(course, handle).also {
        course.addPointerDispatcher(it)
      }
    }
  }
  
  private val mHandlerById = SparseArrayCompat<MoveTouchHandler>()
  
  override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    when (event.action) {
      DOWN -> {
        val pair = course.findPairUnderByXY(x, y) ?: return false
        val item = pair.first
        if (item is IMovableItem) {
          val child = pair.second
          if (child.isInvisible || child.isGone) return false
          val handler = handle.invoke(item, child)
          if (handler != null) {
            mHandlerById.put(event.pointerId, handler)
            return true
          }
        }
      }
      else -> {}
    }
    return false
  }
  
  override fun getInterceptHandler(event: IPointerEvent, view: ViewGroup): IPointerTouchHandler? {
    val handler = mHandlerById[event.pointerId]
    if (handler != null) {
      // 把完整的事件分发给 handler
      handler.onPointerTouchEvent(event, view)
      return if (handler.mIsInLongPress) handler else null
    }
    return null
  }
  
  override fun onDispatchTouchEvent(event: MotionEvent, view: ViewGroup) {
    super.onDispatchTouchEvent(event, view)
    when (event.actionMasked) {
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mHandlerById.clear()
    }
  }
  
  abstract class BaseMoveTouchHandler : IPointerTouchHandler {
  
    /**
     * 是否处于长按激活状态
     *
     * 只有在长按激活后才会收到触摸事件
     */
    abstract val mIsInLongPress: Boolean
  }
}