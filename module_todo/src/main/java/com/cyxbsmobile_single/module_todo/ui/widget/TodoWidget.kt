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
 * description:
 * author: sanhuzhen
 * date: 2024/8/23 0:04
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
        val remoteView = RemoteViews(appContext.packageName, R.layout.todo_widget_main)
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
                getPendingIntentFlags()
            )
        )
        remoteView.setPendingIntentTemplate(
            R.id.todo_lv_widget_todo_list, PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, TodoWidget::class.java),
                getPendingIntentFlags()
            )
        )
        return remoteView
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        TodoWidgetService.todoList.clear()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        when (intent?.action) {
            // 刷新
            "cyxbs.widget.todo.refresh" -> {
                context?.let {
                    refresh(it)
                }
            }
            // 添加
            "cyxbs.widget.todo.add" -> {
                ARouter.getInstance().build(TODO_ADD_TODO_BY_WIDGET).navigation()
            }

            "cyxbs.widget.todo.check" -> {

            }
        }
    }

    private fun refresh(context: Context){
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, TodoWidget::class.java)
        val remoteView = initRemoteView(context)
        TodoRepository
            .queryAllTodo()
            .doOnError {
                processLifecycleScope.launch(Dispatchers.IO) {

                }
            }
            .unsafeSubscribeBy {
                TodoWidgetService.todoList.apply {
                    clear()
                    addAll(it.data.todoArray)
                }
                manager.updateAppWidget(componentName, remoteView)
                manager.notifyAppWidgetViewDataChanged(
                    manager.getAppWidgetIds(componentName),
                    R.id.todo_lv_widget_todo_list
                )
            }

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getPendingIntentFlags(isMutable: Boolean = false) =
        when {
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