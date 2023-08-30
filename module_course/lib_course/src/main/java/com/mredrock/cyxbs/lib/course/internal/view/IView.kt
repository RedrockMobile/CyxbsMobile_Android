package com.mredrock.cyxbs.lib.course.internal.view

import android.content.Context
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewParent

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 19:32
 */
interface IView {
  
  fun getContext(): Context
  
  fun invalidate()
  
  fun post(action: Runnable?): Boolean
  
  fun postDelayed(delayInMillis: Long, action: Runnable?)
  
  fun postOnAnimation(action: Runnable?)
  
  fun removeCallbacks(action: Runnable): Boolean
  
  fun isAttachedToWindow(): Boolean
  
  fun addOnAttachStateChangeListener(listener: OnAttachStateChangeListener)
  
  fun removeOnAttachStateChangeListener(listener: OnAttachStateChangeListener)
  
  fun addOnLayoutChangeListener(l: View.OnLayoutChangeListener)
  
  fun removeOnLayoutChangeListener(l: View.OnLayoutChangeListener)
  
  fun getHeight(): Int
  
  fun getWidth(): Int
  
  fun getParent(): ViewParent
  
  fun getPaddingLeft(): Int
  
  fun getPaddingRight(): Int
  
  fun getPaddingStart(): Int
  
  fun getPaddingEnd(): Int
  
  fun getPaddingTop(): Int
  
  fun getPaddingBottom(): Int
}