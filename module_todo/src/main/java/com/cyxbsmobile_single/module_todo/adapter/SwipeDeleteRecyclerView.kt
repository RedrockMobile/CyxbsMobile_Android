package com.cyxbsmobile_single.module_todo.adapter


import TodoAllAdapter
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
    private var mLastY: Float = 0f
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
                mLastY = y.toFloat()
                mPosition = pointToPosition(x, y)
                if (mPosition != INVALID_POSITION) {
                    mFlingView = getChildAt(mPosition) as ViewGroup
                    setupMenuWidth(mPosition) // 传递当前项的position
                }
                return super.onInterceptTouchEvent(e)
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - mFirstX
                val deltaY = y - mFirstY

                if (Math.abs(deltaX) > mTouchSlop && Math.abs(deltaX) > Math.abs(deltaY)) {
                    mIsSlide = true
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }

                if (Math.abs(deltaY) > mTouchSlop) {
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
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
                            val newScrollX = (it.scrollX + dx.toInt()).coerceIn(0, mMenuViewWidth)
                            it.scrollTo(newScrollX, 0)
                            mLastX = x
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (mMenuViewWidth != 0) {
                        val scrollX = mFlingView?.scrollX ?: 0
                        mVelocityTracker?.computeCurrentVelocity(1000)
                        val finalScrollX: Int
                        val velocityX = mVelocityTracker?.xVelocity ?: 0f
                        when {
                            velocityX < -SNAP_VELOCITY -> finalScrollX = mMenuViewWidth
                            velocityX >= SNAP_VELOCITY -> finalScrollX = 0
                            scrollX >= mMenuViewWidth / 2 -> finalScrollX = mMenuViewWidth
                            else -> finalScrollX = 0
                        }
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

    // 更新 setupMenuWidth 方法
    private fun setupMenuWidth(position: Int) {
        val adapter = adapter as? TodoAllAdapter
        val item = adapter?.getItem(position)
        if (adapter?.isEnabled==true){
            mMenuViewWidth=0
            return
        }
        mFlingView?.let { flingView ->
            val childCount = flingView.childCount
            if (childCount > 0) {
                val menuView = flingView.getChildAt(childCount - 1)
                val layoutParams = menuView.layoutParams as ViewGroup.MarginLayoutParams

                if (adapter?.pinnedItems?.contains(item) == true) {
                    mMenuViewWidth = menuView.width + layoutParams.leftMargin + layoutParams.rightMargin
                    return
                }

                mMenuViewWidth = menuView.width+menuView.width + layoutParams.leftMargin + layoutParams.rightMargin
            } else {
                mMenuViewWidth = 0
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

    fun isMenuOpen(): Boolean {
        return mFlingView?.scrollX ?: 0 != 0
    }
}

