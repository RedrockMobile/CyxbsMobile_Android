package com.mredrock.cyxbs.lib.utils.extensions

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.annotation.FloatRange

import com.mredrock.cyxbs.lib.utils.R
import kotlin.math.pow

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/24 23:08
 */

fun View.gone(): View {
  visibility = View.GONE
  return this
}

fun View.invisible(): View {
  visibility = View.INVISIBLE
  return this
}

fun View.visible(): View {
  visibility = View.VISIBLE
  return this
}

/**
 * @param interval 毫秒为单位，点击间隔小于这个值监听事件无法生效
 * @param click 具体的点击事件
 */
fun View.setOnSingleClickListener(interval: Long = 500, click: (View) -> Unit) {
  setOnClickListener {
    val tag = getTag(R.id.utils_single_click_id) as? Long
    if (System.currentTimeMillis() - (tag ?: 0L) > interval) {
      click(it)
    }
    it.setTag(R.id.utils_single_click_id, System.currentTimeMillis())
  }
}

/**
 * @param interval 毫秒为单位，点击间隔小于这个值监听事件才能生效（默认为500毫秒）
 * @param click 具体的点击事件
 */
fun View.setOnDoubleClickListener(interval: Long = 500, click: (View) -> Unit) {
  setOnClickListener {
    val tag = getTag(R.id.utils_double_click_id) as? Long
    if (System.currentTimeMillis() - (tag ?: 0L) < interval) {
      click(it)
    }
    it.setTag(R.id.utils_double_click_id, System.currentTimeMillis())
  }
}

/**
 * 使控件像果冻一样的按下弹起的 Q弹动画
 *
 * @param scale 该按钮按下之后缩放到原来的多少倍
 * @param rate 按下按钮到达scale的速度，值越大越快到达scale倍数
 * @param onTouch 这里占用了onTouch监听，所以要是还想设置这个监听的话，可以通过这里来设置
 */
@SuppressLint("ClickableViewAccessibility")
fun View.pressToZoomOut(
  scale: Float = 0.85f,
  @FloatRange(from = 1.0, to = 10.0) rate: Float = 6f,
  returnValue:Boolean = false,
  onTouch: View.OnTouchListener? = null
) {
  var progressTag = false
  val animatorList = mutableListOf<ValueAnimator>()
  this.setOnTouchListener { v, event ->
    if (event.action == MotionEvent.ACTION_DOWN) {
      animatorList.forEach { it.cancel() }
      animatorList.clear()
      ValueAnimator.ofFloat(scaleX, scale).apply {
        setInterpolator { (1 - 1.0 / (1.0 + it).pow(rate.toDouble())).toFloat() }
        addUpdateListener {
          scaleX = it.animatedValue as Float
          scaleY = it.animatedValue as Float
        }
        animatorList.add(this)
      }.start()
      progressTag = true
    } else if (progressTag && (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_MOVE)) {
      animatorList.forEach { it.cancel() }
      animatorList.clear()
      ValueAnimator.ofFloat(scaleX, 1f).apply {
        setInterpolator { (1 - 1.0 / (1.0 + it).pow(rate.toDouble())).toFloat() }
        addUpdateListener {
          scaleX = it.animatedValue as Float
          scaleY = it.animatedValue as Float
        }
        animatorList.add(this)
      }.start()
      progressTag = false
    }
    onTouch?.onTouch(v, event)
    returnValue
  }
}