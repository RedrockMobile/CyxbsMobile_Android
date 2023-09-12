package com.mredrock.cyxbs.noclass.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.noclass.R

/**
 * Created by yyfbe, Date on 2020/8/14.
 */
class SeekBarView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.SeekBarView)
        if (ta != null) {
            multiple = ta.getFloat(R.styleable.SeekBarView_progressMultiple, 0f)
            underColor = ta.getColor(R.styleable.SeekBarView_underColor, DEFAULT_UNDER_COLOR)
            progressColor = ta.getColor(R.styleable.SeekBarView_progressColor, DEFAULT_PROGRESS_COLOR)
            endRadius = ta.getFloat(R.styleable.SeekBarView_endRadius, DEFAULT_RADIUS)
        }
        ta?.recycle()
    }

    private var multiple = 0.5f
    private var underColor = DEFAULT_UNDER_COLOR
    private var progressColor = DEFAULT_PROGRESS_COLOR
    private var endRadius = 0f

    companion object {
        const val DEFAULT_UNDER_COLOR = 0xffE8F0FC.toInt()
        const val DEFAULT_PROGRESS_COLOR = 0xff6364F7.toInt()

        //dp
        const val DEFAULT_HEIGHT = 50
        const val DEFAULT_WIDTH = 200
        const val DEFAULT_RADIUS = 50f
    }

    private var progress: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var mWidth = 0f
    private var mHeight = 0f

    //相差多少
    private var last = 0f

    //底部颜色
    private val paintUnder = Paint().apply {
        color = underColor
        isAntiAlias = true
    }

    //滑动的颜色
    private val paintProgress = Paint().apply {
        color = progressColor
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            drawUnder(canvas)
            drawProgress(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.AT_MOST -> {
                setMeasuredDimension(
                    MeasureSpec.makeMeasureSpec(
                        DEFAULT_WIDTH,
                        MeasureSpec.EXACTLY
                    ),
                    heightMeasureSpec
                )
                mWidth = (measuredWidth - paddingStart - paddingEnd).toFloat()
            }

            else -> {
                mWidth = measuredWidth.toFloat()
            }
        }
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST -> {
                setMeasuredDimension(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(
                        DEFAULT_HEIGHT,
                        MeasureSpec.EXACTLY
                    )
                )
                mHeight = ((measuredHeight - paddingTop - paddingBottom).toFloat())
            }

            else -> {
                mHeight = measuredHeight.toFloat()
            }

        }
        last = mWidth * (1 - multiple)
    }

    private fun drawUnder(canvas: Canvas) {
        canvas.drawRoundRect(
            0f,
            0f,
            mWidth,
            mHeight,
            endRadius,
            endRadius,
            paintUnder
        )
    }

    private fun drawProgress(canvas: Canvas) {
        canvas.drawRoundRect(
            progress * last,
            0f,
            //左边距+progress所占倍数+progress*last
            multiple * mWidth + progress * last,
            mHeight,
            endRadius,
            endRadius,
            paintProgress
        )
    }

    fun doAnimation(begin: Float, stop: Float) {
        ObjectAnimator.ofFloat(this, "progress", begin, stop).start()
    }

    fun doMove(stop: Float) {
        progress = stop
        invalidate()
    }

}