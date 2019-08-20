package com.mredrock.cyxbs.freshman.view.widget

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator

/**
 * Create by roger
 * on 2019/8/9
 */
class RotateBanner : ViewGroup {
    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val cCount = childCount
        var cWidth = 0
        var cHeight = 0
        for (i in 0 until cCount) {
            val childView = getChildAt(i)
            cWidth = childView.measuredWidth
            cHeight = childView.measuredHeight
            var cl = 0
            var ct = 0
            var cr = 0
            var cb = 0
            when (i) {
                0 -> {
                    cl = 0
                    ct = 0
                    cr = width
                    cb = height
                }
                1 -> {
                    cl = (width / 1.277).toInt()
                    ct = (height / 2.34).toInt()
                    cr = cl + cWidth
                    cb = ct + cHeight
                }
                2 -> {
                    cl = (width / 5)
                    ct = (height / 3.5).toInt()
                    cr = cl + dp2px(100)
                    cb = ct + dp2px(100)
                }
                3 -> {
                    cl = (width / 1.8).toInt()
                    ct = (height / 3.5).toInt()
                    cr = cl + dp2px(100)
                    cb = ct + dp2px(100)

                }


            }
            childView.layout(cl, ct, cr, cb)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val ratio = 287.toDouble() / 376
        setMeasuredDimension(sizeWidth, (sizeWidth * ratio).toInt())
    }

    fun openAnimation() {

        val cCount = childCount
        for (i in 0 until cCount) {
            val childView = getChildAt(i)
            when (i) {
                1 -> {
                    val animator = ObjectAnimator.ofFloat(childView, View.ROTATION, 0.0f, 359.0f)
                    animator.duration = 2000
                    animator.repeatMode = ValueAnimator.RESTART
                    animator.repeatCount = ValueAnimator.INFINITE
                    animator.interpolator = LinearInterpolator() as TimeInterpolator?
                    animator.start()
                }
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    private fun dp2px(value: Int): Int {
        val v = context.resources.displayMetrics.density
        return (v * value + 0.5f).toInt()
    }
}