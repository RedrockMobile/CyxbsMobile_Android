package com.mredrock.cyxbs.mine.util.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by roger on 2019/11/27
 */
class Stick : View {
    //默认宽高
    private val mWidth = dp2px(10f).toInt()
    private val mHeight = dp2px(2f).toInt()
    val color = Paint(Paint.ANTI_ALIAS_FLAG)


    private fun dp2px(dpValue: Float) =
            (dpValue * resources.displayMetrics.density + 0.5f)

    var progress = 0f
        set(value) {
            field = value
            invalidate()
        }

    var secondaryProgress = 0f
        set(value) {
            field = value
            invalidate()
        }

    constructor(ctx: Context) : this(ctx, null) {

    }

    constructor(ctx: Context, attr: AttributeSet?) : this(ctx, attr, 0) {

    }

    constructor(ctx: Context, attr: AttributeSet?, defStyleAttr: Int) : super(ctx, attr, defStyleAttr) {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.parseColor("#E1E6F0"))

        color.strokeWidth = height.toFloat()
        canvas?.drawLine(0f, height.toFloat() / 2, progress * width.toFloat(), height.toFloat() / 2, color)

    }

    //只需要给View指定一个默认的内部宽高(mWidth, mHeight)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = resolveSize(mWidth, widthMeasureSpec)
        val height = resolveSize(mHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}