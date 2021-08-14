package com.mredrock.cyxbs.discover.map.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.discover.map.R

/**
 *@author zhangzhe
 *@date 2020/8/9
 *@description 带动画效果的搜索框自定义view，所有逻辑均在本类中
 */

class SearchEditText : androidx.appcompat.widget.AppCompatEditText {

    private val clearBitmap: Bitmap by lazy {
        resources.getDrawable(R.drawable.map_ic_search_clear, null).toBitmap()
    }
    private val searchBitmap: Bitmap by lazy {
        imageScale(resources.getDrawable(R.drawable.map_ic_search_edit_text_icon, null).toBitmap(), 40, 40)
    }

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

    //显示热词字符串，需要在代码中设置进去要显示的热词
    public var hintString: String = ""
        set(value) {
            field = if (value.length >= 18) value.take(18) + "..." else value
            invalidate()
            hintWidth = paint.measureText(field)
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
        val fontScale = context.resources.displayMetrics.scaledDensity
        paint.textSize = fontScale * 14 + 0.5f //设置字号为14sp
        paint.color = ContextCompat.getColor(context, R.color.map_search_text_color_hint)
        paint.isAntiAlias = true
        hintString = ""

    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        textIsNotEmpty = text?.length ?: 0 != 0
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        //搜索图标和搜索默认显示文字
        canvas?.translate((scrollX).toFloat(), 0f)

        canvas?.drawText(hintString, offsetXHint + (width - hintWidth) / 2f, hintBaseline, paint)
        canvas?.drawBitmap(searchBitmap, offsetXHint + (width - hintWidth) / 2 - searchBitmap.width - 10f, (height - searchBitmap.height) / 2f, Paint())
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
        val anim1 = ValueAnimator.ofFloat(width.toFloat(), 0f)
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
        val anim1 = ValueAnimator.ofFloat(0f, width.toFloat())
        anim1.repeatCount = 0
        anim1.repeatMode = ValueAnimator.REVERSE
        anim1.duration = 300
        anim1.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            offsetXHint = value
            postInvalidate()
        }
        anim1.start()
        val anim2 = ValueAnimator.ofFloat(300f, 0f)
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

    fun imageScale(bitmap: Bitmap, dst_w: Int, dst_h: Int): Bitmap {
        val srcW = bitmap.width
        val srcH = bitmap.height
        val scaleW = dst_w.toFloat() / srcW
        val scaleH = dst_h.toFloat() / srcH
        val matrix = Matrix()
        matrix.postScale(scaleW, scaleH)
        return Bitmap.createBitmap(bitmap, 0, 0, srcW, srcH, matrix, true)
    }

}