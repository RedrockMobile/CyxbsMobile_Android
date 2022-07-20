package com.cyxbsmobile_single.module_todo.service

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.widget.TodoWidget
import com.cyxbsmobile_single.module_todo.util.isOutOfTime
import com.cyxbsmobile_single.module_todo.util.repeatMode2RemindTime
import com.google.gson.Gson
import com.mredrock.cyxbs.common.utils.LogUtils

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * @describe 小组件中RemoteView的Adapter
 */
class TodoWidgetService : RemoteViewsService() {

    companion object {
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
                Intent(this@TodoWidgetService, TodoWidget::class.java).apply {
                    action = "cyxbs.widget.todo.jump"
                    putExtra("todo", Gson().toJson(todoList[position]))
                }
            )

            listItem.setOnClickFillInIntent(
                R.id.todo_iv_widget_check,
                Intent(this@TodoWidgetService, TodoWidget::class.java).apply {
                    action = "cyxbs.widget.todo.check"
                    putExtra("todo", Gson().toJson(todoList[position]))
                }
            )
            if (position in todoList.indices) {
                val curTodo = todoList[position]
                //过期判断
                if (isOutOfTime(curTodo)) {
                    //如果过期，则标红
                    listItem.setImageViewResource(
                        R.id.todo_iv_widget_check,
                        R.drawable.todo_ic_detail_check_eclipse_out_date
                    )
                    listItem.setTextColor(
                        R.id.todo_tv_widget_todo_title,
                        ContextCompat.getColor(this@TodoWidgetService, R.color.todo_item_del_red)
                    )
                    listItem.setTextViewText(R.id.todo_widget_notify_time, "")
                } else {
                    //恢复为普通状态
                    listItem.setImageViewResource(
                        R.id.todo_iv_widget_check,
                        R.drawable.todo_ic_detail_check_eclipse
                    )
                    listItem.setTextColor(
                        R.id.todo_tv_widget_todo_title,
                        ContextCompat.getColor(this@TodoWidgetService, com.mredrock.cyxbs.common.R.color.common_level_one_font_color)
                    )
                    listItem.setTextViewText(
                        R.id.todo_widget_notify_time,
                        repeatMode2RemindTime(curTodo.remindMode)
                    )
                }
                listItem.setTextViewText(R.id.todo_tv_widget_todo_title, curTodo.title)
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