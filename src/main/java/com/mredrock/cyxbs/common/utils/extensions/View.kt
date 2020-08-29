package com.mredrock.cyxbs.common.utils.extensions

import android.annotation.SuppressLint
import android.view.View

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
    setOnClickListener { v ->
        handler(v)
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
