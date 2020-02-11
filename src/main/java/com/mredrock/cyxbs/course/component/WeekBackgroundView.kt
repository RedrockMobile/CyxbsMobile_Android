package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.course.R
import org.jetbrains.anko.dip

/**
 * @author Jon
 * @date 2019/11/16 16:12
 * description：当天的课程背景展示
 */
class WeekBackgroundView : View {

    var position: Int? = null
        set(value) {
            field = value
            invalidate()
        }


    private var foreground: Int = 0
    private var bottomBackground: Int = 0
    private var round: Float = 0F
    private var mElementGap: Int = 0
    private var mScheduleViewWidth: Int = 0
    private var mBasicElementWidth: Int = 0


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.WeekBackgroundView)
        foreground = typeArray.getColor(R.styleable.WeekBackgroundView_foreground, Color.parseColor("#000000"))
        mElementGap = typeArray.getDimensionPixelSize(R.styleable.WeekBackgroundView_backgroundElementGap, dip(2f))
        bottomBackground = typeArray.getColor(R.styleable.WeekBackgroundView_bottomBackground, Color.parseColor("#00000000"))
        round = typeArray.getDimensionPixelSize(R.styleable.WeekBackgroundView_round, context.dp2pxInt(8f)).toFloat()
        typeArray.recycle()
        backgroundPaint.apply {
            color = bottomBackground
            style = Paint.Style.FILL_AND_STROKE
        }
        foregroundPaint.apply {
            color = foreground
            style = Paint.Style.FILL_AND_STROKE
        }

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mScheduleViewWidth = measuredWidth
        mBasicElementWidth = (mScheduleViewWidth - mElementGap * 8) / 7
    }

    private val backgroundPaint = Paint().apply { isAntiAlias = true }
    private val backgroundRectF = RectF()

    private val foregroundPaint = Paint().apply { isAntiAlias = true }
    private val foregroundRectF = RectF()


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        position?.let { position ->
            val height = measuredHeight.toFloat()
            val pX: Float = mBasicElementWidth * position + mElementGap * (position + 1f)
            backgroundRectF.set(pX, measuredHeight / 2f, pX + mBasicElementWidth, height)
            canvas?.drawRect(backgroundRectF, backgroundPaint)
            foregroundRectF.set(pX, 0f, pX + mBasicElementWidth, height)
            canvas?.drawRoundRect(foregroundRectF, round, round, foregroundPaint)
        }
    }

    private fun Context.dp2pxInt(dpValue: Float) =
            (dpValue * resources.displayMetrics.density + 0.5f).toInt()
}
