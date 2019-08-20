package com.mredrock.cyxbs.freshman.view.widget.bubble

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

/**
 * Create by roger
 * on 2019/8/12
 */
class BubbleView : View {
    //一些属性
    private val list = mutableListOf<Bubble>()
    private var allBubbleCount = 20
    private val addBubbleOnce = 2
    private val addBubbleInterval = 100
    //刷新时间，不高于30ms，否则明显卡顿
    private val refreshTime = 30
    private val moveSpeed = 0.07
    private var startTime = System.currentTimeMillis()
    private var mPaintColor = Color.parseColor("#ffffff")
    private lateinit var paint: Paint
    //泡泡最大半径
    private var maxR = dp2px(1.4)

    private var mIsDrawing = false

    private val cWidth: Int = dp2px(50.toDouble())
    private val cHeight: Int = dp2px(50.toDouble())

    constructor(ctx: Context) : this(ctx, null) {

    }

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0) {
    }

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint.isAntiAlias
        paint.color = mPaintColor
        paint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawSomething(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)

        val expectWidth = if (widthMode == MeasureSpec.EXACTLY) sizeWidth else cWidth
        val expectHeight = if (heightMode == MeasureSpec.EXACTLY) sizeHeight else cHeight
        setMeasuredDimension(expectWidth, expectHeight)
    }

    private fun drawSomething(canvas: Canvas?) {

        val t = System.currentTimeMillis()
        //canvas 清屏
        val paintClear = Paint()
        paintClear.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        manageBubble(refreshTime * moveSpeed.toDouble(), canvas)
        if (mIsDrawing) {
            postInvalidateDelayed(max(refreshTime - (System.currentTimeMillis() - t), 0))
        }
    }

    private fun manageBubble(distance: Double, canvas: Canvas?) {
        val iter = list.iterator()
        while (iter.hasNext()) {
            val bubble = iter.next()
            if (bubble.isOut(0, 0, width, height)) {
                iter.remove()
            } else {
                bubble.move(distance)
            }
        }
        for (x in list) {
            drawBubble(canvas!!, x)
        }



        if (System.currentTimeMillis() - startTime > addBubbleInterval && list.size < allBubbleCount) {
            for (i in 0..addBubbleOnce) {
                if (list.size < allBubbleCount) {
                    list.add(Bubble(randD() * width / 2 + width / 4, height.toDouble(), randD() * maxR, (randD() * 140).toInt() + 20))
                }
            }
            startTime = System.currentTimeMillis()
        }

    }

    private fun drawBubble(canvas: Canvas, bubble: Bubble) {

        paint.strokeWidth = bubble.r.toFloat()
        paint.alpha = (getAlpha(bubble) * 255).toInt()
        canvas.drawCircle(bubble.x.toFloat(), bubble.y.toFloat(), bubble.r.toFloat(), paint)
    }

    private fun getAlpha(bubble: Bubble): Double {
        return bubble.y / height
    }

    private fun dp2px(value: Double): Int {
        val v = context.resources.displayMetrics.density
        return (v * value + 0.5f).toInt()
    }
    /**
     * 开始扩散
     */
    fun start() {
        mIsDrawing = true
        invalidate()
    }

    /**
     * 停止扩散
     */
    fun stop() {
        mIsDrawing = false
    }


}

private fun randD(): Double {
    return (0..1000).random().toDouble() / 1000
}