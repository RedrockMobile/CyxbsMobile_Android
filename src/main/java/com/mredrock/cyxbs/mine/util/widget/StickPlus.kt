package com.mredrock.cyxbs.mine.util.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by roger on 2019/12/3
 * 主要是在inicator中使用
 */
class StickPlus : View {
    //默认宽高
    private val mWidth = dp2px(10f).toInt()
    private val mHeight = dp2px(2f).toInt()

    private lateinit var paint: Paint

    private val color = Color.parseColor("#1AD0F2")


    private fun dp2px(dpValue: Float) =
            (dpValue * resources.displayMetrics.density + 0.5f)

    constructor(ctx: Context) : this(ctx, null) {

    }

    constructor(ctx: Context, attr: AttributeSet?) : this(ctx, attr, 0) {

    }

    constructor(ctx: Context, attr: AttributeSet?, defStyleAttr: Int) : super(ctx, attr, defStyleAttr) {
        init()
    }
    private fun init() {
        setBackgroundColor(Color.TRANSPARENT);
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.strokeWidth = height.toFloat()
        paint.alpha = 133
        canvas?.drawLine(width * 0.1f, height.toFloat() / 2, width * 0.8f, height.toFloat() / 2, paint)

        canvas?.drawPoint(width * 0.9f, height.toFloat() / 2, paint)
        paint.alpha = 255
        canvas?.drawLine(width * 0.1f, height.toFloat() / 2, width * 0.5f, height.toFloat() / 2, paint)
    }

    //只需要给View指定一个默认的内部宽高(mWidth, mHeight)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight)
        }
    }
}