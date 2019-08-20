package com.mredrock.cyxbs.freshman.view.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.freshman.R
import org.jetbrains.anko.dip
import org.jetbrains.anko.px2dip
import org.jetbrains.anko.sp
import kotlin.math.ceil
import kotlin.math.max

/**
 * Create by yuanbing
 * on 2019/8/5
 * 暂定为最多支持有三个条形图的图
 */
class Histogram @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(
        context,
        attr,
        defStyleAttr
) {
    // 图表整体的相关参数
    private var mWidth = dip(335)
    private var mHeight = dip(298)
    private var mOffsetY = 0f

    // 距离相关参数
    private var mFirstToY = 0f  // 第一条柱形图到Y轴的距离
    private var mSpacing = 0f  // 每一条柱形图之间的距离
    private var mGraphWidth = 0f  // 柱形图的宽度
    private var mXDescriptionToX = 0f  // X轴的描述到X轴的距离
    private var mDataToGraph = 0f  // 数据到条形图的距离

    // 字号相关参数
    private var mTitleTextSize = 0f  // 图表标题的文字颜色
    private var mYDescriptionTextSize = 0f  // Y轴描述的文字颜色
    private var mXDescriptionTextSize = 0f  // X轴描述的文字颜色
    private var mDataTextSize = 0f  // 条形图的数值的文字颜色

    // 颜色相关参数
    private var mTitleTextColor = 0  // 图表标题的文字颜色
    private var mYDescriptionTextColor = 0  // Y轴描述的文字颜色
    private var mXDescriptionTextColor = 0  // X轴描述的文字颜色
    private var mDataTextColor = 0  // 条形图的数值的文字颜色
    private var mFirstGraphColor = 0  // 第一个条形图的颜色
    private var mSecondGraphColor = 0  // 第二个条形图的颜色
    private var mThirdGraphColor = 0  // 第三个条形图的颜色
    private var mDecorationColor = 0  // 每一个条形图的装饰的颜色
    private var mXYColor = 0  // 坐标轴的颜色

    // 坐标轴相关的其余的参数
    private var mYLength = 0f  // Y轴的高度
    private var mXLength = 0f  // X轴的长度
    private var mYDividerCount = 4  // Y轴上分割线的个数
    private var mOX = 0f  // 原点的X坐标
    private var mOY = 0f  // 原点的Y坐标
    private var mXYWidth = dip(1)  // XY坐标的宽度
    private var mYSpacing = 0f
    private var mRY = 0f  // Y轴的有效长度
    private var mDecorationWidth = 0f

    // 三个条形图分别的权重
    var mFirstGraphWeight = 0f
    var mSecondGraphWeight = 0f
    var mThirdGraphWeight = 0f

    private var mFirstData = ""
    private var mSecondData = ""
    private var mThirdData = ""

    // 点相关的信息
    private var mYX = 0f  // Y轴端点
    private var mYY = 0f

    private var mYXL = 0f
    private var mYYL = 0f

    private var mYXR = 0f
    private var mYYR = 0f

    private var mXX = 0f  // X轴端点
    private var mXY = 0f

    private var mXXA = 0f
    private var mXYA = 0f

    private var mXXB = 0f
    private var mXYB = 0f

    private var mTitleX = 0f
    private var mTitleY = 0f

    // 条形图的坐标
    private var mFirstGraphX = 0f
    private var mSecondGraphX = 0f
    private var mThirdGraphX = 0f

    // X轴描述文本的坐标
    private var mXDescriptionY = 0f

    // Paint
    private lateinit var mXYPaint: Paint
    private lateinit var mTitlePaint: Paint
    private lateinit var mYDescriptionPaint: Paint
    private lateinit var mXDescriptionPaint: Paint
    private lateinit var mDataPaint: Paint
    private lateinit var mFirstGraphPaint: Paint
    private lateinit var mSecondGraphPaint: Paint
    private lateinit var mThirdGraphPaint: Paint
    private lateinit var mDecorationPaint: Paint

    // 文本
    var mTitle = ""
    var mYDescription = "难度系数"
    var mFirstGraphDescription = ""
        set(value) {
            field = value
            val maxLength = max(max(mFirstGraphDescription.length, mSecondGraphDescription.length),
                    mThirdGraphDescription.length)
            mOffsetY = ceil(maxLength.toFloat() / mXDescriptionTextCountInLine) * mXDescriptionTextSize
            requestLayout()
        }
    var mSecondGraphDescription = ""
        set(value) {
            field = value
            val maxLength = max(max(mFirstGraphDescription.length, mSecondGraphDescription.length),
                    mThirdGraphDescription.length)
            mOffsetY = ceil(maxLength.toFloat() / mXDescriptionTextCountInLine) * mXDescriptionTextSize
            requestLayout()
        }
    var mThirdGraphDescription = ""
        set(value) {
            field = value
            val maxLength = max(max(mFirstGraphDescription.length, mSecondGraphDescription.length),
                    mThirdGraphDescription.length)
            mOffsetY = ceil(maxLength.toFloat() / mXDescriptionTextCountInLine) * mXDescriptionTextSize
            requestLayout()
        }
    private val mXDescriptionTextCountInLine = 4

    // 当前播放的动画的进度
    private var mCurrentProgress = 0f
    private var mFirstGraphCurrentHeight = 0f
    private var mSecondGraphCurrentHeight = 0f
    private var mThirdGraphCurrentHeight = 0f
    var mAnimation: ValueAnimator? = null

    init {
        val typeArray = context.obtainStyledAttributes(attr, R.styleable.Histogram)
        mTitle = typeArray.getString(R.styleable.Histogram_title) ?: ""
        mTitleTextColor = typeArray.getColor(R.styleable.Histogram_titleTextColor, Color.BLACK)
        mYDescription = typeArray.getString(R.styleable.Histogram_descriptionY) ?: "难度系数"
        mYDescriptionTextColor = typeArray.getColor(R.styleable.Histogram_descriptionTextColorY, Color.BLACK)
        mXDescriptionTextColor = typeArray.getColor(R.styleable.Histogram_descriptionTextColorX, Color.BLACK)
        mDataTextColor = typeArray.getColor(R.styleable.Histogram_dataTextColor, Color.BLACK)
        mFirstGraphColor = typeArray.getColor(R.styleable.Histogram_firstGraphColor, Color.BLACK)
        mSecondGraphColor = typeArray.getColor(R.styleable.Histogram_secondGraphColor, Color.BLACK)
        mThirdGraphColor = typeArray.getColor(R.styleable.Histogram_thirdGraphColor, Color.BLACK)
        mFirstGraphDescription = typeArray.getString(R.styleable.Histogram_firstDescriptionX) ?: ""
        mSecondGraphDescription = typeArray.getString(R.styleable.Histogram_secondDescriptionX) ?: ""
        mThirdGraphDescription = typeArray.getString(R.styleable.Histogram_thirdDescriptionX) ?: ""
        mDecorationColor = typeArray.getColor(R.styleable.Histogram_decorationColor, Color.BLACK)
        mXYColor = typeArray.getColor(R.styleable.Histogram_xYColor, Color.BLACK)
        mTitleTextSize = typeArray.getDimension(R.styleable.Histogram_titleTextSize, sp(15))
        mYDescriptionTextSize = typeArray.getDimension(R.styleable.Histogram_descriptionTextSizeY, sp(11))
        mXDescriptionTextSize = typeArray.getDimension(R.styleable.Histogram_descriptionTextSizeX, sp(13))
        mDataTextSize = typeArray.getDimension(R.styleable.Histogram_graphDataTextSize, sp(13))
        typeArray.recycle()
        initPaint()
    }

    override fun onDraw(canvas: Canvas?) {
        drawTitle(canvas)
        drawYDescription(canvas)
        drawXDescription(canvas)
        drawGraph(canvas)
        drawDecoration(canvas)
        drawXY(canvas)
        drawData(canvas)
    }

    private fun drawData(canvas: Canvas?) {
        mFirstData = String.format("%.2f", mCurrentProgress * mFirstGraphWeight)
        mSecondData = String.format("%.2f", mCurrentProgress * mSecondGraphWeight)
        mThirdData = String.format("%.2f", mCurrentProgress * mThirdGraphWeight)

        canvas?.drawText(mFirstData, mFirstGraphX,
                mOY - mFirstGraphCurrentHeight - mDataToGraph, mDataPaint)
        canvas?.drawText(mSecondData, mSecondGraphX,
                mOY - mSecondGraphCurrentHeight - mDataToGraph, mDataPaint)
        canvas?.drawText(mThirdData, mThirdGraphX,
                mOY - mThirdGraphCurrentHeight - mDataToGraph, mDataPaint)
    }

    private fun drawDecoration(canvas: Canvas?) {
        var x1 = mFirstGraphX - mGraphWidth / 2 + mDecorationWidth / 2
        var y1 = mOY
        var x2 = x1
        var y2 = mOY - mFirstGraphCurrentHeight + mDecorationWidth / 2
        var x3 = x1 + mGraphWidth - mDecorationWidth / 2
        var y3 = y2
        var x4 = x3
        var y4 = y1
        canvas?.drawLine(x1, y1, x2, y2, mDecorationPaint)
        canvas?.drawLine(x2, y2, x3, y3, mDecorationPaint)
        canvas?.drawLine(x3, y3, x4, y4, mDecorationPaint)
        canvas?.drawLine(x3 - scaleXDip(9), y3 + scaleYDip(3), x3 - scaleXDip(3),
                y3 + scaleYDip(3), mDecorationPaint)
        canvas?.drawLine(x3 - scaleXDip(3), y3 + scaleYDip(3), x3 - scaleXDip(3),
                y3 + scaleYDip(9), mDecorationPaint)

        x1 = mSecondGraphX - mGraphWidth / 2 + mDecorationWidth / 2
        y1 = mOY
        x2 = x1
        y2 = mOY - mSecondGraphCurrentHeight + mDecorationWidth / 2
        x3 = x1 + mGraphWidth - mDecorationWidth / 2
        y3 = y2
        x4 = x3
        y4 = y1
        canvas?.drawLine(x1, y1, x2, y2, mDecorationPaint)
        canvas?.drawLine(x2, y2, x3, y3, mDecorationPaint)
        canvas?.drawLine(x3, y3, x4, y4, mDecorationPaint)
        canvas?.drawLine(x3 - scaleXDip(9), y3 + scaleYDip(3), x3 - scaleXDip(3),
                y3 + scaleYDip(3), mDecorationPaint)
        canvas?.drawLine(x3 - scaleXDip(3), y3 + scaleYDip(3), x3 - scaleXDip(3),
                y3 + scaleYDip(9), mDecorationPaint)

        x1 = mThirdGraphX - mGraphWidth / 2 + mDecorationWidth / 2
        y1 = mOY
        x2 = x1
        y2 = mOY - mThirdGraphCurrentHeight + mDecorationWidth / 2
        x3 = x1 + mGraphWidth - mDecorationWidth / 2
        y3 = y2
        x4 = x3
        y4 = y1
        canvas?.drawLine(x1, y1, x2, y2, mDecorationPaint)
        canvas?.drawLine(x2, y2, x3, y3, mDecorationPaint)
        canvas?.drawLine(x3, y3, x4, y4, mDecorationPaint)
        canvas?.drawLine(x3 - scaleXDip(9), y3 + scaleYDip(3), x3 - scaleXDip(3),
                y3 + scaleYDip(3), mDecorationPaint)
        canvas?.drawLine(x3 - scaleXDip(3), y3 + scaleYDip(3), x3 - scaleXDip(3),
                y3 + scaleYDip(9), mDecorationPaint)
    }

    private fun drawGraph(canvas: Canvas?) {
        canvas?.drawLine(mFirstGraphX, mOY, mFirstGraphX, mOY - mFirstGraphCurrentHeight, mFirstGraphPaint)
        canvas?.drawLine(mSecondGraphX, mOY, mSecondGraphX,
                mOY - mSecondGraphCurrentHeight, mSecondGraphPaint)
        canvas?.drawLine(mThirdGraphX, mOY, mThirdGraphX,
                mOY - mThirdGraphCurrentHeight, mThirdGraphPaint)
    }

    private fun drawXDescription(canvas: Canvas?) {
        drawXDescription(canvas, mFirstGraphDescription, mFirstGraphX, mXDescriptionY)
        drawXDescription(canvas, mSecondGraphDescription, mSecondGraphX, mXDescriptionY)
        drawXDescription(canvas, mThirdGraphDescription, mThirdGraphX, mXDescriptionY)
    }

    private fun drawXDescription(canvas: Canvas?, text: String, x: Float, y: Float) {
        // 横向最大字符数量
        var subStr = text
        var i = 0
        while (subStr.isNotEmpty()) {
            var drawText: String
            if (mXDescriptionTextCountInLine < subStr.length) {
                drawText = subStr.substring(0, mXDescriptionTextCountInLine)
                subStr = subStr.substring(drawText.length ,subStr.length)
            } else {
                drawText = subStr
                subStr = ""
            }
            canvas?.drawText(drawText, x, y + i * mXDescriptionTextSize, mXDescriptionPaint)
            i++
        }
    }

    private fun drawTitle(canvas: Canvas?) {
        canvas?.drawText(mTitle, mTitleX, mTitleY, mTitlePaint)
    }

    private fun drawYDescription(canvas: Canvas?) {
        var i = 1
        for (char in mYDescription) {
            val x = mTitleX
            val y = mYY + i * mYDescriptionTextSize
            canvas?.drawText(char.toString(), x, y, mYDescriptionPaint)
            i++
        }
    }

    private fun drawXY(canvas: Canvas?) {
        canvas?.drawLine(mYX, mYY, mOX, mOY, mXYPaint)
        canvas?.drawLine(mOX, mOY, mXX, mXY, mXYPaint)

        canvas?.drawLine(mYXL, mYYL, mYX, mYY, mXYPaint)
        canvas?.drawLine(mYX, mYY, mYXR, mYYR, mXYPaint)

        canvas?.drawLine(mXXA, mXYA, mXX, mXY, mXYPaint)
        canvas?.drawLine(mXX, mXY, mXXB, mXYB, mXYPaint)

        // 画divider
        val step = (mYLength - mYSpacing) / mYDividerCount
        for (i in 1..4) {
            val x1 = mOX
            val x2 = x1 - 4 * mXYWidth
            val y = mOY - step * i

            canvas?.drawLine(x1, y, x2, y, mXYPaint)
        }
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasure = MeasureSpec.getSize(widthMeasureSpec)
        var heightMeasure = MeasureSpec.getSize(heightMeasureSpec)
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMeasureMode == MeasureSpec.AT_MOST && heightMeasureMode == MeasureSpec.AT_MOST) {
            widthMeasure = mWidth.toInt()
            heightMeasure = mHeight.toInt()
        } else {
            if (widthMeasureMode == MeasureSpec.AT_MOST) {
                widthMeasure = (335f / 308 * heightMeasure).toInt()
            }
            if (heightMeasureMode == MeasureSpec.AT_MOST) {
                heightMeasure = (308f / 335 * widthMeasure).toInt()
            }
        }
        left = 0

        mWidth = widthMeasure.toFloat()
        mHeight = heightMeasure.toFloat()

        initPoint()
        initAnimation()

        setMeasuredDimension(mWidth.toInt(), (mHeight + mOffsetY).toInt())
    }

    private fun initPaint() {
        mXYPaint = Paint()
        mXYPaint.isAntiAlias = true
        mXYPaint.color = mXYColor
        mXYPaint.style = Paint.Style.STROKE
        mXYPaint.strokeWidth = mXYWidth

        mTitlePaint = Paint()
        mTitlePaint.isAntiAlias = true
        mTitlePaint.textAlign = Paint.Align.LEFT
        mTitlePaint.textSize = mTitleTextSize
        mTitlePaint.color = mTitleTextColor

        mYDescriptionPaint = Paint()
        mYDescriptionPaint.isAntiAlias = true
        mYDescriptionPaint.color = mYDescriptionTextColor
        mYDescriptionPaint.textSize = mYDescriptionTextSize

        mXDescriptionPaint = Paint()
        mXDescriptionPaint.isAntiAlias = true
        mXDescriptionPaint.color = mXDescriptionTextColor
        mXDescriptionPaint.textSize = mXDescriptionTextSize
        mXDescriptionPaint.textAlign = Paint.Align.CENTER

        mFirstGraphPaint = Paint()
        mFirstGraphPaint.isAntiAlias = true
        mFirstGraphPaint.style = Paint.Style.STROKE
        mFirstGraphPaint.color = mFirstGraphColor

        mSecondGraphPaint = Paint()
        mSecondGraphPaint.isAntiAlias = true
        mSecondGraphPaint.style = Paint.Style.STROKE
        mSecondGraphPaint.color = mSecondGraphColor

        mThirdGraphPaint = Paint()
        mThirdGraphPaint.isAntiAlias = true
        mThirdGraphPaint.style = Paint.Style.STROKE
        mThirdGraphPaint.color = mThirdGraphColor

        mDecorationPaint = Paint()
        mDecorationPaint.isAntiAlias = true
        mDecorationPaint.color = mDecorationColor
        mDecorationPaint.strokeWidth = mDecorationWidth
        mDecorationPaint.style = Paint.Style.STROKE

        mDataPaint = Paint()
        mDataPaint.isAntiAlias = true
        mDataPaint.textSize = mDataTextSize
        mDataPaint.color = mDataTextColor
        mDataPaint.textAlign = Paint.Align.CENTER
    }

    private fun initPoint() {
        mOX = left + scaleXDip(54)
        mOY = top + scaleYDip(249)


        mYLength = scaleYDip(173)

        mYX = mOX
        mYY = mOY - mYLength

        mYXL = mOX - scaleXDip(3)
        mYYL = mYY + scaleYDip(4)

        mYXR = mOX + scaleXDip(3)
        mYYR = mYYL

        mXLength = scaleXDip(258)

        mXX = mOX + mXLength
        mXY = mOY

        mXXA = mXX - scaleXDip(4)
        mXYA = mXY - scaleYDip(3)

        mXXB = mXXA
        mXYB = mOY + scaleYDip(3)

        mTitleX = left + scaleXDip(32)
        mTitleY = top + scaleYDip(52)

        mGraphWidth = scaleXDip(35)
        mFirstToY = scaleXDip(25)
        mSpacing = scaleXDip(39)

        mFirstGraphX = mGraphWidth / 2 + mFirstToY + mOX
        val graphXStep = mSpacing + mGraphWidth
        mSecondGraphX = mFirstGraphX + graphXStep
        mThirdGraphX = mSecondGraphX + graphXStep

        mXDescriptionToX = scaleYDip(10)

        mXDescriptionY = mOY + mXDescriptionToX + mXDescriptionTextSize
        mYSpacing = mYLength / 7
        mDataToGraph = scaleYDip(3)
        mRY = mYLength - mYSpacing

        mFirstGraphPaint.strokeWidth = mGraphWidth
        mSecondGraphPaint.strokeWidth = mGraphWidth
        mThirdGraphPaint.strokeWidth = mGraphWidth
    }

    private fun initAnimation() {
        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.addUpdateListener {
            mCurrentProgress = it.animatedValue as Float
            mFirstGraphCurrentHeight = mCurrentProgress * 1 * mRY
            mSecondGraphCurrentHeight = mCurrentProgress * 1 / mFirstGraphWeight * mSecondGraphWeight * mRY
            mThirdGraphCurrentHeight = mCurrentProgress * 1 / mFirstGraphWeight * mThirdGraphWeight * mRY
            invalidate()
        }
        animation.duration = 1000
        mAnimation = animation
        animation.start()
    }

    private fun scaleYDip(d: Int) = dip((d / 335f * px2dip(mWidth.toInt())).toInt())

    private fun scaleXDip(d: Int) = dip((d / 308f * px2dip(mHeight.toInt())).toInt())

    private fun dip(d: Int) = dip(d.toFloat()).toFloat()

    private fun sp(s: Int) = sp(s.toFloat()).toFloat()
}