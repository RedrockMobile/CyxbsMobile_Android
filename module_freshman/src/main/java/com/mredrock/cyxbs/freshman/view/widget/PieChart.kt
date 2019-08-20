package com.mredrock.cyxbs.freshman.view.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.mredrock.cyxbs.freshman.R
import org.jetbrains.anko.dip
import org.jetbrains.anko.px2dip
import org.jetbrains.anko.sp

/**
 * Create by yuanbing
 * on 2019/8/6
 */
class PieChart @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(
        context,
        attr,
        defStyleAttr
) {
    private var mWidth = dip(335)
    private var mHeight = dip(318)

    // Color
    private var mTitleTextColor = 0
    private var mItemTextColor = 0
    private var mBorderColor = 0
    private var mFirstItemColor = 0
    private var mSecondItemColor = 0
    private var mGraphDataTextColor = 0

    // item
    private var mFirstItemLeft = 0f
    private var mFirstItemTop = 0f
    private var mFirstItemRight = 0f
    private var mFirstItemBottom = 0f
    private var mItemOffset = 0f

    // Graph
    private var mGraphLeft = 0f
    private var mGraphTop = 0f
    private var mGraphRight = 0f
    private var mGraphBottom = 0f
    private var mGraphRadius = 0f
    private var mOX = 0f
    private var mOY = 0f

    // EnrollmentRequirementsData
    private var mFirstGraphDataTextX = 0f
    private var mFirstGraphDataTextY = 0f
    private var mSecondGraphDataTextX = 0f
    private var mSecondGraphDataTextY = 0f

    // DormitoryAndCanteenText
    var mTitleText = "传媒艺术学院男女比例"
    var mFirstItemText = "男"
    var mSecondItemText = "女"
    var mFirstGraphDataText = ""
    var mSecondGraphDataText = ""
    private var mTitleTextX = 0f
    private var mTitleTextY = 0f
    private var mFirstItemTextX = 0f
    private var mFirstItemTextY = 0f

    // TextSize
    private var mTitleTextSize = 0f
    private var mItemTextSize = 0f
    private var mGraphDataTextSize = 0f

    private var mBorderWidth = dip(1)

    // Paint
    private lateinit var mFirstPaint: Paint
    private lateinit var mSecondPaint: Paint
    private lateinit var mBorderPaint: Paint
    private lateinit var mTitleTextPaint: Paint
    private lateinit var mFirstGraphDataTextPaint: Paint
    private lateinit var mSecondGraphDataTextPaint: Paint
    private lateinit var mItemTextPaint: Paint

    var mFirstGraphWeight = 0f
        set(value) {
            field = value
            requestLayout()
        }
    var mSecondGraphWeight = 0f
        set(value) {
            field = value
            requestLayout()
        }

    private var mFirstAnimationCurrentProgress = 0f
    private var mSecondAnimationCurrentProgress = 0f
    private var mAnimationDuration = 1000L
    var mAnimation: ValueAnimator? = null
    private var mIsNeedDrawSecondItem = false

    init {
        val typeArray = context.obtainStyledAttributes(attr, R.styleable.PieChart)
        mFirstItemColor = typeArray.getColor(R.styleable.PieChart_firstItemColor, Color.RED)
        mSecondItemColor = typeArray.getColor(R.styleable.PieChart_secondItemColor, Color.BLUE)
        mFirstItemText = typeArray.getString(R.styleable.PieChart_firstItem) ?: "男"
        mSecondItemText = typeArray.getString(R.styleable.PieChart_secondItem) ?: "女"
        mBorderColor = typeArray.getColor(R.styleable.PieChart_borderColor, Color.BLACK)
        mTitleText = typeArray.getString(R.styleable.PieChart_titleText) ?: "Chart"
        mTitleTextColor = typeArray.getColor(R.styleable.PieChart_titleTextColor, Color.BLACK)
        mTitleTextSize = typeArray.getDimension(R.styleable.PieChart_titleTextSize, sp(15))
        mItemTextColor = typeArray.getColor(R.styleable.PieChart_itemTextColor, Color.BLACK)
        mItemTextSize = typeArray.getDimension(R.styleable.PieChart_itemTextSize, sp(13))
        mGraphDataTextSize = typeArray.getDimension(R.styleable.PieChart_itemDataTextSize, sp(12))
        mGraphDataTextColor = typeArray.getColor(R.styleable.PieChart_itemDataTextColor, Color.BLACK)
        typeArray.recycle()
        initPaint()
    }

    override fun onDraw(canvas: Canvas?) {
        drawTitle(canvas)
        drawItemAndItemText(canvas)
        drawGraph(canvas)
        drawBorder(canvas)
        drawGraphData(canvas)
    }

    private fun drawGraphData(canvas: Canvas?) {
        mFirstGraphDataText = String.format("%.1f", (mFirstGraphWeight * mFirstAnimationCurrentProgress * 100)) + "%"
        canvas?.drawText(mFirstGraphDataText, mFirstGraphDataTextX, mFirstGraphDataTextY,
                mFirstGraphDataTextPaint)
        if (mIsNeedDrawSecondItem) {
            mSecondGraphDataText = String.format("%.1f", (mSecondGraphWeight * mSecondAnimationCurrentProgress * 100)) + "%"
            canvas?.drawText(mSecondGraphDataText, mSecondGraphDataTextX, mSecondGraphDataTextY,
                    mSecondGraphDataTextPaint)
        }
    }

    private fun drawTitle(canvas: Canvas?) {
        canvas?.drawText(mTitleText, mTitleTextX, mTitleTextY, mTitleTextPaint)
    }

    private fun drawItemAndItemText(canvas: Canvas?) {
        canvas?.drawRect(mFirstItemLeft, mFirstItemTop, mFirstItemRight, mFirstItemBottom, mFirstPaint)

        canvas?.drawRect(mFirstItemLeft, mFirstItemTop + mItemOffset, mFirstItemRight,
                mFirstItemBottom + mItemOffset, mSecondPaint)

        canvas?.drawText(mFirstItemText, mFirstItemTextX, mFirstItemTextY, mItemTextPaint)

        canvas?.drawText(mSecondItemText, mFirstItemTextX, mFirstItemTextY + mItemOffset, mItemTextPaint)
    }

    private fun drawGraph(canvas: Canvas?) {
        canvas?.drawArc(mGraphLeft, mGraphTop, mGraphRight, mGraphBottom, 90f,
                mFirstGraphWeight * mFirstAnimationCurrentProgress * 360, true, mFirstPaint)

        if (mIsNeedDrawSecondItem) {
            canvas?.drawArc(mGraphLeft, mGraphTop, mGraphRight, mGraphBottom,
                    mFirstGraphWeight * 360 + 90, mSecondGraphWeight * mSecondAnimationCurrentProgress * 360, true, mSecondPaint)
        }
    }

    private fun drawBorder(canvas: Canvas?) {
        canvas?.drawArc(mGraphLeft, mGraphTop, mGraphRight, mGraphBottom, 90f,
                mFirstGraphWeight * mFirstAnimationCurrentProgress * 360, true, mBorderPaint)

        canvas?.drawRect(mFirstItemLeft, mFirstItemTop, mFirstItemRight, mFirstItemBottom, mBorderPaint)

        canvas?.drawRect(mFirstItemLeft, mFirstItemTop + mItemOffset, mFirstItemRight,
                mFirstItemBottom + mItemOffset, mBorderPaint)

        if (mIsNeedDrawSecondItem) {
            canvas?.drawArc(mGraphLeft, mGraphTop, mGraphRight, mGraphBottom,
                    mFirstGraphWeight * 360 + 90,
                    mSecondGraphWeight * mSecondAnimationCurrentProgress * 360, true, mBorderPaint)
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
                widthMeasure = (335f / 328 * heightMeasure).toInt()
            }
            if (heightMeasureMode == MeasureSpec.AT_MOST) {
                heightMeasure = (328f / 335 * widthMeasure).toInt()
            }
        }

        mWidth = widthMeasure.toFloat()
        mHeight = heightMeasure.toFloat()

        initPoint()
        initAnimation()

        setMeasuredDimension(widthMeasure, heightMeasure)
    }

    private fun initAnimation() {
        val secondAnimation = ValueAnimator.ofFloat(0f, 1f)
        secondAnimation.addUpdateListener {
            mSecondAnimationCurrentProgress = it.animatedValue as Float
            invalidate()
        }
        secondAnimation.duration = (mAnimationDuration * mSecondGraphWeight).toLong()
        secondAnimation.interpolator = LinearInterpolator()
        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.addUpdateListener {
            mFirstAnimationCurrentProgress = it.animatedValue as Float
            invalidate()
        }
        animation.duration = (mAnimationDuration * mFirstGraphWeight).toLong()
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                mIsNeedDrawSecondItem = true
                secondAnimation.start()
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) { mIsNeedDrawSecondItem = false }
            override fun onAnimationRepeat(p0: Animator?) {}
        })
        animation.interpolator = LinearInterpolator()
        mAnimation = animation
        animation.start()
    }

    private fun initPaint() {
        mFirstPaint = Paint()
        mFirstPaint.style = Paint.Style.FILL
        mFirstPaint.color = mFirstItemColor

        mSecondPaint = Paint()
        mSecondPaint.style = Paint.Style.FILL
        mSecondPaint.color = mSecondItemColor

        mBorderPaint = Paint()
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = mBorderWidth

        mTitleTextPaint = Paint()
        mTitleTextPaint.isAntiAlias = true
        mTitleTextPaint.textSize = mTitleTextSize
        mTitleTextPaint.color = mTitleTextColor

        mItemTextPaint = Paint()
        mItemTextPaint.isAntiAlias = true
        mItemTextPaint.textSize = mItemTextSize
        mItemTextPaint.color = mItemTextColor

        mFirstGraphDataTextPaint = Paint()
        mFirstGraphDataTextPaint.color = mGraphDataTextColor
        mFirstGraphDataTextPaint.textSize = mGraphDataTextSize
        mFirstGraphDataTextPaint.isAntiAlias = true
        mFirstGraphDataTextPaint.textAlign = Paint.Align.CENTER

        mSecondGraphDataTextPaint = Paint()
        mSecondGraphDataTextPaint.color = mGraphDataTextColor
        mSecondGraphDataTextPaint.textSize = mGraphDataTextSize
        mSecondGraphDataTextPaint.isAntiAlias = true
        mSecondGraphDataTextPaint.textAlign = Paint.Align.CENTER
    }

    private fun initPoint() {
        mTitleTextX = left + scaleXDip(26)
        mTitleTextY = top + scaleXDip(47)

        mFirstItemLeft = mTitleTextX
        mFirstItemTop = mTitleTextY + scaleYDip(16)
        mFirstItemRight = mFirstItemLeft + scaleXDip(23)
        mFirstItemBottom = mFirstItemTop + scaleYDip(15)
        mItemOffset = scaleYDip(26)

        mFirstItemTextX = mFirstItemRight + scaleXDip(5)
        mFirstItemTextY = mFirstItemBottom - scaleYDip(3)

        mGraphRadius = scaleXDip(87)
        mGraphLeft = left + scaleXDip(80)
        mGraphTop = top + scaleYDip(113)
        mGraphRight = mGraphLeft + 2 * mGraphRadius
        mGraphBottom = mGraphTop + 2 * mGraphRadius

        mOX = mGraphLeft + mGraphRadius
        mOY = mGraphTop + mGraphRadius

        val firstGraphPath = Path()
        firstGraphPath.addArc(mGraphLeft, mGraphTop, mGraphRight, mGraphBottom, 90f,
                mFirstGraphWeight * 360)
        val firstGraphPathMeasure = PathMeasure()
        val mid = FloatArray(2)
        firstGraphPathMeasure.setPath(firstGraphPath, false)
        firstGraphPathMeasure.getPosTan(firstGraphPathMeasure.length / 2, mid, null)
        mFirstGraphDataTextX = mid[0] + (mOX - mid[0]) / 2
        mFirstGraphDataTextY = mid[1] + (mOY - mid[1]) / 2 + mGraphDataTextSize / 2

        val secondGraphPath = Path()
        secondGraphPath.addArc(mGraphLeft, mGraphTop, mGraphRight, mGraphBottom,
                90 + mFirstGraphWeight * 360, mSecondGraphWeight * 360)
        val secondPathMeasure = PathMeasure()
        secondPathMeasure.setPath(secondGraphPath, false)
        secondPathMeasure.getPosTan(secondPathMeasure.length / 2, mid, null)
        mSecondGraphDataTextX = mid[0] + (mOX - mid[0]) / 2
        mSecondGraphDataTextY = mid[1] + (mOY - mid[1]) / 2 + mGraphDataTextSize / 2
    }

    private fun scaleYDip(d: Int) = dip((d / 335f * px2dip(mWidth.toInt())).toInt())

    private fun scaleXDip(d: Int) = dip((d / 328f * px2dip(mHeight.toInt())).toInt())

    private fun dip(d: Int) = dip(d.toFloat()).toFloat()

    private fun sp(s: Int) = sp(s.toFloat()).toFloat()
}