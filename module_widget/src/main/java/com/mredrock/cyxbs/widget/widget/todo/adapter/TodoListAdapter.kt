package com.mredrock.cyxbs.widget.widget.todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.bean.Todo
import com.mredrock.cyxbs.widget.util.remindTimeStamp2String
import kotlinx.android.synthetic.main.widget_todo_list_item.view.*

/**
 * @date 2021-08-17
 * @author Sca RayleighZ
 */
class TodoListAdapter(context: Context, itemRes: Int, todoList: List<Todo>)
    : ArrayAdapter<Todo>(context, itemRes, todoList) {

    lateinit var view: View

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val todo = getItem(position)
        view = convertView ?: LayoutInflater.from(context).inflate(R.layout.widget_todo_list_item, parent, false)
        todo?.let {
            view.apply {
                widget_tv_todo_title.text = it.title
                widget_todo_notify_time.text = remindTimeStamp2String(it.remindTime)
            }
        }
        return view
    }
}