package com.mredrock.cyxbs.affair.widge

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.transition.Transition
import androidx.transition.TransitionValues

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/13 21:34
 */
class TextViewTransition : Transition() {

  companion object {
    private const val KEY_TEXT_SIZE = "course:TextViewTransition:textSize"
    private const val KEY_TEXT_COLOR = "course:TextViewTransition:textSize"
  }

  override fun captureStartValues(transitionValues: TransitionValues) {
    saveValues(transitionValues)
  }

  override fun captureEndValues(transitionValues: TransitionValues) {
    saveValues(transitionValues)
  }

  private fun saveValues(transitionValues: TransitionValues) {
    val view = transitionValues.view
    if (view is TextView) {
      transitionValues.values[KEY_TEXT_SIZE] = view.textSize
      transitionValues.values[KEY_TEXT_COLOR] = view.currentTextColor
    }
  }

  override fun createAnimator(
    sceneRoot: ViewGroup,
    startValues: TransitionValues?,
    endValues: TransitionValues?
  ): Animator? {
    if (startValues == null || endValues == null) {
      return null
    }
    val textView = endValues.view
    if (textView !is TextView) return null
    val sTextSize = startValues.values.getValue(KEY_TEXT_SIZE) as Int
    val eTextSize = endValues.values.getValue(KEY_TEXT_SIZE) as Int
    val sTextColor = startValues.values.getValue(KEY_TEXT_COLOR) as Int
    val eTextColor = endValues.values.getValue(KEY_TEXT_COLOR) as Int
    AnimatorSet().playTogether(
      createTextSizeAnimator(
        textView, sTextSize.toFloat(),
        eTextSize.toFloat()
      )
    )
    AnimatorSet().playTogether(createTextColorAnimator(textView, sTextColor, eTextColor))
    return super.createAnimator(sceneRoot, startValues, endValues)
  }

  private fun createTextSizeAnimator(textView: TextView, start: Float, end: Float): Animator? {
    if (start == end) return null
    return ValueAnimator.ofFloat(start, end).apply {
      addUpdateListener {
        val value = it.animatedValue as Float
        textView.textSize = value
        Log.d(
          "ggg", "(TextViewTransition.kt:71)-->> " +
                  "value = $value"
        )
      }
      duration = this@TextViewTransition.duration
    }
  }

  private fun createTextColorAnimator(textView: TextView, start: Int, end: Int): Animator? {
    if (start == end) return null
    return ValueAnimator.ofArgb(start, end).apply {
      addUpdateListener {
        val value = it.animatedValue as Int
        textView.setTextColor(value)
      }
      duration = this@TextViewTransition.duration
    }
  }
}