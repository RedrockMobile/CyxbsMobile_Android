package com.mredrock.cyxbs.freshman.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Create by yuanbing
 * on 2019/8/6
 */
class ScrollEnableViewPager(
        context: Context,
        attrs: AttributeSet
) : ViewPager(
        context,
        attrs
) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?) = false

    override fun onInterceptTouchEvent(ev: MotionEvent?) = false
}