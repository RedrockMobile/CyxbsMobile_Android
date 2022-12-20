package com.mredrock.cyxbs.common.utils.extensions

import android.annotation.SuppressLint
import android.view.View
import com.mredrock.cyxbs.common.R

/**
 * Created By jay68 on 2018/8/13.
 */
@Deprecated("使用 lib_utils 中 View#gone() 代替", replaceWith = ReplaceWith(""))
fun View.gone() {
    visibility = View.GONE
}

@Deprecated("使用 lib_utils 中 View#visible() 代替", replaceWith = ReplaceWith(""))
fun View.visible() {
    visibility = View.VISIBLE
}

@Deprecated("使用 lib_utils 中 View#invisible() 代替", replaceWith = ReplaceWith(""))
fun View.invisible() {
    visibility = View.INVISIBLE
}

@Deprecated("过度封装，使用 setOnTouchListener 代替")
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

/**
 * @param interval 毫秒为单位，点击间隔小于这个值监听事件无法生效
 * @param click 具体的点击事件
 */
@Deprecated("使用 lib_utils 中的 View#setOnSingleClickListener() 代替")
fun View.setOnSingleClickListener(interval: Long = 500, click: (View) -> Unit) {
    setOnClickListener {
        val tag = getTag(R.id.common_view_click_time) as? Long
        if (System.currentTimeMillis() - (tag ?: 0L) > interval) {
            click(it)
        }
        it.setTag(R.id.common_view_click_time, System.currentTimeMillis())
    }
}

