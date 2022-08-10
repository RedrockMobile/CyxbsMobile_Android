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
 * @Description: 不同版本杀掉activity的方法（参考至https://github.com/android-notes/Cockroach）
 */
class ActivityKillerV28 : IActivityKiller {
    override fun finishActivity(message: Message) {
        try {
            tryFinish1(message)
            return
        } catch (throwable: Throwable) {
            try {
                tryFinish2(message)
                return
            } catch (throwable: Throwable) {
                throw throwable
            }
        }
    }

    private fun tryFinish1(message: Message) {
        val clientTransaction = message.obj
        val getActivityTokenMethod =
            clientTransaction.javaClass.getDeclaredMethod("getActivityToken")
        val binder = getActivityTokenMethod.invoke(clientTransaction) as IBinder
        finish(binder)
    }

    private fun tryFinish2(message: Message) {
        val clientTransaction = message.obj
        val mActivityTokenField = clientTransaction.javaClass.getDeclaredField("mActivityToken")
        val binder = mActivityTokenField[clientTransaction] as IBinder
        finish(binder)
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun finish(binder: IBinder) {

        val getServiceMethod = ActivityManager::class.java.getDeclaredMethod("getService")
        val activityManager = getServiceMethod.invoke(null)!!
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