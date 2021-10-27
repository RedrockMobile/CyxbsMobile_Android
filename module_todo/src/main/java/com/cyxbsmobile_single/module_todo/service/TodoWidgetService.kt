package com.cyxbsmobile_single.module_todo.service

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.TodoModel
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.widget.TodoWidget
import com.cyxbsmobile_single.module_todo.util.repeatMode2RemindTime
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * @describe 小组件中RemoteView的Adapter
 */
class TodoWidgetService : RemoteViewsService() {

    companion object{
        val todoList: ArrayList<Todo> = ArrayList()
    }

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TodoWidgetFactory()
    }

    inner class TodoWidgetFactory : RemoteViewsFactory {

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

        override fun getCount(): Int = todoList.size

        override fun getViewAt(position: Int): RemoteViews {
            val listItem = RemoteViews(baseContext.packageName, R.layout.todo_widget_todo_list_item)
            LogUtils.d("RayleighZ", "position = $position")
            listItem.setOnClickFillInIntent(
                R.id.todo_ll_widget_back,
                Intent(BaseApp.context, TodoWidget::class.java).apply {
                    action = "cyxbs.widget.todo.jump"
                    putExtra("todo", Gson().toJson(todoList[position]))
                }
            )

            listItem.setOnClickFillInIntent(
                R.id.todo_iv_widget_check,
                Intent(BaseApp.context, TodoWidget::class.java).apply {
                    action = "cyxbs.widget.todo.check"
                    putExtra("todo", Gson().toJson(todoList[position]))
                }
            )
            if (position in todoList.indices) {
                val curTodo = todoList[position]
                listItem.setTextViewText(R.id.todo_tv_widget_todo_title, curTodo.title)
                if (curTodo.remindMode.repeatMode == RemindMode.NONE) {
                    listItem.setTextViewText(
                        R.id.todo_widget_notify_time,
                        ""
                    )
                } else {
                    listItem.setTextViewText(
                        R.id.todo_widget_notify_time,
                        repeatMode2RemindTime(curTodo.remindMode)
                    )
                }
            }
            return listItem
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(baseContext.packageName, R.layout.todo_widget_todo_list_item)
        }

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = position.toLong()

        override fun hasStableIds(): Boolean = true

    }
}