package com.mredrock.cyxbs.widget.widget.oversize

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.service.GridWidgetService
import com.mredrock.cyxbs.widget.widget.page.oversized.deleteTitlePref
import java.util.*


class OversizedAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        updateAppWidget(context, appWidgetManager, appWidgetId)
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
    val weekDay = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
    for (i in 1..7) {
        val i1 = if (i == weekDay) View.VISIBLE else View.INVISIBLE
        remoteViews.setViewVisibility(getShadowId(i), i1)
    }
    val intent = Intent(context, GridWidgetService::class.java).apply {
        putExtra("time", if (weekDay == 1) 6 else weekDay - 2)
        //因为如果每次更新的时候都是一个Intent那么onGetViewFactory只会执行一次
        //这样更新就不会生效，因为RemoteView中的GridView只会局部更新,这样处理并不是很好
        //如果谁有好办法，希望尽快更换
        var int = context.defaultSharedPreferences.getInt("type", 0)
        context.defaultSharedPreferences.editor { putInt("type", ++int) }
        type = int.toString()
    }
    remoteViews.setRemoteAdapter(R.id.grid_course_widget, intent)
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
}

fun getShadowId(num: Int) = when (num) {
    //因为Calendar这个类里面的星期是从周日开始记的
    2 -> R.id.oversize_shadow_1
    3 -> R.id.oversize_shadow_2
    4 -> R.id.oversize_shadow_3
    5 -> R.id.oversize_shadow_4
    6 -> R.id.oversize_shadow_5
    7 -> R.id.oversize_shadow_6
    1 -> R.id.oversize_shadow_7
    else -> R.id.oversize_shadow_1
}