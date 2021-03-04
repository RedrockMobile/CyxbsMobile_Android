package com.mredrock.cyxbs.qa.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap


class RectangleView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {
    private var paint: Paint = Paint()
    private var textPaint: TextPaint = TextPaint()
    private var imageCount: Int = 0

    //是否显示蒙版
    private var isShowMask = false

    //蒙版色值
    private val maskColor = Color.parseColor("#80000000")

    init {
        textPaint.apply {
            style = Paint.Style.FILL
            color = 0xffffffff.toInt()
            textSize = 100F
            isAntiAlias = true
        }
    }

    /**
     * 绘制圆角矩形图片
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val drawable = drawable
        if (null != drawable) {
            if (width > 20 && height > 20) {
                val path = Path()
                path.moveTo(20f, 0f)
                path.lineTo((width - 20).toFloat(), 0f)
                path.quadTo(width.toFloat(), 0f, width.toFloat(), 20f)
                path.lineTo(width.toFloat(), (height - 20).toFloat())
                path.quadTo(width.toFloat(), height.toFloat(), (width - 20).toFloat(), height.toFloat())
                path.lineTo(20f, height.toFloat())
                path.quadTo(0f, height.toFloat(), 0f, (height - 20).toFloat())
                path.lineTo(0f, 20f)
                path.quadTo(0f, 0f, 20f, 0f)
                canvas.clipPath(path)
            }
            val bitmap = drawable.toBitmap()
            val rectSrc =
                    Rect(0, 0, bitmap.width, bitmap.height)
            val rectDest =
                    Rect(0, 0, width, height)
            paint.reset()
            canvas.drawBitmap(bitmap, rectSrc, rectDest, paint)
            if (isShowMask)
                canvas.drawColor(maskColor)
            if (imageCount > 0) {
                var showText = "+$imageCount"
                val textWidth = textPaint.measureText(showText)
                var x = width / 2 - textWidth / 2
                var y = height / 2 + textPaint.fontMetrics.bottom
                canvas.drawText(showText, x, y, textPaint)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    fun setShowMask() {
        isShowMask = true
        invalidate()
    }

    @SuppressLint("ResourceType")
    fun setAddImageCount(imageCount: Int) {
        this.imageCount = imageCount
        invalidate()
    }

}