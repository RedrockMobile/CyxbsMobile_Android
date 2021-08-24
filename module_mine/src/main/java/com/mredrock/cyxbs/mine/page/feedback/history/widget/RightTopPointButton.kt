package com.mredrock.cyxbs.mine.page.feedback.history.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import com.afollestad.materialdialogs.utils.MDUtil.updatePadding
import com.mredrock.cyxbs.mine.R

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  13:10
 *@signature 我们不明前路，却已在路上
 */
class RightTopPointButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

     var pointVisible: Boolean = false
     var pointSize: Float = 0f
     var pointColor: Int = 0
     private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {

        initAttrs(attrs)

        initPaint()

    }

    private fun initAttrs(attrs: AttributeSet?) {
        val attributeSet =
            context.obtainStyledAttributes(attrs, R.styleable.RightTopPointButton)
        pointColor = attributeSet.getColor(R.styleable.RightTopPointButton_pointColor,
            Color.parseColor("#FF6262"))

        pointSize =
            attributeSet.getDimension(R.styleable.RightTopPointButton_pointSize, dpToFloat(2f))

        pointVisible = attributeSet.getBoolean(R.styleable.RightTopPointButton_pointVisible, true)

        //回收
        attributeSet.recycle()
    }

    private fun initPaint() {
        paint.apply {
            color = pointColor
            style = Paint.Style.FILL
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (pointVisible){
            drawPoint(canvas)
        }
    }

    private fun drawPoint(canvas: Canvas?) {
        val x: Float = width.toFloat()
        canvas?.drawCircle(x - pointSize, pointSize, pointSize, paint)
    }


    private fun dpToFloat(float: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, float,
        resources.displayMetrics
    )

}