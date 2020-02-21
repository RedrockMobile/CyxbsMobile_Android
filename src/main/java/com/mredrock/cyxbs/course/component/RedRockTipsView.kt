package com.mredrock.cyxbs.course.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.mredrock.cyxbs.course.R
import kotlin.math.atan

/**
 * @author jon
 * @create 2020-02-21 1:28 PM
 *
 *
 * 描述:课表头部可拉动提示，别问为什么要写这个动态的东西
 *      问的话那还要从一只蝙蝠说起（闲的）
 */
internal class RedRockTipsView : View {

    private val paint: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint().apply {
            color = 0xff000000.toInt()
            isAntiAlias = true
        }
    }

    private val leftPath: Path by lazy(LazyThreadSafetyMode.NONE) {
        Path()
    }

    private val rightPath: Path by lazy(LazyThreadSafetyMode.NONE) {
        Path()
    }

    private var tipColor: Int = 0xff000000.toInt()

    var position = 0f
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
            context,
            attrs
    ) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.RedRockTipsView,
                R.attr.RedRockTipsViewStyle, 0)
        tipColor = typeArray.getColor(R.styleable.RedRockTipsView_tipsColor, 0xff000000.toInt())
        typeArray.recycle()
        init()
    }

    private fun init() {
        paint.color = tipColor
    }

    constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {

        init()
    }


    override fun onDraw(canvas: Canvas) {
        val heightSpan = height / 8f
        val add = ((height.toDouble() / width.toDouble()) * (heightSpan) * position).toFloat()
        val radian = atan((height.toFloat() / width.toFloat()) * position)
        val angle = -Math.toDegrees(radian.toDouble()).toFloat()
        val distance = (-(heightSpan * 2)) * position
        canvas.save()
        canvas.translate(0f, distance)
        canvas.rotate(angle, width / 2f, height / 2f)
        leftPath.moveTo(0f + 20, heightSpan * 4f)
        leftPath.quadTo(0f + 20, heightSpan * 3, 0f + 20 + heightSpan * 1f, heightSpan * 3)
        leftPath.lineTo((width.toFloat() / 2) + add, heightSpan * 3)
        leftPath.lineTo((width.toFloat() / 2) - add, heightSpan * 5)
        leftPath.lineTo(0f + 20 + heightSpan * 1f, heightSpan * 5)
        leftPath.quadTo(0f + 20, heightSpan * 5, 0f + 20, heightSpan * 4f)
        canvas.drawPath(leftPath, paint)
        canvas.restore()
        canvas.translate(0f, distance)
        canvas.rotate(-angle, width / 2f, height / 2f)
        rightPath.moveTo((width.toFloat() / 2) - add, heightSpan * 3)
        rightPath.lineTo(width - 20f - heightSpan * 1f, heightSpan * 3)
        rightPath.quadTo(width - 20f, heightSpan * 3, width - 20f, heightSpan * 4f)
        rightPath.lineTo(width - 20f, heightSpan * 4f)
        rightPath.quadTo(width - 20f, heightSpan * 5, width - 20f - heightSpan * 1f, heightSpan * 5)
        rightPath.lineTo((width.toFloat() / 2) + add, heightSpan * 5)
        canvas.drawPath(rightPath, paint)
    }


    fun topWayAnimation(): ValueAnimator {
        val animation = ValueAnimator.ofFloat(position, 1f)
        animSetting(animation)
        return animation
    }


    fun bottomWayAnimation(): ValueAnimator {
        val animation = ValueAnimator.ofFloat(position, -1f)
        animSetting(animation)
        return animation
    }

    fun centerWayAnimation(): ValueAnimator {
        val animation = ValueAnimator.ofFloat(position, 0f)
        animSetting(animation)
        return animation
    }

    private fun animSetting(animation: ValueAnimator) {
        animation.interpolator = DecelerateInterpolator()
        animation.duration = 500
        animation.addUpdateListener {
            position = it.animatedValue as Float
            invalidate()
        }
        animation.start()
    }

}