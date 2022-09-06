package com.mredrock.cyxbs.lib.course.helper.move

import android.view.ViewConfiguration
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.utils.utils.VibratorUtil
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
  val course: ICourseViewGroup,
  dispatcher: MoveTouchDispatcher
) : MoveTouchDispatcher.BaseMoveTouchHandler(dispatcher) {
  
  private lateinit var mItem: IItem
  private var mPointerId = 0
  
  override var mIsInLongPress = false
  
  private val mLongPressRunnable = object : Runnable {
    private val mLongPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
    override fun run() {
      mIsInLongPress = true
      longPressStart()
    }
    
    fun start() {
      mIsInLongPress = false
      course.postDelayed(mLongPressTimeout, this)
    }
    
    fun cancel() {
      course.removeCallbacks(this)
      mIsInLongPress = false
    }
  }
  
  // 认定是在滑动的最小移动值，其中 ScrollView 拦截事件就与该值有关，不建议修改该值
  private var mTouchSlop = ViewConfiguration.get(course.getContext()).scaledTouchSlop
  
  private var mInitialX = 0
  private var mInitialY = 0
  private var mInitialRawX = 0
  private var mInitialRawY = 0
  
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
        mLongPressRunnable.start()
      }
      MOVE -> {
        if (mIsInLongPress) {
        
        } else {
          // 不处于长按状态时
          if (abs(rawX - mInitialRawX) > mTouchSlop || abs(rawY - mInitialRawY) > mTouchSlop) {
            // 使用 raw 来判断绝对的移动距离
            mLongPressRunnable.cancel()
          }
        }
      }
      UP, CANCEL -> {
        if (mIsInLongPress) {
        
        } else {
          mLongPressRunnable.cancel()
        }
      }
    }
  }
  
  private fun longPressStart() {
    // 禁止父布局拦截
    course.getParent().requestDisallowInterceptTouchEvent(true)
  
    // 让 item 绘制在其他 View 之上，再增加一些阴影效果
    mItem.view.translationZ = 3F + mPointerId
    
    VibratorUtil.start(course.getContext(), 36) // 长按触发来个震动提醒
  }
}