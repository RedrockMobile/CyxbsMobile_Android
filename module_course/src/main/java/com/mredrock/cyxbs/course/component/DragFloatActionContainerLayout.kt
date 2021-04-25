package com.mredrock.cyxbs.course.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import kotlin.math.abs

class DragFloatActionContainerLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private var parentHeight = 0
    private var parentWidth = 0
    private var lastX = 0
    private var lastY = 0

    private var downX = 0
    private var downY = 0
    private var upX = 0
    private var upY = 0

    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (background == null) {

        }
    }





    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()
        val parentView = (parent as? ViewGroup) ?: return false
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                run {
                    lastX = rawX
                    downX = lastX
                }
                run {
                    lastY = rawY
                    downY = lastY
                }
                //请求父控件不中断事件
                parentView.requestDisallowInterceptTouchEvent(true)
                /* 获取父控件的高度 */
                parentHeight = parentView.height
                //获取父控件的宽度
                parentWidth = parentView.width
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = rawX - lastX
                val dy = rawY - lastY
                var x = x + dx
                var y = y + dy
                //检测是否到达边缘 左上右下
                x = when {
                    x <= parentView.paddingStart + marginStart -> (parentView.paddingStart + marginStart).toFloat()
                    x >= parentWidth - parentView.paddingEnd - marginEnd - width -> (parentWidth - parentView.paddingEnd - marginEnd - width).toFloat()
                    else -> x
                }
                //控件距离底部的margin
                y =when {
                    y <= parentView.paddingTop + marginTop -> (parentView.paddingTop + marginTop).toFloat()
                    y >= parentHeight - parentView.paddingBottom - marginBottom - height -> (parentHeight - parentView.paddingBottom - marginBottom - height).toFloat()
                    else -> y
                }
                setX(x)
                setY(y)
                lastX = rawX
                lastY = rawY
            }
            MotionEvent.ACTION_UP -> {
                upX = event.rawX.toInt()
                upY = event.rawY.toInt()
                val distanceX = abs(abs(upX) - abs(downX))
                val distanceY = abs(abs(upY) - abs(downY))
                //当手指按下的事件跟手指抬起事件之间的距离小于10时执行点击事件
                if (distanceX.coerceAtLeast(distanceY) <= 10) {
                    performClick()
                }
                moveHide()
            }
        }
        //如果是拖拽则消s耗事件，否则正常传递即可。
        return true
    }

    private fun moveHide() {
        val viewGroup = (parent as? ViewGroup) ?: return
        val duration: Long = 500
        if (x + width/2 >= parentWidth / 2) {
            //靠右吸附
            animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(duration)
                    .x((parentWidth - viewGroup.paddingEnd - marginEnd - width).toFloat())
                    .start()
        } else {
            //靠左吸附
            animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(duration)
                    .x((viewGroup.paddingStart + marginStart).toFloat())
                    .start()
        }
    }
}