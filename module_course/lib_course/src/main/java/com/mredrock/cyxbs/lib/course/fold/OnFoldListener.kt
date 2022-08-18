package com.mredrock.cyxbs.lib.course.fold

import android.animation.ValueAnimator
import com.mredrock.cyxbs.lib.course.view.course.ICourseLayout

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
  fun onFoldStart(course: ICourseLayout) {}
  
  /**
   * 折叠动画中
   * @param fraction [ValueAnimator.getAnimatedFraction]
   */
  fun onFolding(course: ICourseLayout, fraction: Float) {}
  
  /**
   * 折叠动画结束
   */
  fun onFoldEnd(course: ICourseLayout) {}
  
  /**
   * 折叠动画被取消
   */
  fun onFoldCancel(course: ICourseLayout) {}
  
  /**
   * 不带动画的直接折叠
   */
  fun onFoldWithoutAnim(course: ICourseLayout) {}
  
  /**
   * 展开动画开始
   */
  fun onUnfoldStart(course: ICourseLayout) {}
  
  /**
   * 展开动画中
   * @param fraction [ValueAnimator.getAnimatedFraction]
   */
  fun onUnfolding(course: ICourseLayout, fraction: Float) {}
  
  /**
   * 展开开动画结束
   */
  fun onUnfoldEnd(course: ICourseLayout) {}
  
  /**
   * 展开动画被取消
   */
  fun onUnfoldCancel(course: ICourseLayout) {}
  
  /**
   * 不带动画的直接展开
   */
  fun onUnfoldWithoutAnim(course: ICourseLayout) {}
}