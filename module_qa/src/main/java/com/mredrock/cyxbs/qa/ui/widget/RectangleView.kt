package com.mredrock.cyxbs.qa.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import com.mredrock.cyxbs.common.utils.LogUtils


class RectangleView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {
    private var paint: Paint = Paint()
    private var textPaint: TextPaint = TextPaint()
    private var imageCount: Int = 0

    private val path = Path()
    //是否显示蒙版
    private var isShowMask = false

    //蒙版色值
    private val maskColor = Color.parseColor("#80000000")

    private var bitmap : Bitmap? = null
    init {
        textPaint.apply {
            style = Paint.Style.FILL
            color = 0xffffffff.toInt()
            textSize = 100F
            isAntiAlias = true
        }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        bitmap = bm
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        bitmap = drawable?.toBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        bitmap = drawable?.toBitmap()
    }

    /**
     * 绘制圆角矩形图片
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (null == bitmap) {
            super.onDraw(canvas)
        } else {
            bitmap?.let {
                if (width > 20 && height > 20) {
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
                val rectSrc =
                        Rect(0, 0, it.width, it.height)
                val rectDest =
                        Rect(0, 0, width, height)
                paint.reset()
                drawable.draw(canvas)
                canvas.drawBitmap(it, rectSrc, rectDest, paint)
                if (isShowMask)
                    canvas.drawColor(maskColor)
                if (imageCount > 0) {
                    val showText = "+$imageCount"
                    val textWidth = textPaint.measureText(showText)
                    val x = width / 2 - textWidth / 2
                    val y = height / 2 + textPaint.fontMetrics.bottom
                    canvas.drawText(showText, x, y, textPaint)
                }
            }
        }

    }

    fun setShowMask() {
        isShowMask = true
    }

    @SuppressLint("ResourceType")
    fun setAddImageCount(imageCount: Int) {
        this.imageCount = imageCount
    }

}