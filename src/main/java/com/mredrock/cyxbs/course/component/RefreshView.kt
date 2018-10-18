package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

/**
 * Created by anriku on 2018/9/28.
 */
class RefreshView : View {

    private val mPaint = Paint(Color.BLACK).apply {
        isDither = true
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.FILL
        strokeWidth = 5f
    }
    private val mEveryRotate = 30f
    private var mRotatedAngle = 0f
    private val mLineLength = 50f
    private val mCenterPoint = PointF()
    private var mBaseAngle = 0f
    private val mStartPointRatio = 2f / 5f

    var isRefreshing = false
        set(value) {
            field = value
            if (field) {
                visibility = View.VISIBLE

                Thread {
                    while (field) {
                        postInvalidate()
                        Thread.sleep(100)
                        mBaseAngle = (mBaseAngle + 30) % 360f
                    }
                }.start()
            } else {
                visibility = View.GONE
            }
        }

    constructor(context: Context) : super(context){

    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mCenterPoint.x = (measuredWidth / 2).toFloat()
        mCenterPoint.y = (measuredHeight / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mRotatedAngle = 0f

        while (mRotatedAngle <= 360f) {

            mPaint.alpha = (255 * (mRotatedAngle + mBaseAngle) / 360f).toInt() % 255
            canvas.save()
            canvas.rotate(mRotatedAngle, mCenterPoint.x, mCenterPoint.y)
            canvas.drawLine(mCenterPoint.x + mLineLength * mStartPointRatio, mCenterPoint.y,
                    mCenterPoint.x + mLineLength, mCenterPoint.y, mPaint)
            mRotatedAngle += mEveryRotate
            canvas.restore()
        }
    }
}