package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView


class CourseScrollView : ScrollView {
    var isTop = true
    var isSlideDowm = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        return if (isTop && isSlideDowm) {
//            false
//        } else {
//            super.onInterceptTouchEvent(ev)
//        }
////        return false
//    }

//    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        if (isTop && isSlideDowm) {
//            parent.requestDisallowInterceptTouchEvent(true)
//            return false
//        } else {
//            return super.onTouchEvent(ev)
//        }
//    }

    fun setScrollViewListener(scrollViewListener: ScrollViewListener) {
        this.scrollViewListener = scrollViewListener
    }

    private var scrollViewListener: ScrollViewListener? = null
    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        isTop = y == 0
        isSlideDowm = y - oldy > 0
        scrollViewListener?.onScrollChanged(this, x, y, oldx, oldy)
    }

    companion object{
        var isCurrentCourseTop = true
    }

    interface ScrollViewListener {
        fun onScrollChanged(scrollView: CourseScrollView, x: Int, y: Int, oldx: Int, oldy: Int)
    }
}
