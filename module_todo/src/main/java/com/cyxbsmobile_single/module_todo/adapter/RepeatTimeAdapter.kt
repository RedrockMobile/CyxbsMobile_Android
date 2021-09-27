package com.cyxbsmobile_single.module_todo.adapter

import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.mredrock.cyxbs.common.utils.LogUtils
import kotlinx.android.synthetic.main.todo_rv_item_repeat_time_item.view.*

/**
 * Author: RayleighZ
 * Time: 2021-08-24 3:21
 */
@Suppress("UNCHECKED_CAST")
class RepeatTimeAdapter(val dataList: ArrayList<String>, private val onCancel: (Int)->Unit) :
    SimpleTextAdapter(dataList, R.layout.todo_rv_item_repeat_time_item) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            todo_tv_repeat_time.text = dataList[position]
            todo_iv_repeat_time_cancel.setOnClickListener {
                onCancel.invoke(dataList.indexOf(todo_tv_repeat_time.text.toString()))
                dataListCopy.remove(todo_tv_repeat_time.text.toString())
                refreshList()
                dataList.remove(todo_tv_repeat_time.text.toString())
            }
        }
    }
}