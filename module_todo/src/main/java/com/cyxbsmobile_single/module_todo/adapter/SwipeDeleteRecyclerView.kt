package com.cyxbsmobile_single.module_todo.adapter

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import androidx.recyclerview.widget.RecyclerView

class SwipeDeleteRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private val INVALID_POSITION = -1
    private val SNAP_VELOCITY = 600

    private var mVelocityTracker: VelocityTracker? = null
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private val mScroller: Scroller = Scroller(context)
    private var mLastX: Float = 0f
    private var mFirstX: Float = 0f
    private var mFirstY: Float = 0f
    private var mIsSlide: Boolean = false
    private var mFlingView: ViewGroup? = null
    private var mPosition: Int = INVALID_POSITION
    private var mMenuViewWidth: Int = 0

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val x = e.x.toInt()
        val y = e.y.toInt()
        obtainVelocity(e)
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }
                mFirstX = x.toFloat()
                mLastX = x.toFloat()
                mFirstY = y.toFloat()
                mPosition = pointToPosition(x, y)
                if (mPosition != INVALID_POSITION) {
                    mFlingView = getChildAt(mPosition) as ViewGroup
                    setupMenuWidth() // 更新菜单宽度
                }
                return super.onInterceptTouchEvent(e)
            }
            MotionEvent.ACTION_MOVE -> {
                val xVelocity = mVelocityTracker?.xVelocity ?: 0f
                val yVelocity = mVelocityTracker?.yVelocity ?: 0f
                val deltaX = x - mFirstX
                val deltaY = y - mFirstY
                val isHorizontalScroll = Math.abs(deltaX) > Math.abs(deltaY)

                if (Math.abs(xVelocity) > SNAP_VELOCITY && isHorizontalScroll ||
                    Math.abs(deltaX) >= mTouchSlop && isHorizontalScroll) {
                    mIsSlide = true
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
                return false
            }
            MotionEvent.ACTION_UP -> {
                releaseVelocity()
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (mIsSlide && mPosition != INVALID_POSITION) {
            val x = e.x
            obtainVelocity(e)
            when (e.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (mMenuViewWidth != 0) {
                        val dx = mLastX - x
                        mFlingView?.let {
                            // 计算新的滚动位置，并限制在菜单宽度范围内
                            val newScrollX = (it.scrollX + dx.toInt()).coerceIn(0, mMenuViewWidth)
                            it.scrollTo(newScrollX, 0)
                            mLastX = x
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mMenuViewWidth != 0) {
                        val scrollX = mFlingView?.scrollX ?: 0
                        mVelocityTracker?.computeCurrentVelocity(1000)
                        val finalScrollX: Int
                        val velocityX = mVelocityTracker?.xVelocity ?: 0f
                        when {
                            velocityX < -SNAP_VELOCITY -> {
                                finalScrollX = 495 // 限制最终滑动距离为 495 像素
                            }
                            velocityX >= SNAP_VELOCITY -> {
                                finalScrollX = 0
                            }
                            scrollX >= 495 / 2 -> {
                                finalScrollX = 495
                            }
                            else -> {
                                finalScrollX = 0
                            }
                        }
                        // 使用 Scroller 执行平滑滚动
                        mScroller.startScroll(scrollX, 0, finalScrollX - scrollX, 0, 400)
                        invalidate()
                    }
                    mMenuViewWidth = 0
                    mIsSlide = false
                    mPosition = INVALID_POSITION
                    releaseVelocity()
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return true
        } else {
            closeMenu()
            releaseVelocity()
        }
        return super.onTouchEvent(e)
    }

    private fun setupMenuWidth() {
        mFlingView?.let { flingView ->
            // 计算菜单的总宽度，包括所有子视图的宽度和边距
            mMenuViewWidth = (0 until flingView.childCount).sumOf { index ->
                val child = flingView.getChildAt(index)
                val layoutParams = child.layoutParams as ViewGroup.MarginLayoutParams
                child.width + layoutParams.leftMargin + layoutParams.rightMargin
            }
        } ?: run {
            mMenuViewWidth = 0
        }
    }

    private fun releaseVelocity() {
        mVelocityTracker?.let {
            it.clear()
            it.recycle()
            mVelocityTracker = null
        }
    }

    private fun obtainVelocity(event: MotionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(event)
    }

    private fun pointToPosition(x: Int, y: Int): Int {
        val frame = Rect()
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if (child.visibility == View.VISIBLE) {
                child.getHitRect(frame)
                if (frame.contains(x, y)) {
                    return i
                }
            }
        }
        return INVALID_POSITION
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mFlingView?.scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }

    fun closeMenu() {
        mFlingView?.let {
            if (it.scrollX != 0) {
                it.scrollTo(0, 0)
            }
        }
    }
}
