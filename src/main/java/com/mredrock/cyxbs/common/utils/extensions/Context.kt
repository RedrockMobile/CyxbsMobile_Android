package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

/**
 * Created by anriku on 2018/8/14.
 */

var screenHeight: Int = 0
var screenWidth: Int = 0

fun Context.getScreenHeight(): Int {
    if (screenHeight == 0) {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenHeight = size.y
    }
    return screenHeight
}


fun Context.getScreenWidth(): Int {
    if (screenWidth == 0) {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
    }
    return screenWidth
}

fun Context.dp2px(dpValue : Float) = (dpValue * resources.displayMetrics.density + 0.5f).toInt()