package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.mredrock.cyxbs.course.R
import org.jetbrains.anko.dip





/**
 * SimpleDotsView is used as an indicator of the ViewPager or the slideshow.
 *
 * @attr [R.styleable.SimpleDotsView_dotGap] The gap between two dots.
 * @attr [R.styleable.SimpleDotsView_dotRadius] The radius of the a dot.
 *
 * Created by anriku on 2018/9/17.
 */
class SimpleDotsView : ScheduleDetailView.DotsView {

    companion object {
        private const val TAG = "SimpleDotsView"
    }

    private var mDotsViewWidth: Int = 0
    private var mDotsViewHeight: Int = 0
    private lateinit var mPaint: Paint
    private var mDotRadius = dip(4)
    private var mFirstDotLeftMargin: Int = 0
    private val mDotsColors: IntArray by lazy {
        intArrayOf(Color.parseColor("#788EFA"),
                Color.GRAY)
    }
    private var currentFocusDot: Int = 0

    var dotsSize: Int = 0
        set(value) {
            field = value
            computeFirstDotLeftMargin()
        }
    var mDotGap: Int = dip(8)

    constructor(context: Context) : super(context) {
        initDotsView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.SimpleDotsViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleDotsView, defStyleAttr, 0)
        mDotRadius = typeArray.getDimensionPixelSize(R.styleable.SimpleDotsView_dotRadius, dip(4))
        mDotGap = typeArray.getDimensionPixelSize(R.styleable.SimpleDotsView_dotGap, dip(8))

        typeArray.recycle()
        initDotsView()
    }

    private fun initDotsView() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    /**
     * This function is used to set currentFocus dot.
     * @param position current focus dot's position.
     */
    override fun setCurrentFocusDot(position: Int) {
        currentFocusDot = position
        invalidate()
    }

    /**
     * This function is used to compute the SimpleDotsView's left margin.
     */
    private fun computeFirstDotLeftMargin() {
        mFirstDotLeftMargin = (mDotsViewWidth - 2 * mDotRadius * dotsSize - mDotGap * (dotsSize - 1)) / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val width: Int = measuredWidth
        val height: Int

        if (heightMode == MeasureSpec.AT_MOST) {
            height = mDotRadius*2
            setMeasuredDimension(width, height);
        }
        mDotsViewWidth = measuredWidth
        mDotsViewHeight = measuredHeight

        computeFirstDotLeftMargin()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        repeat(dotsSize) {
            if (it == currentFocusDot) {
                mPaint.color = mDotsColors[0]
            } else {
                mPaint.color = mDotsColors[1]
            }

            canvas.drawCircle((mFirstDotLeftMargin + (mDotRadius * 2 + mDotGap) * it + mDotRadius).toFloat(),
                    (mDotsViewHeight / 2).toFloat(), mDotRadius.toFloat(), mPaint)
        }
    }
}