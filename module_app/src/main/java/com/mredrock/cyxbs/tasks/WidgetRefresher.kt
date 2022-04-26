package com.mredrock.cyxbs.tasks

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mredrock.cyxbs.R
import androidx.work.*
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.spi.TaskService
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

/**
 *@author ZhiQiang Tu
 *@time 2022/4/21  11:20
 *@signature 我将追寻并获取我想要的答案
 */
@AutoService(TaskService::class)
class WidgetRefresher : TaskService {
    override fun work() {
        WorkManager.getInstance(BaseApp.appContext)
            .enqueue(
                listOf(
                    //先进行一次加急任务
                    OneTimeWorkRequestBuilder<WidgetRefreshWork>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .build(),
                    //加入一个15分钟的周期任务,虽然用处不大,说不定有那么一点用不是。
                    PeriodicWorkRequestBuilder<WidgetRefreshWork>(15.minutes.toJavaDuration()).build(),
                )
            )

    }
}

class WidgetRefreshWork(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    val WIDGET_UPDATA_NOTIFICATION_ID = 10
    val CHANNLE_ID = "widget_update"
    val CHANNEL_NAME = "widget_update_channel"

    override suspend fun doWork(): Result {
        val ctx = context
        widgetList.forEach { (action, pkg) ->
            ctx.sendBroadcast(Intent(action).apply {
                component = ComponentName(ctx, pkg)
            })
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {

        return ForegroundInfo(WIDGET_UPDATA_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {

        val manager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNLE_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(notificationChannel)
        }

        return NotificationCompat.Builder(context, CHANNLE_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }


}

const val littleWidgetPkg = "com.mredrock.cyxbs.widget.widget.little.LittleWidget"
const val littleWidgetAction = "$littleWidgetPkg.init"
val widgetList = mapOf(
    littleWidgetAction to littleWidgetPkg
)