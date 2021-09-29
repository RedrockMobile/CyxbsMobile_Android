package com.mredrock.cyxbs.discover.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.discover.R

/**
 * Created by yyfbe, Date on 2020/8/14.
 */
class IndicatorView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
        View(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.IndicatorView)
        if (ta != null) {
            multiple = ta.getFloat(R.styleable.IndicatorView_progressMultiple, 0f)
            underColor = under_color
            progressColor = progress_color
            endRadius = ta.getFloat(R.styleable.IndicatorView_endRadius, DEFAULT_RADIUS)
        }
        ta?.recycle()
        SkinManager.addSkinUpdateListener(object :SkinManager.SkinUpdateListener{
            override fun onSkinUpdate() {
                if (ta != null){
                    underColor = under_color
                    progressColor = progress_color
                    invalidate()
                }
            }

        })
    }

    private var multiple = 0.5f
    private var underColor = under_color
    private var progressColor = progress_color
    private var endRadius = 0f

    companion object {
        val under_color
            get() = SkinManager.getColor("common_indicator_under_color", R.color.common_indicator_under_color)

        val progress_color
            get() = SkinManager.getColor("common_indicator_progress_color", R.color.common_indicator_progress_color)

        //dp
        const val DEFAULT_HEIGHT = 50
        const val DEFAULT_WIDTH = 200
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
    private val paintUnder
            get() = Paint().apply {
        color = underColor
        isAntiAlias = true
    }

    //滑动的颜色
    private val paintProgress
            get() = Paint().apply {
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
                mWidth.toFloat(),
                mHeight.toFloat(),
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
                mHeight.toFloat(),
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