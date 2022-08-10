package com.mredrock.lib.crashmonitor.activitykiller

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
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
class ActivityKillerV26 : IActivityKiller {
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

    @SuppressLint("DiscouragedPrivateApi")
    @Throws(Exception::class)
    private fun finish(binder: IBinder) {
        val getServiceMethod = ActivityManager::class.java.getDeclaredMethod("getService")
        val activityManager = getServiceMethod.invoke(null)
        val finishActivityMethod = activityManager.javaClass.getMethod(
            "finishActivity",
            IBinder::class.java,
            Int::class.javaPrimitiveType,
            Intent::class.java,
            Int::class.javaPrimitiveType
        )
        finishActivityMethod.isAccessible = true
        val DONT_FINISH_TASK_WITH_ACTIVITY = 0
        finishActivityMethod.invoke(
            activityManager,
            binder,
            Activity.RESULT_CANCELED,
            null,
            DONT_FINISH_TASK_WITH_ACTIVITY
        )
    }
}