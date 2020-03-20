package com.mredrock.cyxbs.discover.grades.utils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.mredrock.cyxbs.discover.grades.utils.extension.dp2px
import org.jetbrains.anko.backgroundColor
import kotlin.math.abs

/**
 * Created by roger on 2020/2/11
 */
@Suppress("NAME_SHADOWING")
class GpAGraph : View {

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        init()
    }


    //默认的宽高
    private val mWidth: Int = dp2px(100)
    private val mHeight: Int = dp2px(100)

    private lateinit var path: Path
    private lateinit var pathBlue: Path
    //顶部和底部预留的高度
    private val bottomHeight = dp2px(50)
    private val topHeight = dp2px(25)

    private var segHeight: Int = 0
    private var segWidth: Int = 0
    //Paint
    private lateinit var dashPaint: Paint
    private lateinit var gpaPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var gradientPaint: Paint
    private lateinit var whitePaint: Paint


    private lateinit var linearGradient: LinearGradient
    //用户点击的是textArray中的第几个。例如用户点击"大一上"，touchPoint则为0
    private var touchPoint: Int? = null
    private val textArray = arrayListOf("大一上", "大一下", "大二上", "大二下", "大三上", "大三下", "大四上", "大四下")

    /**
     * 数据
     */
    private var originalData = arrayListOf<Float>()   //原始数据
    private lateinit var mappingData: List<Float>       //经过映射后的数据：通过将array用mappingRule一一映射得到

    //映射规则
    private var mappingRule: GraphRule = DefaultMappingRule()


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
        //将原始数据转换成元素范围在区间[0,4]的数据
        mappingData = originalData.map {
            mappingRule.mappingRule(it)
        }

    }

    /**
     * 设置数据
     */
    fun setData(newData: ArrayList<Float>) {
        originalData = newData
        mappingData = originalData.map {
            mappingRule.mappingRule(it)
        }
    }

    /**
     * 设置规则
     */
    fun setRule(newRule: GraphRule) {
        mappingRule = newRule
        mappingData = originalData.map {
            mappingRule.mappingRule(it)
        }
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
        canvas.translate(0F, (height - bottomHeight).toFloat())

        drawDashLine(canvas)
        drawText(canvas)
        if (!mappingData.isNullOrEmpty()) {
            drawGPAPath(canvas)
        }

        canvas.restore()
    }

    private fun drawGPAPath(canvas: Canvas) {
        path.moveTo(0F, -3F * segHeight)
        for (item in mappingData.withIndex()) {
            path.lineTo(segWidth * (item.index + 1F), -1 * segHeight * item.value)
        }
        path.lineTo(width.toFloat(), -1 * mappingData.last() * segHeight)
        pathBlue.addPath(path)

        path.lineTo(segWidth * 9F, 0F)
        path.lineTo(0F, 0F)
        path.close()

        gradientPaint.shader = linearGradient
        canvas.drawPath(path, gradientPaint)

        //画点
        gpaPaint.style = Paint.Style.FILL
        for (item in mappingData.withIndex()) {
            canvas.drawCircle((item.index + 1) * segWidth.toFloat(), -1 * item.value * segHeight.toFloat(), dp2px(5).toFloat(), gpaPaint)
        }
        gpaPaint.style = Paint.Style.STROKE
        canvas.drawPath(pathBlue, gpaPaint)

        //如果有touch 的点的话，画touch的点
        touchPoint?.let {
            if (mappingData.size >= it + 1) {
                //先画外面的蓝色
                gpaPaint.style = Paint.Style.FILL
                canvas.drawCircle((it + 1) * segWidth.toFloat(), -1 * mappingData[it] * segHeight.toFloat(), dp2px(8).toFloat(), gpaPaint)
                canvas.drawCircle((it + 1) * segWidth.toFloat(), -1 * mappingData[it] * segHeight.toFloat(), dp2px(4).toFloat(), whitePaint)
            }
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
            canvas.drawText(textArray[item.index], (item.index + 1) * segWidth.toFloat(), bottomHeight / 2F, textPaint)
        }


        touchPoint?.let {
            if (mappingData.size >= it + 1) {
                textPaint.textSize = dp2px(14).toFloat()
                canvas.drawText(originalData[it].toString(), (it + 1) * segWidth.toFloat(), -1 * mappingData[it] * segHeight.toFloat() - dp2px(13), textPaint)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        segHeight = (h - bottomHeight - topHeight) / 4
        segWidth = w / 9

        linearGradient = LinearGradient(0F, 0F, 0F, -1F * h, Color.parseColor("#66FFFFFF"), Color.parseColor("#44A19EFF"), Shader.TileMode.REPEAT)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val event = event ?: return true
        val newPoint = (event.x / segWidth - 0.5).toInt()
        if (touchPoint != null && touchPoint == newPoint) {
            return true
        } else {
            touchPoint = (event.x / segWidth - 0.5).toInt()
            postInvalidate()
        }
        return true
    }

    /**
     * 以下是配合BottomSheet的事件分发部分
     */
    private var childNeed = false
    private var mLastX: Float? = null
    private var mLastY: Float? = null

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val parent = parent ?: return super.dispatchTouchEvent(event)
        val event = event ?: return super.dispatchTouchEvent(event)

        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                mLastX?.let { lastX ->
                    mLastY?.let { lastY ->
                        val deltaX = x - lastX
                        val deltaY = y - lastY
                        if (abs(deltaY) - abs(deltaX) > 0.000001F && !childNeed) {
                            parent.requestDisallowInterceptTouchEvent(false)
                        } else if (abs(deltaY) - abs(deltaX) <= 0.000001F) {
                            parent.requestDisallowInterceptTouchEvent(true)
                            childNeed = true
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                childNeed = false
            }
            MotionEvent.ACTION_CANCEL -> {
                childNeed = false
            }
        }
        mLastX = x
        mLastY = y
        return super.dispatchTouchEvent(event)
    }


    class DefaultMappingRule : GraphRule() {
        override fun mappingRule(old: Float): Float {
            return old
        }

    }
}