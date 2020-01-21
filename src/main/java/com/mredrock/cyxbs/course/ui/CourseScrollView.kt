package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * @author Jon
 * @date 2019/11/6 19:01
 * description：这个ScrollView解决了滑动监听在低版本的问题
 */
class CourseScrollView : ScrollView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        print("")
        val a = super.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
//
//    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        print("")
//        return super.onTouchEvent(ev)
//    }
//
//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        return super.onInterceptTouchEvent(ev)
//    }




    fun setScrollViewListener(scrollViewListener: ScrollViewListener) {
        this.scrollViewListener = scrollViewListener
    }

    private var scrollViewListener: ScrollViewListener? = null
    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        scrollViewListener?.onScrollChanged(this, x, y, oldx, oldy)
    }

    companion object{
        var isCurrentCourseTop = true
    }

    interface ScrollViewListener {
        fun onScrollChanged(scrollView: CourseScrollView, x: Int, y: Int, oldx: Int, oldy: Int)
    }
}
