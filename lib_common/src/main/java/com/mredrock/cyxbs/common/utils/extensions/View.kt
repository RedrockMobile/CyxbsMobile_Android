package com.mredrock.cyxbs.common.utils.extensions

import android.annotation.SuppressLint
import android.view.View
import com.mredrock.cyxbs.common.R

/**
 * Created By jay68 on 2018/8/13.
 */
fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

@SuppressLint("ClickableViewAccessibility")
fun View.onTouch(
        returnValue: Boolean = false,
        handler: (v: View, event: android.view.MotionEvent) -> Unit
) {
    setOnTouchListener { v, event ->
        handler(v, event)
        returnValue
    }
}

fun View.onClick(
        handler: (v: View?) -> Unit
) {
    setOnSingleClickListener { v ->
        handler(v)
    }
}

/**
 * @param interval 毫秒为单位，点击间隔小于这个值监听事件无法生效
 * @param click 具体的点击事件
 */
fun View.setOnSingleClickListener(interval: Long = 500, click: (View) -> Unit) {
    setOnClickListener {
        val tag = getTag(R.id.common_view_click_time) as? Long
        if (System.currentTimeMillis() - (tag ?: 0L) > interval) {
            click(it)
        }
        it.setTag(R.id.common_view_click_time, System.currentTimeMillis())
    }
}
var View.leftPadding: Int
    inline get() = paddingLeft
    set(value) = setPadding(value, paddingTop, paddingRight, paddingBottom)

var View.topPadding: Int
    inline get() = paddingTop
    set(value) = setPadding(paddingLeft, value, paddingRight, paddingBottom)

var View.rightPadding: Int
    inline get() = paddingRight
    set(value) = setPadding(paddingLeft, paddingTop, value, paddingBottom)

var View.bottomPadding: Int
    inline get() = paddingBottom
    set(value) = setPadding(paddingLeft, paddingTop, paddingRight, value)

