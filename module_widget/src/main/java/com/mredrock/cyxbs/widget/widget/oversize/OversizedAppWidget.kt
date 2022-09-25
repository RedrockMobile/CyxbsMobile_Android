package com.mredrock.cyxbs.widget.widget.oversize

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.edit
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.service.GridWidgetService
import com.mredrock.cyxbs.widget.util.*
import com.mredrock.cyxbs.widget.widget.page.oversized.deleteTitlePref
import java.util.*

/**
 * 超大小部件
 */
class OversizedAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            appWidgetManager.getAppWidgetOptions(appWidgetId)
                ?.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES)
        }
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    @SuppressLint("ApplySharedPref")
    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        intent ?: return
        when (intent.action) {
            /** 点击事件*/
            ACTION_CLICK -> {
                val lastPosition = defaultSp.getInt(POSITION, -1)
                val position = intent.getIntExtra(POSITION, -1)
                val day = position / 7
                val beginLesson = position % 7
                if (lastPosition == position) {
                    defaultSp.edit().putInt(POSITION, -1).commit()
                } else {
                    defaultSp.edit().putInt(POSITION, position).commit()
                }
            }
            ACTION_FLUSH -> {
                val manager = AppWidgetManager.getInstance(context)
                val ids =
                    manager.getAppWidgetIds(ComponentName(context, OversizedAppWidget::class.java))
                manager.notifyAppWidgetViewDataChanged(ids, R.id.grid_course_widget)
            }
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_oversized_app_widget)
            val weekDay = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
            for (i in 1..7) {
                val i1 = if (i == weekDay) View.VISIBLE else View.INVISIBLE
                remoteViews.setViewVisibility(getShadowId(i), i1)
            }
            val gridWidgetIntent = Intent(context, GridWidgetService::class.java).apply {
                putExtra("time", if (weekDay == 1) 6 else weekDay - 2)
                //因为如果每次更新的时候都是一个Intent那么onGetViewFactory只会执行一次
                //这样更新就不会生效，因为RemoteView中的GridView只会局部更新,这样处理并不是很好
                //如果谁有好办法，希望尽快更换
                var int = defaultSp.getInt("type", 0)
                defaultSp.edit { putExtra("type", ++int) }
                type = int.toString()
            }
            remoteViews.setPendingIntentTemplate(R.id.grid_course_widget,
                getClickIntent(context,
                    appWidgetId,
                    R.id.grid_course_widget,
                    3,
                    ACTION_CLICK,
                    OversizedAppWidget::class.java))
            remoteViews.setRemoteAdapter(R.id.grid_course_widget, gridWidgetIntent)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }
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