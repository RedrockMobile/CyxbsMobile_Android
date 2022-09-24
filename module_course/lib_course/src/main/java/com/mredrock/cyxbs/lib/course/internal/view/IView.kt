package com.mredrock.cyxbs.lib.course.internal.view

import android.content.Context
import android.view.View
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
  
  fun addOnAttachStateChangeListener(listener: View.OnAttachStateChangeListener)
  
  fun getHeight(): Int
  
  fun getWidth(): Int
  
  fun getParent(): ViewParent
}