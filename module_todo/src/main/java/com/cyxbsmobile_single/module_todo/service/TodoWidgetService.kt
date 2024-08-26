package com.cyxbsmobile_single.module_todo.service

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.repository.TodoRepository
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.processLifecycleScope
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * description: 小组件中RemoteView的Adapter
 * author: sanhuzhen
 * date: 2024/8/20 17:39
 */
class TodoWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = TodoWidgetFactory()

    class TodoWidgetFactory : RemoteViewsFactory {

        private var todoList: List<Todo> = emptyList()

        override fun onCreate() {
            loadData()
        }

        override fun onDataSetChanged() {
            todoList = emptyList()
            loadData()

        }

        private fun loadData() {
            TodoRepository
                .queryAllTodo()
                .doOnError {
                    // 处理错误的逻辑
                }
                .unsafeSubscribeBy {
                    todoList = it.data.todoArray.filter { todo -> todo.isChecked == 0 }
                }
        }

        override fun onDestroy() {
            todoList = emptyList()
        }

        override fun getCount(): Int = todoList.size

        override fun getViewAt(position: Int): RemoteViews {
            val listItem = RemoteViews(appContext.packageName, R.layout.todo_widget_todo_list_item)
            return listItem.apply {
                setTextViewText(R.id.todo_tv_widget_todo_title, todoList[position].title)
                setTextViewText(R.id.todo_widget_notify_time, todoList[position].endTime)
            }
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(appContext.packageName, R.layout.todo_widget_todo_list_item)
        }

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = position.toLong()

        override fun hasStableIds(): Boolean = true
    }
}