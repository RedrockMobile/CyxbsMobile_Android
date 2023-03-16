package com.mredrock.cyxbs.lib.course.helper.fold

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.mredrock.cyxbs.lib.course.fragment.course.ICourseBase
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.FoldState
import com.mredrock.cyxbs.lib.course.utils.getOrPut
import com.ndhzs.netlayout.save.OnSaveStateListener
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * ### 该类作用：
 * - 监听点击时间轴上中午和傍晚时间段的事件
 * - 保存折叠状态，在 Fragment 被回收再还原后能还原原始状态
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 19:24
 */
class CourseFoldHelper private constructor(
  private val course: ICourseBase
) : IPointerDispatcher, OnSaveStateListener {
  
  private val mDownPointById = SparseArray<Point>()
  
  override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    val timeLineLeft = course.getTimelineStartWidth()
    val timeLineRight = course.getTimelineEndWidth()
    if (x !in timeLineLeft..timeLineRight) return false
    val clickRange = 50 // 点击的范围
    when (event.action) {
      IPointerEvent.Action.DOWN -> {
        when (course.getNoonRowState()) {
          FoldState.FOLD, FoldState.UNFOLD -> {
            val noonTop = course.getNoonStartHeight()
            val noonBottom = course.getNoonEndHeight()
            if (y in (noonTop - clickRange)..(noonBottom + clickRange)) {
              val point = mDownPointById.getOrPut(event.pointerId) { Point(0, 0, false) }
              if (!point.isNoon) {
                point.isNoon = true
              }
              point.x = x
              point.y = y
              return true
            }
          }
          else -> {}
        }
        when (course.getDuskRowState()) {
          FoldState.FOLD, FoldState.UNFOLD -> {
            val duskTop = course.getDuskStartHeight()
            val duskBottom = course.getDuskEndHeight()
            if (y in (duskTop - clickRange)..(duskBottom + clickRange)) {
              val point = mDownPointById.getOrPut(event.pointerId) { Point(0, 0, false) }
              if (point.isNoon) {
                point.isNoon = false
              }
              point.x = x
              point.y = y
              return true
            }
          }
          else -> {}
        }
      }
      IPointerEvent.Action.UP -> {
        // 因为 getInterceptHandler 一直返回 null，所以会收到 UP 事件
        // 写在 UP 事件的原因在于点击事件本来就是在 UP 中触发的
        // 上面 DOWN 中虽然返回了 true，但 getInterceptHandler() 却一直返回 null，可以保证事件不会被 View 自身拦截
        when (mDownPointById[event.pointerId].isNoon) {
          true -> {
            when (course.getNoonRowState()) {
              FoldState.FOLD -> course.unfoldNoon()
              FoldState.UNFOLD -> course.foldNoon()
              else -> {}
            }
          }
          false -> {
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
  
  class Point(var x: Int, var y: Int, var isNoon: Boolean)
  
  override fun onRestoreState(savedState: Parcelable?) {
    if (savedState is Bundle) {
      val noonState = savedState.getBoolean("noon_state")
      if (noonState) course.foldNoonWithoutAnim() else course.unfoldNoonWithoutAnim()
      val duskState = savedState.getBoolean("dusk_state")
      if (duskState) course.foldDuskWithoutAnim() else course.unfoldDuskWithoutAnim()
    }
  }
  
  override fun onSaveState(): Parcelable {
    fun isFold(state: FoldState): Boolean {
      return when (state) {
        FoldState.FOLD, FoldState.FOLD_ANIM -> true
        FoldState.UNFOLD, FoldState.UNFOLD_ANIM -> false
        FoldState.UNKNOWN -> true
      }
    }
    return bundleOf(
      "noon_state" to isFold(course.getNoonRowState()),
      "dusk_state" to isFold(course.getDuskRowState())
    )
  }
  
  companion object {
    fun attach(course: ICourseBase): CourseFoldHelper {
      val dispatcher = CourseFoldHelper(course)
      // 处理事件分发
      course.course.addPointerDispatcher(dispatcher)
      // 保存折叠状态
      course.course.addSaveStateListener("课表折叠状态", dispatcher)
      return dispatcher
    }
  }
}