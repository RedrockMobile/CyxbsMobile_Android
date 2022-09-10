package com.mredrock.cyxbs.lib.course.internal.transition

import android.animation.Animator
import com.ndhzs.netlayout.transition.ILayoutTransition

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/9 21:50
 */
interface ICourseTransition : ILayoutTransition {
  fun getChangingAppearingAnimator(): Animator?
  fun getChangingDisappearingAnimator(): Animator?
  fun getAppearingAnimator(): Animator?
  fun getDisappearingAnimator(): Animator?
  fun getChangingAnimator(): Animator?
}