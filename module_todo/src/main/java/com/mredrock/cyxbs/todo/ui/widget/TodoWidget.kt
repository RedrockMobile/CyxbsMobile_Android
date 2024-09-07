package com.mredrock.cyxbs.todo.ui.widget

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
import com.mredrock.cyxbs.todo.service.TodoWidgetService
import com.mredrock.cyxbs.config.route.TODO_ADD_TODO_BY_WIDGET
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.todo.R

/**
 * description: 待办事项桌面小组件
 * author: sanhuzhen
 * date: 2024/8/23 0:04
 */
class TodoWidget : AppWidgetProvider() {
    companion object{
        fun sendAddTodoBroadcast(context: Context) {
            context.sendBroadcast(Intent("cyxbs.widget.todo.refresh").apply {
                component = ComponentName(appContext, TodoWidget::class.java)
            })
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateAppWidgets(context, appWidgetManager, appWidgetIds)
    }

    private fun updateAppWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val remoteView = initRemoteView(context)

        // 更新所有小组件
        appWidgetManager.updateAppWidget(appWidgetIds, remoteView)
    }

    private fun initRemoteView(context: Context): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.todo_widget_main)

        // 设置 RemoteAdapter 以显示 ListView
        val intent = Intent(context, TodoWidgetService::class.java)
        remoteView.setRemoteAdapter(R.id.todo_lv_widget_todo_list, intent)

        // 设置添加任务的 PendingIntent
        val addIntent = Intent(context, TodoWidget::class.java).apply {
            action = "cyxbs.widget.todo.add"
        }
        remoteView.setOnClickPendingIntent(R.id.todo_iv_widget_add_todo,
            PendingIntent.getBroadcast(context, 0, addIntent, getPendingIntentFlags())
        )

        // 设置 ListView 的点击事件
        remoteView.setPendingIntentTemplate(
            R.id.todo_lv_widget_todo_list,
            PendingIntent.getBroadcast(context, 0, Intent(context, TodoWidget::class.java), getPendingIntentFlags())
        )

        return remoteView
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        when (intent?.action) {
            "cyxbs.widget.todo.refresh" -> {
                context?.let { refresh(it) }
            }
            "cyxbs.widget.todo.add" -> {
                // 启动添加任务的界面或 Activity
                ARouter.getInstance().build(TODO_ADD_TODO_BY_WIDGET).navigation()
            }
        }
    }

    private fun refresh(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, TodoWidget::class.java)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.todo_lv_widget_todo_list,)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getPendingIntentFlags(isMutable: Boolean = false): Int {
        return when {
            isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            !isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                PendingIntent.FLAG_UPDATE_CURRENT
            !isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                PendingIntent.FLAG_CANCEL_CURRENT
            else -> PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
}