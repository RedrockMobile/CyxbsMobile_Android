package com.mredrock.cyxbs.course.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.course.R

/**
 * @author Jon
 * @date 2019/11/16 16:12
 * description：当天的课程背景展示
 */
class WeekBackgroundView : View {

    private var position: Int = 5
        set(value) {
            field = value
            invalidate()
        }

    private var foreground: Int = 0
    private var bottomBackground: Int = 0
    private var round :Float= 0F



    constructor(context: Context) : super(context)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.WeekBackgroundView)
        foreground = typeArray.getColor(R.styleable.WeekBackgroundView_foreground, Color.parseColor("#000000"))
        bottomBackground = typeArray.getColor(R.styleable.WeekBackgroundView_bottomBackground, Color.parseColor("#00000000"))
        round = typeArray.getDimensionPixelSize(R.styleable.WeekBackgroundView_round, context.dp2pxInt(8f)).toFloat()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val with = measuredWidth / 7F
        val height = measuredHeight.toFloat()
        val pX: Float = with * position

        canvas?.drawRect(
            RectF(pX, measuredHeight / 2f, pX + with, height),
            Paint().apply {
                color = bottomBackground
                style = Paint.Style.FILL_AND_STROKE
            })

        canvas?.drawRoundRect(
            RectF(pX,0f , pX + with, height),
            round,
            round,
            Paint().apply {
                color = foreground
                style = Paint.Style.FILL_AND_STROKE
            })
    }

    private fun Context.dp2pxInt(dpValue: Float) =
        (dpValue * resources.displayMetrics.density + 0.5f).toInt()
}
