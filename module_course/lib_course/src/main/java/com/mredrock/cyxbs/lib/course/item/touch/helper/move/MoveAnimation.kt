package com.mredrock.cyxbs.lib.course.item.touch.helper.move

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
  private val dx: Float,
  private val dy: Float,
  private val time: Long,
  private val onChange: (x: Float, y: Float, fraction: Float) -> Unit
) {
  private val animator = ValueAnimator.ofFloat(0F, 1F)
  fun start(): MoveAnimation {
    animator.run {
      addUpdateListener {
        val fraction = it.animatedFraction
        val x = dx * (1 - fraction)
        val y = dy * (1 - fraction)
        onChange.invoke(x, y, fraction)
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