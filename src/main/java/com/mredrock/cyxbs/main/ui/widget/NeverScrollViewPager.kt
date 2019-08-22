package com.mredrock.cyxbs.main.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Create By Hosigus at 2019/3/11
 */
class NeverScrollViewPager(context: Context, attrs: AttributeSet? = null) : androidx.viewpager.widget.ViewPager(context, attrs) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, false)
    }
}