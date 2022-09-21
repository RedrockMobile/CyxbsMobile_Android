package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.transition.ICourseTransition
import com.ndhzs.netlayout.transition.ILayoutTransition.TransitionType.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/8 12:38
 */
abstract class CourseTransitionImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseScrollControlImpl(context, attrs, defStyleAttr, defStyleRes), ICourseTransition {
  
  companion object {
    // 下面这个是 cv 官方的 LayoutTransition
    val defaultChangeIn = ObjectAnimator.ofPropertyValuesHolder(
      PropertyValuesHolder.ofInt("left", 0, 1),
      PropertyValuesHolder.ofInt("top", 0, 1),
      PropertyValuesHolder.ofInt("right", 0, 1),
      PropertyValuesHolder.ofInt("bottom", 0, 1),
      PropertyValuesHolder.ofInt("scrollX", 0, 1),
      PropertyValuesHolder.ofInt("scrollY", 0, 1)
    )
    val defaultChangeOut = defaultChangeIn.clone()
    val defaultChange = defaultChangeIn.clone()
    val defaultFadeIn = ObjectAnimator.ofFloat(null, "alpha", 0f, 1f)
    val defaultFadeOut = ObjectAnimator.ofFloat(null, "alpha", 1f, 0f)
  }
  
  @CallSuper
  override fun onFinishInflate() {
    super.onFinishInflate()
    addAnimator(CHANGE_APPEARING, getChangingAppearingAnimator())
    addAnimator(CHANGE_DISAPPEARING, getChangingDisappearingAnimator())
    addAnimator(APPEARING, getAppearingAnimator())
    addAnimator(DISAPPEARING, getDisappearingAnimator())
    addAnimator(CHANGING, getChangingAnimator())
  }
  
  override fun getChangingAppearingAnimator(): Animator? {
    return defaultChangeIn.clone()
  }
  
  override fun getChangingDisappearingAnimator(): Animator? {
    return defaultChangeOut.clone()
  }
  
  override fun getAppearingAnimator(): Animator? {
    // 因为每次进入页面都会开启这个动画，很影响观感，所以置为 null
    return null
  }
  
  override fun getDisappearingAnimator(): Animator? {
    return defaultFadeOut.clone()
  }
  
  override fun getChangingAnimator(): Animator? {
    return defaultChange.clone()
  }
}