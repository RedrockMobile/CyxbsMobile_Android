package com.cyxbsmobile_single.module_todo.service

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.Todo

/**
 * description: 小组件中RemoteView的Adapter
 * author: sanhuzhen
 * date: 2024/8/20 17:39
 */
class TodoWidgetService: RemoteViewsService() {

    companion object {
        var todoList: ArrayList<Todo> = ArrayList()
    }
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = TodoWidgetFactory()

    inner class TodoWidgetFactory: RemoteViewsFactory {

        private var todoList: List<Todo> = emptyList()

        override fun onCreate() {
            todoList = TodoWidgetService.todoList
        }

        override fun onDataSetChanged() {
            todoList = TodoWidgetService.todoList
        }

        override fun onDestroy() {
            todoList = emptyList()
        }

        override fun getCount(): Int {
            return todoList.size
        }

        override fun getViewAt(p0: Int): RemoteViews {
            val listItem = RemoteViews(applicationContext.packageName, R.layout.todo_widget_todo_list_item)
            return listItem.apply {
                setTextViewText(R.id.todo_tv_widget_todo_title, todoList[p0].title)
                if (todoList[p0].remindMode.notifyDateTime == ""){
                    setTextViewText(R.id.todo_widget_notify_time, todoList[p0].remindMode.notifyDateTime)
                } else {

                }

            }
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(baseContext.packageName, R.layout.todo_widget_todo_list_item)
        }

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(p0: Int): Long = p0.toLong()

        override fun hasStableIds(): Boolean = true

    }
}