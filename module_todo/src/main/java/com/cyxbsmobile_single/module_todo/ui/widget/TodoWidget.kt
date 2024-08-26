package com.cyxbsmobile_single.module_todo.ui.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.alibaba.android.arouter.launcher.ARouter
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.repository.TodoRepository
import com.cyxbsmobile_single.module_todo.service.TodoWidgetService
import com.mredrock.cyxbs.config.route.TODO_ADD_TODO_BY_WIDGET
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.processLifecycleScope
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * description: 待办事项桌面小组件
 * author: sanhuzhen
 * date: 2024/8/23 0:04
 */
class TodoWidget : AppWidgetProvider() {

    companion object {
        // 发送刷新广播
        fun sendRefreshBroadcast(context: Context) {
            val intent = Intent("cyxbs.widget.todo.refresh")
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val remoteView = RemoteViews(context.packageName, R.layout.todo_widget_main)
        val intent = Intent(context, TodoWidgetService::class.java)
        remoteView.setRemoteAdapter(R.id.todo_lv_widget_todo_list, intent)

        // 设置点击添加待办事件的PendingIntent
        val addIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, TodoWidget::class.java).apply { action = "cyxbs.widget.todo.add" },
            getPendingIntentFlags()
        )
        remoteView.setOnClickPendingIntent(R.id.todo_iv_widget_add_todo, addIntent)

        // 设置待办项点击事件的PendingIntent模板
        val itemIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, TodoWidget::class.java),
            getPendingIntentFlags()
        )
        remoteView.setPendingIntentTemplate(R.id.todo_lv_widget_todo_list, itemIntent)

        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        when (intent?.action) {
            "cyxbs.widget.todo.refresh" -> {
                context?.let {
                    refreshWidget(it)
                }
            }

            "cyxbs.widget.todo.add" -> {
                ARouter.getInstance().build(TODO_ADD_TODO_BY_WIDGET).navigation()
            }
            // 可添加其他事件的处理
        }
    }

    private fun refreshWidget(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, TodoWidget::class.java)
        val appWidgetIds = manager.getAppWidgetIds(componentName)
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.todo_lv_widget_todo_list)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getPendingIntentFlags(isMutable: Boolean = false): Int {
        return when {
            isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            !isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_UPDATE_CURRENT
            !isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_CANCEL_CURRENT
            else -> PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
}