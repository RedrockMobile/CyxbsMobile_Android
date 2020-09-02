package com.mredrock.cyxbs.qa.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by yyfbe, Date on 2020/3/20.
 */
class HackyViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    constructor(context: Context) : this(context, null)

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return try {
            super.onInterceptHoverEvent(event)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }
    }
}
