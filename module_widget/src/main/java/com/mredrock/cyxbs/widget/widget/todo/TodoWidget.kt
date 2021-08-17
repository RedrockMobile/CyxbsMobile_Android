package com.mredrock.cyxbs.widget.widget.todo

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.BaseAdapter
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.WIDGET_TODO_RAW
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.bean.Todo
import com.mredrock.cyxbs.widget.service.TodoWidgetService


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

    private fun initRemoteView(context: Context): RemoteViews{
        val remoteView = RemoteViews(context.packageName, R.layout.widget_todo)
        val intent = Intent(context, TodoWidgetService::class.java)
        val json = BaseApp.context.defaultSharedPreferences.getString(WIDGET_TODO_RAW, "")
        LogUtils.d("MasterRay", "onInit, json = $json")
        if (json!=""){
            intent.putExtra("todoJson", json)
            remoteView.apply {
                setRemoteAdapter(R.id.widget_lv_todo_list, intent)
            }
        }
        return remoteView
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        LogUtils.d("MasterRay", "onReceive")
        context?.let {
            val manager = AppWidgetManager.getInstance(it)
            val componentName = ComponentName(it,TodoWidget::class.java)
            val remoteView = initRemoteView(it)
            manager.updateAppWidget(componentName, remoteView)
            manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(componentName), R.id.widget_lv_todo_list)
        }
    }
}