package com.mredrock.cyxbs.lib.course.helper.move

import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.helper.move.expose.IMovableItem
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.*
import kotlin.math.abs

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/4 20:40
 */
open class MoveTouchHandler(
  val item: IMovableItem,
  val view: View
) : MoveTouchDispatcher.BaseMoveTouchHandler() {
  
  protected open val mMoveItemWrapper = MoveItemWrapper(item)
  
  final override var mIsInLongPress = false
  
  private val mLongPressRunnable = Runnable {
    mIsInLongPress = true
    longPressStart()
  }
  
  private val mLongPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
  
  protected open fun startLongPress() {
    mIsInLongPress = false
    view.postDelayed(mLongPressRunnable, mLongPressTimeout)
  }
  
  protected open fun cancelLongPress() {
    view.removeCallbacks(mLongPressRunnable)
    mIsInLongPress = false
  }
  
  // 认定是在滑动的最小移动值，其中 ScrollView 拦截事件就与该值有关，不建议修改该值
  private var mTouchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
  
  protected var mInitialX = 0
    private set
  protected var mInitialY = 0
    private set
  protected var mInitialRawX = 0
    private set
  protected var mInitialRawY = 0
    private set
  
  protected var mLastMoveX = 0
    private set
  protected var mLastMoveY = 0
    private set
  protected var mLastMoveRawX = 0
    private set
  protected var mLastMoveRawY = 0
    private set
  
  protected var mPointerId = 0
    private set
  
  
  override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
    val x = event.x.toInt()
    val y = event.y.toInt()
    val rawX = event.rawX.toInt()
    val rawY = event.rawY.toInt()
    when (event.action) {
      DOWN -> {
        mInitialX = x
        mInitialY = y
        mInitialRawX = rawX
        mInitialRawY = rawY
        mLastMoveX = x
        mLastMoveY = y
        mLastMoveRawX = rawX
        mLastMoveRawY = rawY
        mPointerId = event.pointerId
        startLongPress()
      }
      MOVE -> {
        mLastMoveX = x
        mLastMoveY = y
        mLastMoveRawX = rawX
        mLastMoveRawY = rawY
        if (mIsInLongPress) {
        
        } else {
          // 不处于长按状态时
          if (abs(x - mLastMoveX) > mTouchSlop || abs(y - mLastMoveY) > mTouchSlop) {
            cancelLongPress()
          }
        }
      }
      UP, CANCEL -> {
        if (mIsInLongPress) {
        
        } else {
          cancelLongPress()
        }
      }
    }
  }
  
  protected open fun longPressStart() {
    mMoveItemWrapper.onLongPressStart()
  }
}