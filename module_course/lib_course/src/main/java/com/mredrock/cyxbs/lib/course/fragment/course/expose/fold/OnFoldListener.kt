package com.mredrock.cyxbs.lib.course.fragment.course.expose.fold

import android.animation.ValueAnimator
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup

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
  fun onFoldStart(course: ICourseViewGroup) {}
  
  /**
   * 折叠动画中
   * @param fraction [ValueAnimator.getAnimatedFraction]，0 -> 1 的进度值
   */
  fun onFolding(course: ICourseViewGroup, fraction: Float) {}
  
  /**
   * 折叠动画结束
   */
  fun onFoldEnd(course: ICourseViewGroup) {}
  
  /**
   * 折叠动画被取消
   */
  fun onFoldCancel(course: ICourseViewGroup) {}
  
  /**
   * 不带动画的直接折叠
   *
   * 不带动画时不会回调 [onFoldStart] 和 [onFoldEnd]
   */
  fun onFoldWithoutAnim(course: ICourseViewGroup) {}
  
  /**
   * 展开动画开始
   */
  fun onUnfoldStart(course: ICourseViewGroup) {}
  
  /**
   * 展开动画中
   * @param fraction [ValueAnimator.getAnimatedFraction]，0 -> 1 的进度值
   */
  fun onUnfolding(course: ICourseViewGroup, fraction: Float) {}
  
  /**
   * 展开动画结束
   */
  fun onUnfoldEnd(course: ICourseViewGroup) {}
  
  /**
   * 展开动画被取消
   */
  fun onUnfoldCancel(course: ICourseViewGroup) {}
  
  /**
   * 不带动画的直接展开
   *
   * 不带动画时不会回调 [onUnfoldStart] 和 [onUnfoldEnd]
   */
  fun onUnfoldWithoutAnim(course: ICourseViewGroup) {}
}