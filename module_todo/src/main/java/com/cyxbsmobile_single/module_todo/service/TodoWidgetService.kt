package com.cyxbsmobile_single.module_todo.service

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.cyxbsmobile_single.module_todo.ui.widget.TodoWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * description: 小组件中RemoteView的Adapter
 * author: sanhuzhen
 * date: 2024/8/20 17:39
 */
class TodoWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TodoWidgetFactory(applicationContext)
    }

    class TodoWidgetFactory(val context: Context) : RemoteViewsFactory {
        private var todoList: List<Todo> = emptyList()

        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        override fun onCreate() {
            // 初始化数据
            loadData()
        }

        override fun onDataSetChanged() {
            // 数据集变化时加载数据
            loadData()
        }

        private fun loadData() {
            // 使用协程获取数据
            scope.launch {

                val newTodoList = TodoDatabase.instance.todoDao().queryAll()!!.filter { it.isChecked == 0 }.sortedByDescending { it.todoId } // 获取待办事项列表

                // 检查数据是否发生变化
                if (newTodoList != todoList) {
                    todoList = newTodoList
                    // 更新UI线程
                    withContext(Dispatchers.Main) {
                        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(
                            AppWidgetManager.getInstance(context)
                                .getAppWidgetIds(ComponentName(context, TodoWidget::class.java)),
                            R.id.todo_lv_widget_todo_list
                        )
                    }
                }
            }
        }

        override fun onDestroy() {
            // 清理操作
            scope.cancel()
        }

        override fun getCount(): Int = todoList.size

        override fun getViewAt(position: Int): RemoteViews {
            val item = RemoteViews(context.packageName, R.layout.todo_widget_todo_list_item)
            item.setTextViewText(R.id.todo_tv_widget_todo_title, todoList[position].title)
            return item
        }

        override fun getLoadingView(): RemoteViews? = null

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = position.toLong()

        override fun hasStableIds(): Boolean = true
    }
}