package com.mredrock.cyxbs.lib.course.fold

import android.animation.ValueAnimator
import com.mredrock.cyxbs.lib.course.view.course.base.AbstractCourseLayout

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/13 14:32
 */
interface OnFoldListener {
  /**
   * 折叠动画开始
   */
  fun onFoldStart(course: AbstractCourseLayout) {}
  
  /**
   * 折叠动画中
   * @param fraction [ValueAnimator.getAnimatedFraction]
   */
  fun onFolding(course: AbstractCourseLayout, fraction: Float) {}
  
  /**
   * 折叠动画结束
   */
  fun onFoldEnd(course: AbstractCourseLayout) {}
  
  /**
   * 折叠动画被取消
   */
  fun onFoldCancel(course: AbstractCourseLayout) {}
  
  /**
   * 不带动画的直接折叠
   */
  fun onFoldWithoutAnim(course: AbstractCourseLayout) {}
  
  /**
   * 展开动画开始
   */
  fun onUnfoldStart(course: AbstractCourseLayout) {}
  
  /**
   * 展开动画中
   * @param fraction [ValueAnimator.getAnimatedFraction]
   */
  fun onUnfolding(course: AbstractCourseLayout, fraction: Float) {}
  
  /**
   * 展开开动画结束
   */
  fun onUnfoldEnd(course: AbstractCourseLayout) {}
  
  /**
   * 展开动画被取消
   */
  fun onUnfoldCancel(course: AbstractCourseLayout) {}
  
  /**
   * 不带动画的直接展开
   */
  fun onUnfoldWithoutAnim(course: AbstractCourseLayout) {}
}