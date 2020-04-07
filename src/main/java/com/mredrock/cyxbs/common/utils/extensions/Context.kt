package com.mredrock.cyxbs.common.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.mredrock.cyxbs.common.component.CyxbsToast

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

fun Context.dp2px(dpValue: Float) = (dpValue * resources.displayMetrics.density + 0.5f).toInt()

fun Activity.setFullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val lp = window.attributes
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = lp
    }

    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
}

fun Context.toast(message: CharSequence) = CyxbsToast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toast(res: Int) = CyxbsToast.makeText(this, res, Toast.LENGTH_SHORT).show()

fun Context.getDarkModeStatus(): Boolean {
    val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mode == Configuration.UI_MODE_NIGHT_YES
}

/**
 * 获取状态栏高度
 */
fun Context.getStatusBarHeight(): Int {
    val resources: Resources = resources
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

