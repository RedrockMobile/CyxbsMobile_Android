package com.cyxbsmobile_single.module_todo.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import kotlinx.android.synthetic.main.todo_rv_item_simple_text.view.*

/**
 * Author: RayleighZ
 * Time: 2021-08-24 7:24
 */
class RepeatInnerAdapter(
    private val dataList: ArrayList<String>,
    private val onItemClick: (view: View) -> Unit
) : SimpleTextAdapter(dataList, R.layout.todo_rv_item_simple_text) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            todo_tv_simple_text.text = dataList[position]
            if (dataList[position] == "设置提醒时间"){
                todo_tv_simple_text.setTextColor(ContextCompat.getColor(context, R.color.todo_inner_add_thing_et_hint_color))
            } else {
                todo_tv_simple_text.setTextColor(ContextCompat.getColor(context, R.color.todo_check_line_color))
            }
            setOnClickListener {
                onItemClick.invoke(it)
            }
        }
    }
}