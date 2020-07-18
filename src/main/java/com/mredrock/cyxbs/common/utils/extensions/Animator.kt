package com.mredrock.cyxbs.common.utils.extensions

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import androidx.annotation.FloatRange
import org.jetbrains.anko.sdk27.coroutines.onTouch
import kotlin.math.pow

/**
 * @author Jovines
 * create 2020-07-18 9:33 PM
 * description: 一些通用的动画
 */

/**
 * 使控件像果冻一样的按下弹起的Q弹动画
 * 可以作用到任意的控件上
 * @param scale 该按钮按下之后缩放到原来的多少倍
 * @param rate 按下按钮到达scale的速度，值越大越快到达scale倍数
 *
 * 注意：因为采用非侵入的写法，所以不能判断手指位置的坐标和不得不占用一些监听器
 *      如果你在代码中需要使用到onTouch监听，又想使用这个果冻动画可能需要自己复制代码过去
 *      因为这里有一些tag，不便于拆分
 */
fun View.pressToZoomOut( scale: Float = 0.85f,
                        @FloatRange(from = 1.0, to = 10.0) rate: Float = 6f) {
    var progressTag = false
    val animatorList = mutableListOf<ValueAnimator>()
    this.onTouch { v, event ->
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
    }
}