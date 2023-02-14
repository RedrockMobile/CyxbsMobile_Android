package com.mredrock.cyxbs.declare.pages.detail.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.cardview.widget.CardView

/**
 * ... 一个带有百分数增长动画的自定义view
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/14
 * @Description:
 */
class IncreaseCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val mPaint = Paint()
    private var allPercent = 0
    private var dvPercent = 0
    private val animator: ValueAnimator

    init {
        //允许ViewGroup的onDraw生效
        setWillNotDraw(false)
        animator = ValueAnimator.ofInt(0, 100)
            .apply {
                duration = 1000//时长5s
                interpolator = LinearInterpolator()//线性插值器
                addUpdateListener {
                    val value = it.animatedValue.toString().toInt()
                    dvPercent = (value * 0.01 * allPercent).toInt()
                    invalidate()//更新占比
                }//监听
            }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, measuredWidth * dvPercent * 0.01f, measuredHeight.toFloat(), mPaint)
    }

    /**
     * 设置百分数
     */
    fun setPercent(percent: Int, color: Int) {
        mPaint.color = color
        this.allPercent = percent
        animator.start()
    }

    /**
     * 取消占比增长动画
     */
    fun cancelIncrease() {
        animator.cancel()
        allPercent = 0
        dvPercent = 0
        invalidate()
    }
}