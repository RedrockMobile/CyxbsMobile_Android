package com.mredrock.cyxbs.qa.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout

class CoordinatorLayoutTouch(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : CoordinatorLayout(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0)
    constructor(context: Context): this(context, null, 0)

    var isReplyEdit = false
    var onReplyCancelEvent : () -> Unit = {}

    // 如果正在回复，则拦截触摸事件
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if(isReplyEdit) {
            true
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if(isReplyEdit) {
            onReplyCancelEvent.invoke()
            isReplyEdit = false
            true
        } else {
            super.onTouchEvent(ev)
        }
    }
}