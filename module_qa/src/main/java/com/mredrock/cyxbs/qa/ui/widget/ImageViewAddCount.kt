package com.mredrock.cyxbs.qa.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @Author: xgl
 * @ClassName: ImageViewAddCount
 * @Description:
 * @Date: 2020/11/19 12:48
 */
class ImageViewAddCount(context: Context?, attrs: AttributeSet?) :
    CircleImageView(context!!, attrs) {
    private var paint: Paint = Paint()
    private var textPaint: TextPaint? = null

    companion object {
        const val NO_POINT = 1
        const val ONLY_POINT = 2
        const val NUMBER_POINT = 3
    }

    private var isHaveMessage = false

    /*
    默认显示消息数
     */
    private var pointMode = NUMBER_POINT
    private var messageNumber = 0

    init {
        paint.apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#2923D2")
            isAntiAlias = true
        }
        textPaint = TextPaint()
        textPaint?.apply {
            style = Paint.Style.FILL
            color = 0xffffffff.toInt()
            textSize = 30F
            isAntiAlias = true
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isHaveMessage)
            return
        when (pointMode) {
            NO_POINT -> {
            }

            NUMBER_POINT -> {
                var textPaintWidth = 0.0
                if (messageNumber in 1..9) {
                    textPaintWidth = 1.5
                    canvas?.drawCircle(
                        (width - paddingRight * 1.5).toFloat(),
                        paddingTop * 1.5.toFloat(),
                        paddingRight.toFloat(),
                        paint
                    )
                } else if (messageNumber in 1..99) {
                    val rect = RectF()
                    rect.left = width - (paddingLeft * 2.5).toFloat()
                    rect.right = width.toFloat()
                    rect.bottom = (paddingTop * 2.3).toFloat()
                    rect.top = 14f
                    canvas?.drawRoundRect(rect, 28f, 28f, paint)
                    textPaintWidth = 1.2
                }
                var showText = ""
                if (messageNumber in 1..99)
                    showText = messageNumber.toString() + ""
                else if (messageNumber >= 100) {
                    showText = "99+"
                    textPaintWidth = 1.2
                }
                val textWidth = textPaint?.measureText(showText)
                val x = (width - paddingRight * textPaintWidth - textWidth!! / 2).toFloat()
                val y = (paddingTop * 1.8).toFloat()
                canvas?.drawText(showText, x, y, textPaint as Paint)
            }

            ONLY_POINT -> {
                canvas?.drawCircle(
                    (width - paddingRight).toFloat(),
                    paddingTop.toFloat(),
                    paddingRight.toFloat(),
                    paint
                )
            }
        }
    }

    fun setMessageBum(number: Int) {
        this.messageNumber = number
    }

    fun setHaveMessage(isHaveMessage: Boolean) {
        this.isHaveMessage = isHaveMessage
        invalidate()
    }

}