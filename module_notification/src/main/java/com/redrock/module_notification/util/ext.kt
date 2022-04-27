package com.redrock.module_notification.util

import android.animation.ValueAnimator
import android.content.Context
import android.view.Window
import com.redrock.module_notification.util.Constant.NOTIFICATION_SP_FILE_NAME

/**
 * Author by OkAndGreat
 * Date on 2022/4/26 16:13.
 * 扩展函数类
 */

val Context.NotificationSp
get() = getSharedPreferences(NOTIFICATION_SP_FILE_NAME, Context.MODE_PRIVATE)

fun Window.changeWindowAlpha(targetWindowAlpha: Float){
    val windowAttrs = attributes
    val curWindowAlpha = windowAttrs.alpha

    val anim = ValueAnimator
        .ofFloat(curWindowAlpha, targetWindowAlpha)
        .setDuration(300)

    anim.addUpdateListener {
        val curVal = it.animatedValue as Float
        windowAttrs.alpha = curVal
        attributes = windowAttrs
    }
    anim.start()
}

