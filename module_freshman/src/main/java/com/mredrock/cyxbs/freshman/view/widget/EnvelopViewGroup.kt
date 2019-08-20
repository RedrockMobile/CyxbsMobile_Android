package com.mredrock.cyxbs.freshman.view.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.LogUtils

/**
 * Create by roger
 * 哭了，为了调这个的比例尺，写了一些乱七八糟的参数
 * on 2019/8/9
 */
class EnvelopViewGroup : ViewGroup {

    private val radiusWidth = dp2px(70)
    private val radiusHeight = dp2px(25)

    private val duration: Long = 1500

    private val secDuration: Long = 1000
    //一堆比例，用来确定word和button的位置
    private val marginWidthRatioWord = 31.toDouble() / 340
    private val marginTopRatioWord = 42.toDouble() / 611
    private val widthRatioButton = 126.toDouble() / 318
    private val marginTopRatioButton = 519.toDouble() / 606
    private val cRatioButton = 44.toDouble() / 126

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
            val centerX = width.toDouble() / 2
            val centerY = height.toDouble() / 2
            var cl = 0
            var ct = 0
            var cr = 0
            var cb = 0
            when (i) {
                0 -> {
                    cl = (centerX - radiusWidth).toInt()
                    ct = (centerY - radiusHeight).toInt()
                    cr = (centerX + radiusWidth).toInt()
                    cb = (centerY + radiusHeight).toInt()
                }
                1 -> {
                    val mTop = height * marginTopRatioWord
                    val mLR = width * marginWidthRatioWord
                    cl = mLR.toInt()
                    cr = width - mLR.toInt()
                    ct = mTop.toInt()
                    val mRatio = cHeight.toDouble() / cWidth
                    cb = ((cr - cl) * mRatio + ct).toInt()
                }
                2 -> {
                    cl = (centerX - radiusWidth).toInt()
                    ct = (centerY - radiusHeight).toInt()
                    cr = (centerX + radiusWidth).toInt()
                    cb = (centerY + radiusHeight).toInt()
                }
                3 -> {
                    cl = (centerX - radiusWidth).toInt()
                    ct = (centerY - radiusHeight).toInt()
                    cr = cl + cWidth
                    cb = ct + cHeight
                }
                4 -> {
                    cr = (centerX + radiusWidth).toInt()
                    ct = (centerY - radiusHeight).toInt()
                    cl = cr - cWidth
                    cb = ct + cHeight
                }
                5 -> {
                    cl = (centerX - radiusWidth).toInt()
                    cb = (centerY + radiusHeight).toInt()
                    cr = cl + cWidth
                    ct = cb - cHeight
                }
                6 -> {
                    cr = (centerX + radiusWidth).toInt()
                    cb = (centerY + radiusHeight).toInt()
                    cl = cr - cWidth
                    ct = cb - cHeight
                }
                7 -> {
                    cl = (centerX - width * widthRatioButton / 2).toInt()
                    cr = (centerX + width * widthRatioButton / 2).toInt()
                    ct = (height * marginTopRatioButton).toInt()
                    cb = (width * widthRatioButton * cRatioButton + ct).toInt()
                }
            }
            childView.layout(cl, ct, cr, cb)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizeWidth, sizeHeight)

    }

    fun openAnimation() {

        val cCount = childCount
        var cWidth = 0
        var cHeight = 0
        for (i in 0 until cCount) {
            val childView = getChildAt(i)
            cWidth = childView.measuredWidth
            cHeight = childView.measuredHeight
            val centerX = width.toDouble() / 2
            val centerY = height.toDouble() / 2
            var cl = 0
            var ct = 0
            var cr = 0
            var cb = 0
            when (i) {
                0 -> {
                    val ratioX = width / cWidth.toDouble() * 0.98
                    val ratioY = height / cHeight.toDouble() * 0.99
                    childView.animate().scaleX(ratioX.toFloat()).scaleY(ratioY.toFloat()).setDuration(duration).start()

                }

                1 -> {
                    childView.animate().alpha(1F).setDuration(secDuration).setStartDelay(duration).start()
                }


                2 -> {
                    val ratioX = width.toDouble() / cWidth.toDouble() * 0.99
                    val ratioY = height.toDouble() / cHeight.toDouble() * 0.99
                    childView.animate().scaleX(ratioX.toFloat()).scaleY(ratioY.toFloat()).setDuration(duration).start()
                }


                3 -> {
                    cl = (centerX - radiusWidth).toInt()
                    ct = (centerY - radiusHeight).toInt()
                    cr = cl + cWidth
                    cb = ct + cHeight
                    childView.animate().translationX(-(cl).toFloat()).translationY(-(ct).toFloat()).setDuration(duration).start()
                }
                4 -> {
                    cr = (centerX + radiusWidth).toInt()
                    ct = (centerY - radiusHeight).toInt()
                    cl = cr - cWidth
                    cb = ct + cHeight
                    childView.animate().translationX((width - cr).toFloat()).translationY(-(ct).toFloat()).setDuration(duration).start()

                }
                5 -> {
                    cl = (centerX - radiusWidth).toInt()
                    cb = (centerY + radiusHeight).toInt()
                    cr = cl + cWidth
                    ct = cb - cHeight
                    childView.animate().translationX(-(cl).toFloat()).translationY((height - cb).toFloat()).setDuration(duration).start()


                }
                6 -> {
                    cr = (centerX + radiusWidth).toInt()
                    cb = (centerY + radiusHeight).toInt()
                    cl = cr - cWidth
                    ct = cb - cHeight
                    childView.animate().translationX((width - cr).toFloat()).translationY((height - cb).toFloat()).setDuration(duration).start()
                }
                7 -> {
                    childView.animate().alpha(1F).setDuration(secDuration).setStartDelay(duration).start()
                }


            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    fun dp2px(value: Int): Int {
        val v = context.resources.displayMetrics.density
        return (v * value + 0.5f).toInt()
    }
}
