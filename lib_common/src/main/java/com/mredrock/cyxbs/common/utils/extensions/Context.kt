package com.mredrock.cyxbs.common.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.ACTIVITY_CLASS
import com.mredrock.cyxbs.main.MAIN_LOGIN
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.utils.Internals

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

fun Context.longToast(message: CharSequence) = CyxbsToast
        .makeText(this, message, Toast.LENGTH_LONG)
        .show()

fun Context.longToast(res: Int) = CyxbsToast
        .makeText(this, res, Toast.LENGTH_LONG)
        .show()


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


//anko-bridge
//anko不再维护，删除anko，一些从anko拿过来的扩展方法
fun Context.dp2px(dpValue: Float) = (dpValue * resources.displayMetrics.density + 0.5f).toInt()

//returns dip(dp) dimension value in pixels
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

//converts px value into dip or sp
fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)


fun Context.runOnUiThread(f: Context.() -> Unit) {
    if (Looper.getMainLooper() === Looper.myLooper()) f() else Handler(Looper.getMainLooper()).post { f() }
}

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) =
        Internals.internalStartActivity(this, T::class.java, params)

inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) =
        Internals.internalStartActivityForResult(this, T::class.java, requestCode, params)

inline fun <reified T : Activity> Fragment.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) {
    startActivityForResult(Internals.createIntent(requireActivity(), T::class.java, params), requestCode)
}

/**
 * Returns the content view of this Activity if set, null otherwise.
 */
inline val Activity.contentView: View?
    get() = findOptional<ViewGroup>(android.R.id.content)?.getChildAt(0)

inline fun <reified T : View> Activity.findOptional(@IdRes id: Int): T? = findViewById(id) as? T

inline fun <reified T : Any> Context.intentFor(vararg params: Pair<String, Any?>): Intent =
        Internals.createIntent(this, T::class.java, params)
