package com.xl.myapplication

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

/**
 * Created by yyfbe, Date on 2020/8/14.
 */
class Indicator(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    @SuppressLint("Recycle")
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.Indicator)
        if (ta != null) {
            multiple = ta.getFloat(R.styleable.Indicator_progressMultiple, 0f)
            underColor = ta.getColor(R.styleable.Indicator_underColor, DEFAULT_UNDER_COLOR)
            progressColor = ta.getColor(R.styleable.Indicator_underColor, DEFAULT_PROGRESS_COLOR)
            endRadius = ta.getFloat(R.styleable.Indicator_endRadius, DEFAULT_RADIUS)
        }
    }

    private var multiple = 0.5f
    private var underColor = DEFAULT_UNDER_COLOR
    private var progressColor = DEFAULT_PROGRESS_COLOR
    private var endRadius = 0f

    companion object {
        const val DEFAULT_UNDER_COLOR = 0xff97B7F0.toInt()
        const val DEFAULT_PROGRESS_COLOR = 0xff2923D2.toInt()
        const val DEFAULT_HEIGHT = 10
        const val DEFAULT_WIDTH = 30
        const val DEFAULT_RADIUS = 50f
    }

    private var progress: Float = 0f
        get() = field
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            drawUnder(canvas)
            drawProgress(canvas)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.AT_MOST -> {
                setMeasuredDimension(
                    MeasureSpec.makeMeasureSpec(
                        200,
                        MeasureSpec.EXACTLY
                    ),
                    heightMeasureSpec
                )
                mWidth = (measuredWidth - paddingStart - paddingEnd).toFloat()
            }
        }
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST -> {
                setMeasuredDimension(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(
                        50,
                        MeasureSpec.EXACTLY
                    )
                )
                mHeight = ((measuredHeight - paddingTop - paddingBottom).toFloat())
            }
        }
        last = mWidth * (1 - multiple)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun drawUnder(canvas: Canvas) {
        canvas.drawRoundRect(
            0f,
            0f,
            mWidth.toFloat(),
            mHeight.toFloat(),
            endRadius,
            endRadius,
            paintUnder
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun drawProgress(canvas: Canvas) {
        canvas.drawRoundRect(
            progress * last,
            0f,
            //左边距+progress所占倍数+progress*last
            multiple * mWidth + progress * last,
            mHeight.toFloat(),
            endRadius,
            endRadius,
            paintProgress
        )
    }

    fun doAnimation(start: Float, end: Float, _duration: Long) {
        ObjectAnimator.ofFloat(this, "progress", start, end).apply {
            duration = _duration
        }.start()
    }

}