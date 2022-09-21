package com.mredrock.cyxbs.lib.course.internal.view

import android.view.View
import android.view.animation.LayoutAnimationController

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/16 1:32
 */
interface IViewGroup : IView {
  
  fun setLayoutAnimation(controller: LayoutAnimationController?)
  
  fun getLayoutAnimation(): LayoutAnimationController?
  
  fun startLayoutAnimation()
  
  fun getIterable(): Iterable<View>
  
  fun getChildCount(): Int
  
  fun getChildAt(index: Int): View
  
  fun setClipChildren(clipChildren: Boolean)
  
  fun getClipChildren(): Boolean
}