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

    fun setShowMask() {
        isShowMask = true
        invalidate()
    }

    /**
     * 绘制圆角矩形图片
     */
    override fun onDraw(canvas: Canvas) {
        val drawable = drawable
        if (null != drawable) {
            val bitmap = drawable.toBitmap()
            val b = getRoundBitmap(bitmap, 20)
            val rectSrc =
                    Rect(0, 0, b.width, b.height)
            val rectDest =
                    Rect(0, 0, width, height)
            paint.reset()
            canvas.drawBitmap(b, rectSrc, rectDest, paint)
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

    /**
     * 获取圆角矩形图片
     * @param bitmap
     * @param roundPx 设置弧度.
     */
    private fun getRoundBitmap(bitmap: Bitmap, roundPx: Int): Bitmap {
        val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val rect =
                Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx.toFloat(), roundPx.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        if (isShowMask) {
            canvas.drawColor(maskColor)
        }
        return output
    }

    @SuppressLint("ResourceType")
    fun setAddImageCount(imageCount: Int) {
        this.imageCount = imageCount
        invalidate()
    }

}