package com.mredrock.cyxbs.course.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import kotlin.math.abs

/**
 * @author Jovines
 * @date 2019/11/6 19:01
 * description：解决Scroll与Bottom的滑动冲突
 */
class CourseScrollView : ScrollView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var downX: Float? = null
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return super.dispatchTouchEvent(ev)
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> downX = ev.rawX
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> downX = null
        }
        if (scrollY == 0) {
            parent.requestDisallowInterceptTouchEvent(false)
        } else {
            downX?.let {
                if (ev.action == MotionEvent.ACTION_MOVE && abs((ev.rawX - it)) > 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
