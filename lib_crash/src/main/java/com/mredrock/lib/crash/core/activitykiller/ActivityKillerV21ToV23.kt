package com.mredrock.lib.crash.core.activitykiller

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.IBinder
import android.os.Message

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description:
 */
class ActivityKillerV21ToV23 : IActivityKiller {
    override fun finishActivity(message: Message) {
        try {
            val activityClientRecord = message.obj
            val tokenField = activityClientRecord.javaClass.getDeclaredField("token")
            tokenField.isAccessible = true
            val binder = tokenField[activityClientRecord] as IBinder
            finish(binder)
        } catch (e: Exception) {
            throw e
        }
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun finish(binder: IBinder) {
        val activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative")
        val getDefaultMethod = activityManagerNativeClass.getDeclaredMethod("getDefault")
        val activityManager = getDefaultMethod.invoke(null)
        val finishActivityMethod = activityManager.javaClass.getMethod(
            "finishActivity",
            IBinder::class.java,
            Int::class.javaPrimitiveType,
            Intent::class.java,
            Boolean::class.javaPrimitiveType
        )
        finishActivityMethod.invoke(activityManager, binder, Activity.RESULT_CANCELED, null, false)
    }
}