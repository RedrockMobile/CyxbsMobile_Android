package com.mredrock.cyxbs.freshman.view.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.mredrock.cyxbs.freshman.R
import org.jetbrains.anko.dip
import java.lang.RuntimeException
import kotlin.math.min

/**
 * Create by yuanbing
 * on 2019/8/4
 */
class SuccessView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(
        context,
        attr,
        defStyleAttr
) {
    private var mWidth = dip(65).toFloat()
    private var mHeight = mWidth
    private var mRadius = mWidth  / 2
    private var mDecorationRadius = 0f
    private var mLineWidth = 8f
    @ColorInt var mColor = Color.BLUE

    // 圆心坐标
    private var mOX = mWidth / 2
    private var mOY = mHeight / 2

    // 组成勾的三个点，从左至右各点的坐标
    private var mPointXL = 0f
    private var mPointYL = 0f

    private var mPointXM = 0f
    private var mPointYM = 0f

    private var mPointXR = 0f
    private var mPointYR = 0f

    private val mCirclePath = Path()
    private val mGouPath = Path()
    private val mPaint = Paint()
    private val mDecorationPaint = Paint()
    private val mGouPathMeasure = PathMeasure()
    private val mGouAnimationPath = Path()
    private val mCirclePathMeasure = PathMeasure()
    private val mCircleAnimationPath = Path()
    private var mGouAnimationEnd = false
    private var mCircleAnimationEnd = false

    private var mGouProgress = 0f
    private var mCircleProgress = 0f

    private var mCircleAnimationDuration = 0L
    private var mGouAnimationDuration = 0L

    var mOnAnimationFinish: (() -> Unit)? = null

    init {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.SuccessView)
        mColor = typedArray.getColor(R.styleable.SuccessView_color, Color.BLACK)
        mCircleAnimationDuration = typedArray.getInteger(R.styleable.SuccessView_circleDuration, 0).toLong()
        mGouAnimationDuration = typedArray.getInteger(R.styleable.SuccessView_gouDuration, 0).toLong()
        mLineWidth = typedArray.getDimension(R.styleable.SuccessView_lineWidth, 0f)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        // 画外圆环
        mCirclePathMeasure.getSegment(0f, mCircleProgress, mCircleAnimationPath, true)
        canvas?.drawPath(mCircleAnimationPath, mPaint)

        // 画对勾的起始点
        if (mCircleAnimationEnd) {
            canvas?.drawCircle(mPointXL, mPointYL, mDecorationRadius, mDecorationPaint)
        }

        // 画对勾
        mGouPathMeasure.getSegment(0f, mGouProgress, mGouAnimationPath, true)
        canvas?.drawPath(mGouAnimationPath, mPaint)

        // 画对勾的终点
        if (mGouAnimationEnd) {
            canvas?.drawCircle(mPointXR, mPointYR, mDecorationRadius, mDecorationPaint)
        }
    }

    private fun initPaint() {
        mPaint.color = mColor
        mPaint.strokeWidth = mLineWidth
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true

        mDecorationPaint.isAntiAlias = true
        mDecorationPaint.style = Paint.Style.FILL
        mDecorationPaint.color = mColor
        mDecorationPaint.strokeWidth = mLineWidth
        mDecorationRadius = mLineWidth / 2
    }

    private fun initPath() {
        mCirclePath.addCircle(mOX, mOY, mRadius, Path.Direction.CCW)
        mGouPath.moveTo(mPointXL, mPointYL)
        mGouPath.lineTo(mPointXM, mPointYM)
        mGouPath.lineTo(mPointXR, mPointYR)
        mGouPathMeasure.setPath(mGouPath, false)
        mCirclePathMeasure.setPath(mCirclePath, false)
    }

    private fun initPoint() {
        mPointXL = mOX - ((mRadius - mLineWidth) / 4 * 3)
        mPointYL = mOY

        mPointXM = mPointXL + ((mRadius - mPointXL) / 6 * 5)
        mPointYM = mOY + (mRadius - mLineWidth) / 2

        mPointXR = mOX + (mRadius - mLineWidth) / 3 * 2
        mPointYR = mOY - (mRadius - mLineWidth) / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasure = MeasureSpec.getSize(widthMeasureSpec)
        var heightMeasure = MeasureSpec.getSize(heightMeasureSpec)
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMeasureMode == MeasureSpec.AT_MOST) widthMeasure = mWidth.toInt()
        if (heightMeasureMode == MeasureSpec.AT_MOST) heightMeasure = mHeight.toInt()

        mWidth = min(widthMeasure, heightMeasure).toFloat()
        mHeight = mWidth
        mRadius = mWidth / 2 - 1.5f * mLineWidth
        mOX = left + mWidth / 2
        mOY = top + mHeight / 2

        setMeasuredDimension(widthMeasure, heightMeasure)
        initPath()
        initPaint()
        initPoint()

        initAnimation()
    }

    private fun initAnimation() {
        val gouValueAnimation = ValueAnimator.ofFloat(0f ,1f)
        gouValueAnimation.addUpdateListener {
            mGouProgress = it.animatedValue as Float * mGouPathMeasure.length
            invalidate()
        }
        gouValueAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                mGouAnimationEnd = true
                invalidate()
                postDelayed({ mOnAnimationFinish?.let { it() } }, 150)
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
        })
        gouValueAnimation.duration = mGouAnimationDuration

        val circleValueAnimation = ValueAnimator.ofFloat(0f, 1f)
        circleValueAnimation.addUpdateListener {
            mCircleProgress = it.animatedValue as Float * mCirclePathMeasure.length
            invalidate()
        }
        circleValueAnimation.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                mCircleAnimationEnd = true
                gouValueAnimation.start()
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
        })
        circleValueAnimation.duration = mCircleAnimationDuration
        circleValueAnimation.start()
    }
}