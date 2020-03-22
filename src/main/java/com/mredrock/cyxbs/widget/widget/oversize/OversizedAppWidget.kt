package com.mredrock.cyxbs.widget.widget.oversize

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.widget.page.oversized.deleteTitlePref
import com.mredrock.cyxbs.widget.service.GridWidgetService


class OversizedAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val remoteViews = RemoteViews(context.packageName, R.layout.widget_oversized_app_widget)
    val intent = Intent(context, GridWidgetService::class.java)
    remoteViews.setRemoteAdapter(R.id.grid_course_widget, intent)
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
}