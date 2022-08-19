package com.mredrock.cyxbs.lib.course.helper

import android.util.SparseArray
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.internal.fold.FoldState
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseLayout
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * ## 点击时间轴上中午和傍晚时间段的事件分发者
 *
 * ### 该类作用：
 * - 监听点击时间轴上中午和傍晚时间段的事件；
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 19:24
 */
class FoldPointerDispatcher(
  val course: ICourseLayout
) : IPointerDispatcher {
  
  private val mPointById = SparseArray<Point>()
  
  override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    val timeLineLeft = course.getTimeLineStartWidth()
    val timeLineRight = course.getTimeLineEndWidth()
    if (x !in timeLineLeft .. timeLineRight) return false
    val clickRange = 30 // 点击的范围
    when (event.action) {
      IPointerEvent.Action.DOWN -> {
        when (course.getNoonRowState()) {
          FoldState.FOLD, FoldState.UNFOLD -> {
            val noonTop = course.getNoonStartHeight()
            val noonBottom = course.getNoonEndHeight()
            if (y in (noonTop - clickRange) .. (noonBottom + clickRange)) {
              var point = mPointById[event.pointerId]
              if (point !is NoonPoint) {
                point = NoonPoint(x, y)
                mPointById[event.pointerId] = point
              } else {
                point.x = x
                point.y = y
              }
              return true
            }
          }
          else -> {}
        }
        when (course.getDuskRowState()) {
          FoldState.FOLD, FoldState.UNFOLD -> {
            val duskTop = course.getDuskStartHeight()
            val duskBottom = course.getDuskEndHeight()
            if (y in (duskTop - clickRange) .. (duskBottom + clickRange)) {
              var point = mPointById[event.pointerId]
              if (point !is DuskPoint) {
                point = DuskPoint(x, y)
                mPointById[event.pointerId] = point
              } else {
                point.x = x
                point.y = y
              }
              return true
            }
          }
          else -> {}
        }
      }
      IPointerEvent.Action.UP -> {
        // 因为 getInterceptHandler 一直返回 null，所以会收到 UP 事件
        when (mPointById[event.pointerId]) {
          is NoonPoint -> {
            when (course.getNoonRowState()) {
              FoldState.FOLD -> course.unfoldNoon()
              FoldState.UNFOLD -> course.foldNoon()
              else -> {}
            }
          }
          is DuskPoint -> {
            when (course.getDuskRowState()) {
              FoldState.FOLD -> course.unfoldDusk()
              FoldState.UNFOLD -> course.foldDusk()
              else -> {}
            }
          }
        }
      }
      else -> {}
    }
    return false
  }
  
  override fun getInterceptHandler(event: IPointerEvent, view: ViewGroup): IPointerTouchHandler? {
    return null
  }
  
  sealed class Point(var x: Int, var y: Int)
  private class NoonPoint(x: Int, y: Int) : Point(x, y)
  private class DuskPoint(x: Int, y: Int) : Point(x, y)
}