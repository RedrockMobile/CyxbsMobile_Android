package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * @author Jon
 * @date 2019/11/6 19:01
 * description：解决Scroll与Bottom的滑动冲突
 */
class CourseScrollView : ScrollView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (scrollY == 0) {
            parent.requestDisallowInterceptTouchEvent(false);
        } else {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev)
    }
}
