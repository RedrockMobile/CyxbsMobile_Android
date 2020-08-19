package com.mredrock.cyxbs.common.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.ACTIVITY_CLASS
import com.mredrock.cyxbs.common.config.MAIN_LOGIN
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService

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

fun Context.doIfLogin(msg: String? = "此功能", next: () -> Unit) {
    if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
        next()
    } else {
        ServiceManager.getService(IAccountService::class.java).getVerifyService().askLogin(this, "请先登录才能使用${msg}哦~")
    }
}


fun Activity.startLoginActivity(loginConfig: LoginConfig = LoginConfig(isWarnUser = false)) {
    //取消转场动画
    val postcard = ARouter.getInstance().build(MAIN_LOGIN)
            .withTransition(0, 0)
            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    //如果设置了重新启动activity，则传Class过去
    if (loginConfig.isFinish) postcard.withSerializable(ACTIVITY_CLASS, this::class.java)
    //如果需要提示用户未登录
    if (loginConfig.isWarnUser) {
        CyxbsToast.makeText(this, loginConfig.warnMessage, Toast.LENGTH_SHORT).show()
    }
    //正式开始路由
    postcard.navigation(this)
    if (loginConfig.isFinish) finish() // 关闭需要放在跳转之后
}
