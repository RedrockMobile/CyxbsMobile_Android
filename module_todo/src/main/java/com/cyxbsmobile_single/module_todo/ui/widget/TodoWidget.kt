package com.cyxbsmobile_single.module_todo.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.alibaba.android.arouter.launcher.ARouter
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.service.TodoWidgetService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TODO_ADD_TODO_BY_WIDGET
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences


/**
 * Author: RayleighZ
 * Time: 2021-07-31 19:49
 */
class TodoWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        context?.let {
            val remoteView = initRemoteView(it)
            appWidgetManager?.updateAppWidget(appWidgetIds, remoteView)
        }
    }

    private fun initRemoteView(context: Context): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.todo_widget_main)
        val intent = Intent(context, TodoWidgetService::class.java)
        remoteView.apply {
            setRemoteAdapter(R.id.todo_lv_widget_todo_list, intent)
        }
        remoteView.setOnClickPendingIntent(
            R.id.todo_iv_widget_add_todo,
            PendingIntent.getBroadcast(
                context,
                1,
                Intent(context, TodoWidget::class.java).apply {
                    action = "cyxbs.widget.todo.add"
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        return remoteView
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.action?.let { LogUtils.d("RayleighZ", it) }
        when (intent?.action) {
            "cyxbs.widget.todo.refresh" -> {
                context?.let {
                    val manager = AppWidgetManager.getInstance(it)
                    val componentName = ComponentName(it, TodoWidget::class.java)
                    val remoteView = initRemoteView(it)
                    manager.updateAppWidget(componentName, remoteView)
                    manager.notifyAppWidgetViewDataChanged(
                        manager.getAppWidgetIds(componentName),
                        R.id.todo_lv_widget_todo_list
                    )
                }
            }

            "cyxbs.widget.todo.add" -> {
                ARouter.getInstance().build(TODO_ADD_TODO_BY_WIDGET).navigation()
            }
        }
    }
}