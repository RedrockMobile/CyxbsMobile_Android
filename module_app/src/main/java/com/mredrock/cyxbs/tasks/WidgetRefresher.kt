package com.mredrock.cyxbs.tasks

import android.content.ComponentName
import android.content.Intent
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
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
                PeriodicWorkRequestBuilder<WidgetRefreshWork>(10.minutes.toJavaDuration()).build()
            )

    }
}

class WidgetRefreshWork(workerParams: WorkerParameters) : Worker(BaseApp.appContext, workerParams) {
    override fun doWork(): Result {
        val ctx = BaseApp.appContext
        widgetList.forEach { (action, pkg) ->
            ctx.sendBroadcast(Intent(action).apply {
                component = ComponentName(ctx,pkg)
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