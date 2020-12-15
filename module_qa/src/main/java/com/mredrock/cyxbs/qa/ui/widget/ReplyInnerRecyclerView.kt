package com.mredrock.cyxbs.qa.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class ReplyInnerRecyclerView : RecyclerView {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, defStyleAttr: Int) : super(context, null, defStyleAttr)
    constructor(context: Context) : super(context, null, 0)

    init {
        isNestedScrollingEnabled = false
    }
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        parent.requestDisallowInterceptTouchEvent(true)
//        return super.dispatchTouchEvent(ev)
//    }

}