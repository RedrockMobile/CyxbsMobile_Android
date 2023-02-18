package com.mredrock.cyxbs.lib.course.item.touch.helper

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.FoldState
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.abs

/**
 * .
 *
 * @author 985892345
 * 2023/2/6 21:34
 */
class MovableItemHelper: ITouchItemHelper {
  
  private var mInitialX = 0
  private var mInitialY = 0
  
  private var mLastMoveX = 0
  private var mLastMoveY = 0
  
  private var mPointerId = MotionEvent.INVALID_POINTER_ID
  
  private var mIsInLongPress: Boolean? = null
  
  private var mCoursePage: ICoursePage? = null
  
  private var mTouchItem: ITouchItem? = null
  
  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    course: ICoursePage
  ) {
    when (event.action) {
      IPointerEvent.Action.DOWN -> {
        if (mPointerId == MotionEvent.INVALID_POINTER_ID) {
          mPointerId = event.pointerId
          mInitialX = event.x.toInt() // 重置
          mInitialY = event.y.toInt() // 重置
          mLastMoveX = mInitialX // 重置
          mLastMoveY = mInitialY // 重置
          mIsInLongPress = false // 重置
          mCoursePage = course
          mTouchItem = item
        }
      }
      IPointerEvent.Action.MOVE -> {
        if (event.pointerId == mPointerId) {
          val isInLongPress = mIsInLongPress
          if (isInLongPress != null) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            if (!isInLongPress) {
              val touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
              if (abs(x - mLastMoveX) > touchSlop || abs(y - mLastMoveY) > touchSlop) {
                mLongPressRunnable.cancel(parent)
                mIsInLongPress = null // 结束
              }
            } else {
              // 该分支表明已经触发长按
              
            }
            mLastMoveX = x
            mLastMoveY = y
          }
        }
      }
      IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
        if (event.pointerId == mPointerId) {
          mPointerId = MotionEvent.INVALID_POINTER_ID
          when (mIsInLongPress) {
            false -> {
              mLongPressRunnable.cancel(parent)
            }
            true -> {}
            null -> {}
          }
          mIsInLongPress = null
          mCoursePage = null
          mTouchItem = null
        }
      }
    }
  }
  
  private fun longPressStart() {
    mIsInLongPress = true
    unfoldNoonDuskIfNeed()
  }
  
  private fun unfoldNoonDuskIfNeed() {
    val course = mCoursePage ?: return
    val item = mTouchItem ?: return
    when (course.getNoonRowState()) {
      FoldState.FOLD,
      FoldState.FOLD_ANIM,
      FoldState.UNKNOWN -> {
        val tResult = course.compareNoonPeriod(item.lp.startRow)
        val bResult = course.compareNoonPeriod(item.lp.endRow)
        if (tResult * bResult <= 0) {
          course.unfoldNoon()
        }
      }
      else -> {}
    }
  }
  
  private val mLongPressRunnable = object : Runnable {
    override fun run() {
      longPressStart()
    }
    
    fun start(view: View) {
      view.postDelayed(this, ViewConfiguration.getLongPressTimeout().toLong())
    }
    
    fun cancel(view: View) {
      view.removeCallbacks(this)
    }
  }
}