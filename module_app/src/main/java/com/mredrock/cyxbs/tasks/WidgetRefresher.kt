package com.mredrock.cyxbs.tasks

import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
                    OneTimeWorkRequestBuilder<WidgetRefreshWork>().setExpedited(
                        OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
                    ).build(),
                    PeriodicWorkRequestBuilder<WidgetRefreshWork>(15.minutes.toJavaDuration())
                        .build(),
                )
            )

    }
}

class WidgetRefreshWork(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val ctx = context
        widgetList.forEach { (action, pkg) ->
            ctx.sendBroadcast(Intent(action).apply {
                component = ComponentName(ctx, pkg)
            })
        }
        return Result.success()
    }

}

const val littleWidgetPkg = "com.mredrock.cyxbs.widget.widget.little.LittleWidget"
const val littleWidgetAction = "$littleWidgetPkg.init"
val widgetList = mapOf(
    littleWidgetAction to littleWidgetPkg

)