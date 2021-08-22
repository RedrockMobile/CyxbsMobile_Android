package com.mredrock.cyxbs.widget.service

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.WIDGET_TODO_RAW
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.bean.RemindMode
import com.mredrock.cyxbs.widget.bean.Todo
import com.mredrock.cyxbs.widget.util.repeatMode2RemindTime

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 */
class TodoWidgetService : RemoteViewsService() {

    lateinit var todoList: List<Todo>

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        intent?.let {
            todoList = Gson().fromJson<List<Todo>>(
                it.getStringExtra("todoJson"),
                object : TypeToken<List<Todo>>() {}.type
            )
        }
        LogUtils.d("MasterRay", "factory init")
        return TodoWidgetFactory(todoList)
    }

    inner class TodoWidgetFactory(var todoList: List<Todo>) : RemoteViewsFactory {
        override fun onCreate() {

        }

        override fun onDataSetChanged() {
            val json = BaseApp.context.defaultSharedPreferences.getString(WIDGET_TODO_RAW, "")
            if (json != "") todoList =
                Gson().fromJson<List<Todo>>(json, object : TypeToken<List<Todo>>() {}.type)
        }

        override fun onDestroy() {
            todoList = emptyList()
        }

        override fun getCount(): Int = todoList.size

        override fun getViewAt(position: Int): RemoteViews {
            val listItem = RemoteViews(baseContext.packageName, R.layout.widget_todo_list_item)
            if (position in todoList.indices) {
                val curTodo = todoList[position]
                listItem.setTextViewText(R.id.widget_tv_todo_title, curTodo.title)
                if (curTodo.remindMode.repeatMode == RemindMode.NONE){
                    listItem.setTextViewText(
                        R.id.widget_todo_notify_time,
                        ""
                    )
                } else {
                    listItem.setTextViewText(
                        R.id.widget_todo_notify_time,
                        repeatMode2RemindTime(curTodo.remindMode)
                    )
                }
            }
            return listItem
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(baseContext.packageName, R.layout.widget_todo_list_item)
        }

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = position.toLong()

        override fun hasStableIds(): Boolean = true

    }
}