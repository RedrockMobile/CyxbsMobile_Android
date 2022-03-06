package com.mredrock.cyxbs.discover.map.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.graphics.drawable.toBitmap
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.discover.map.R

/**
 *@author zhangzhe
 *@date 2020/8/9
 *@description
 */

class FavoriteEditText : androidx.appcompat.widget.AppCompatEditText {

    private val clearBitmap: Bitmap by lazy {
        resources.getDrawable(R.drawable.map_ic_search_clear, null).toBitmap()
    }

    var maxStringLength: Int? = null
        set(value) {
            field = value
            notifyStringChange()
        }

    private var lastText = ""

    private var isEmpty = true

    private var offsetXHint = 0f
    private var offsetXClear = 300f
    private var textIsNotEmpty = false
        set(value) {
            if (value != field) {
                field = value
                if (value) {
                    hideHintAnimation()
                    isEmpty = false
                } else {
                    showHintAnimation()
                    isEmpty = true
                }
            }
        }

    private var hintWidth = 0f
    private val hintBaseline by lazy {
        val fontMetrics = paint.fontMetrics
        (fontMetrics.bottom - fontMetrics.top) / 2f - fontMetrics.bottom + height / 2
    }

    //显示文字长度
    private var hintString: String = ""
        set(value) {
            field = value
            invalidate()
            hintWidth = paint.measureText(hintString)
        }

    private lateinit var paint: Paint


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint.textSize = 35f
        paint.isAntiAlias = true
        hintString = "0/256"

    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        textIsNotEmpty = text?.length ?: 0 != 0
        notifyStringChange()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.translate((scrollX).toFloat(), 0f)
        //显示文本长度
        canvas?.drawText(hintString, -offsetXHint + (width - hintWidth) - context.dp2px(8f), hintBaseline, paint)
        //清空按钮
        canvas?.drawBitmap(clearBitmap, offsetXClear + width - clearBitmap.width - context.dp2px(8f), (height - clearBitmap.height) / 2f, Paint())
        canvas?.translate((-scrollX).toFloat(), 0f)

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEmpty) {
            return super.onTouchEvent(event)
        }
        val x = event?.x ?: 0f
        val y = event?.y ?: 0f
        //手动判断是否点击了清空按钮
        if (event?.action == MotionEvent.ACTION_DOWN
                && x >= width - clearBitmap.width - context.dp2px(8f)
                && x <= width - clearBitmap.width - context.dp2px(8f) + clearBitmap.width
                && y >= (height - clearBitmap.height) / 2f
                && y <= (height - clearBitmap.height) / 2f + clearBitmap.height) {
            setText("")
            return true
        }
        return super.onTouchEvent(event)
    }


    private fun showHintAnimation() {
        val anim1 = ValueAnimator.ofFloat(clearBitmap.width.toFloat(), 0f)
        anim1.repeatCount = 0
        anim1.repeatMode = ValueAnimator.REVERSE
        anim1.duration = 300
        anim1.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            offsetXHint = value
            postInvalidate()
        }
        anim1.start()
        //设置清空按钮的偏移动画，下同
        val anim2 = ValueAnimator.ofFloat(0f, 200f)
        anim2.repeatCount = 0
        anim2.repeatMode = ValueAnimator.REVERSE
        anim2.duration = 300
        anim2.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            offsetXClear = value
            postInvalidate()
        }
        anim2.start()
    }

    private fun hideHintAnimation() {
        val anim1 = ValueAnimator.ofFloat(0f, clearBitmap.width.toFloat())
        anim1.repeatCount = 0
        anim1.repeatMode = ValueAnimator.REVERSE
        anim1.duration = 300
        anim1.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            offsetXHint = value
            postInvalidate()
        }
        anim1.start()
        val anim2 = ValueAnimator.ofFloat(200f, 0f)
        anim2.repeatCount = 0
        anim2.repeatMode = ValueAnimator.REVERSE
        anim2.duration = 300
        anim2.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            offsetXClear = value
            postInvalidate()
        }
        anim2.start()
    }

    fun notifyStringChange() {
        if (text.toString().length > maxStringLength ?: 256) {
            setText(lastText)
            setSelection(text.toString().length)
        } else {
            lastText = text.toString()
        }
        if (maxStringLength != null) {
            hintString = text.toString().length.toString() + "/" + maxStringLength.toString()
        }
    }


}