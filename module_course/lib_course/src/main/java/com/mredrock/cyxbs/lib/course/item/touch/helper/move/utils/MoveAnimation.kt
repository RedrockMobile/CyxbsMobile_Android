package com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils

import android.animation.ValueAnimator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener

/**
 * 结束动画
 *
 * @author 985892345
 * 2023/2/20 12:59
 */
class MoveAnimation(
  private val time: Long,
  private val onChange: (fraction: Float) -> Unit
) {
  private val animator = ValueAnimator.ofFloat(0F, 1F)
  fun start(): MoveAnimation {
    animator.run {
      addUpdateListener {
        val fraction = it.animatedFraction
        onChange.invoke(fraction)
      }
      duration = time
      interpolator = OvershootInterpolator(0.6F) // 个人认为 0.6F 的回弹比较合适
      start()
    }
    return this
  }
  
  fun doOnEnd(onEnd: () -> Unit): MoveAnimation {
    animator.addListener(onEnd = { onEnd.invoke() })
    return this
  }
}