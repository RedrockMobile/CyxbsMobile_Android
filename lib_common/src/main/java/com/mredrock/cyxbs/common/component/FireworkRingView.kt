package com.mredrock.cyxbs.common.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * Author: RayleighZ
 * Time: 2021-08-04 15:46
 * 仿twitter的点赞动画
 * 仅提供外部一圈的动效
 * 子类可通过：drawInnerItem方法绘制内部内容
 */
abstract class FireworkRingView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    abstract fun drawInnerView(canvas: Canvas)

    abstract val paint: Paint
    abstract val centerX: Float //圈圈动画在自己画布的x坐标
    abstract val centerY: Float //圈圈动画在自己画布的y坐标
    abstract val maxRingRadius: Float //圆圈动画的最大半径

    private var dotScale = 7f

    private var ringCurPercent = 0f

    private val ringRectF by lazy { RectF() }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
    }

    //绘制爆炸开始最初的圆形
    private fun drawCircle(canvas: Canvas, radius: Float, color: Int) {
        paint.color = color
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    //绘制爆炸一段时间之后的圆环
    private fun drawRing(canvas: Canvas, radius: Float, color: Int, percent: Float){
        paint.color = color
        paint.isAntiAlias = true
        paint.strokeWidth = 2 * radius * percent
        ringRectF.apply {
            top = centerY - radius
            left = centerX - radius
            right = centerX + radius
            bottom = centerY + radius
        }
        canvas.drawArc(ringRectF, 0f, 360f, false, paint)
    }

    private fun drawDotWithRing(canvas: Canvas, radius: Float, color: Int){
        val dotR = maxRingRadius / dotScale
        paint.color = color
        paint.isAntiAlias = true
        //计算圆环宽度，最小为0，随着动化进程发展逐渐变细
        val ringPercent = min(1f - ringCurPercent, 1f) * 0.2f
        val ringWidth = 2 * maxRingRadius * ringPercent

        paint.strokeWidth = ringWidth
        paint.style = Paint.Style.STROKE

        if (ringCurPercent <= 1f){
            canvas.drawArc(ringRectF, 0f, 360f, false, paint)
        }

        //绘制
    }

}