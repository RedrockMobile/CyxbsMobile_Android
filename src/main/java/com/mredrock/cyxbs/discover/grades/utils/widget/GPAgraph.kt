package com.mredrock.cyxbs.discover.grades.utils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.mredrock.cyxbs.discover.grades.utils.extension.dp2px
import org.jetbrains.anko.backgroundColor

/**
 * Created by roger on 2020/2/11
 */
class GPAgraph : View {

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        init()
    }


    private lateinit var gpaPaint: Paint
    private val mWidth: Int = dp2px(100)
    private val mHeight: Int = dp2px(100)
    private lateinit var path: Path
    private lateinit var pathBlue: Path
    private val bottomWidth = dp2px(50)
    private val topWidth = dp2px(25)
    private var segHeight: Int = 0
    private var segWidth: Int = 0
    private lateinit var dashPaint: Paint
    private val textArray = arrayListOf("大一上", "大一下", "大二上", "大二下", "大三上", "大三下", "大四上", "大四下")
    private lateinit var textPaint: Paint
    private lateinit var gradientPaint: Paint
    private lateinit var linearGradient: LinearGradient
    var touchPoint: Int? = null
    var array = arrayListOf<Float>()
    private lateinit var whitePaint: Paint

    private fun init() {
        backgroundColor = Color.WHITE

        gpaPaint = Paint()
        gpaPaint.color = Color.parseColor("#2921D1")
        gpaPaint.style = Paint.Style.STROKE
        gpaPaint.strokeWidth = dp2px(4).toFloat()

        path = Path()
        pathBlue = Path()

        dashPaint = Paint()
        dashPaint.color = Color.parseColor("#2915315B")
        dashPaint.style = Paint.Style.STROKE
        dashPaint.strokeWidth = dp2px(1).toFloat()
        dashPaint.pathEffect = DashPathEffect(floatArrayOf(dp2px(5).toFloat(), dp2px(3).toFloat()), 0F)

        textPaint = Paint()
        textPaint.color = Color.parseColor("#A415315B")
        textPaint.textSize = dp2px(9).toFloat()
        textPaint.textAlign = Paint.Align.CENTER

        gradientPaint = Paint()
        gradientPaint.style = Paint.Style.FILL

        whitePaint = Paint()
        whitePaint.color = Color.parseColor("#FFFFFFFF")
        whitePaint.style = Paint.Style.FILL

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(resolveSize(mWidth, widthMeasureSpec), resolveSize(mHeight, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val canvas = canvas ?: return
        canvas.save()
        path.reset()
        pathBlue.reset()
        canvas.translate(0F, (height - bottomWidth).toFloat())

        drawDashLine(canvas)
        drawText(canvas)
        if (!array.isNullOrEmpty()) {
            drawGPAPath(canvas)
        }

        canvas.restore()
    }

    private fun drawGPAPath(canvas: Canvas) {
        path.moveTo(0F, -3F * segHeight)
        for (item in array.withIndex()) {
            path.lineTo(segWidth * (item.index + 1F), -1 * segHeight * item.value)
        }
        path.lineTo(width.toFloat(), -1 * array.last() * segHeight)
        pathBlue.addPath(path)

        path.lineTo(segWidth * 9F, 0F)
        path.lineTo(0F, 0F)
        path.close()

        gradientPaint.shader = linearGradient
        canvas.drawPath(path, gradientPaint)

        //画点
        gpaPaint.style = Paint.Style.FILL
        for (item in array.withIndex()) {
            canvas.drawCircle((item.index + 1) * segWidth.toFloat(), -1 * item.value * segHeight.toFloat(), dp2px(5).toFloat(), gpaPaint)
        }
        gpaPaint.style = Paint.Style.STROKE
        canvas.drawPath(pathBlue, gpaPaint)

        //如果有touch 的点的话，画touch的点
        touchPoint?.let {
            //先画外面的蓝色
            gpaPaint.style = Paint.Style.FILL
            canvas.drawCircle((it + 1) * segWidth.toFloat(), -1 * array[it] * segHeight.toFloat(), dp2px(8).toFloat(), gpaPaint)
            canvas.drawCircle((it + 1) * segWidth.toFloat(), -1 * array[it] * segHeight.toFloat(), dp2px(4).toFloat(), whitePaint)
        }

    }

    private fun drawDashLine(canvas: Canvas) {
        for (i in 0..3) {
            canvas.drawLine(0F, -1 * i * segHeight.toFloat(), width.toFloat(), -1 * i * segHeight.toFloat(), dashPaint)
        }

    }

    private fun drawText(canvas: Canvas) {

        textPaint.textSize = dp2px(9).toFloat()
        for (item in textArray.withIndex()) {
            canvas.drawText(textArray[item.index], (item.index + 1) * segWidth.toFloat(), bottomWidth / 2F, textPaint)
        }

        if (!array.isNullOrEmpty()) {
            touchPoint?.let {
                textPaint.textSize = dp2px(14).toFloat()
                canvas.drawText(array[it].toString(), (it + 1) * segWidth.toFloat(), -1 * array[it] * segHeight.toFloat() - dp2px(13), textPaint)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        segHeight = (h - bottomWidth - topWidth) / 4
        segWidth = w / 9

        linearGradient = LinearGradient(0F, 0F, 0F, -1F * h, Color.parseColor("#66FFFFFF"), Color.parseColor("#44A19EFF"), Shader.TileMode.REPEAT)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val event = event ?: return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchPoint = (event.x / segWidth - 0.5).toInt()
                postInvalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                touchPoint = (event.x / segWidth - 0.5).toInt()
                postInvalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                touchPoint = (event.x / segWidth - 0.5).toInt()
                postInvalidate()
                return true
            }
        }
        return super.onTouchEvent(event)

    }
}