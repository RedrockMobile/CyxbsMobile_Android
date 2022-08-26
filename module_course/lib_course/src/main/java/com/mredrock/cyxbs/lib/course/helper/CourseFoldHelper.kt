package com.mredrock.cyxbs.lib.course.helper

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.mredrock.cyxbs.lib.course.fragment.page.course.ICourseExtend
import com.mredrock.cyxbs.lib.course.fragment.page.fold.FoldState
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
  private val extend: ICourseExtend
) : IPointerDispatcher, OnSaveStateListener {
  
  private val mDownPointById = SparseArray<Point>()
  
  override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    val timeLineLeft = extend.getTimeLineStartWidth()
    val timeLineRight = extend.getTimeLineEndWidth()
    if (x !in timeLineLeft .. timeLineRight) return false
    val clickRange = 30 // 点击的范围
    when (event.action) {
      IPointerEvent.Action.DOWN -> {
        when (extend.getNoonRowState()) {
          FoldState.FOLD, FoldState.UNFOLD -> {
            val noonTop = extend.getNoonStartHeight()
            val noonBottom = extend.getNoonEndHeight()
            if (y in (noonTop - clickRange) .. (noonBottom + clickRange)) {
              var point = mDownPointById[event.pointerId]
              if (point !is NoonPoint) {
                point = NoonPoint(x, y)
                mDownPointById[event.pointerId] = point
              } else {
                point.x = x
                point.y = y
              }
              return true
            }
          }
          else -> {}
        }
        when (extend.getDuskRowState()) {
          FoldState.FOLD, FoldState.UNFOLD -> {
            val duskTop = extend.getDuskStartHeight()
            val duskBottom = extend.getDuskEndHeight()
            if (y in (duskTop - clickRange) .. (duskBottom + clickRange)) {
              var point = mDownPointById[event.pointerId]
              if (point !is DuskPoint) {
                point = DuskPoint(x, y)
                mDownPointById[event.pointerId] = point
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
        // 写在 UP 事件的原因在于点击事件本来就是在 UP 中触发的
        // 该方法虽然返回了 true，但 getInterceptHandler() 却一直返回 null，可以保证事件不会被 View 自身拦截
        when (mDownPointById[event.pointerId]) {
          is NoonPoint -> {
            when (extend.getNoonRowState()) {
              FoldState.FOLD -> extend.unfoldNoon()
              FoldState.UNFOLD -> extend.foldNoon()
              else -> {}
            }
          }
          is DuskPoint -> {
            when (extend.getDuskRowState()) {
              FoldState.FOLD -> extend.unfoldDusk()
              FoldState.UNFOLD -> extend.foldDusk()
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
  
  override fun onRestoreState(savedState: Parcelable?) {
    if (savedState is Bundle) {
      val noonState = savedState.getBoolean("noon_state")
      if (noonState) extend.foldNoonWithoutAnim() else extend.unfoldNoonWithoutAnim()
      val duskState = savedState.getBoolean("dusk_state")
      if (duskState) extend.foldDuskWithoutAnim() else extend.unfoldDuskWithoutAnim()
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
      "noon_state" to isFold(extend.getNoonRowState()),
      "dusk_state" to isFold(extend.getDuskRowState())
    )
  }
  
  companion object {
    fun attach(extend: ICourseExtend): CourseFoldHelper {
      val dispatcher = CourseFoldHelper(extend)
      // 处理事件分发
      extend.course.addPointerDispatcher(dispatcher)
      // 保存折叠状态
      extend.course.addSaveStateListener("课表折叠状态", dispatcher)
      return dispatcher
    }
  }
}