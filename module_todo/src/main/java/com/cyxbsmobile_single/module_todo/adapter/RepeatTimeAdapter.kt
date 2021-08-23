package com.cyxbsmobile_single.module_todo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import kotlinx.android.synthetic.main.todo_inner_add_thing_repeat_select_auto_warp_item.view.*

/**
 * Author: RayleighZ
 * Time: 2021-08-24 3:21
 */
@Suppress("UNCHECKED_CAST")
class RepeatTimeAdapter(private val dataList: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataListCopy: ArrayList<String> = dataList.clone() as ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_inner_add_thing_repeat_select_auto_warp_item, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            todo_tv_repeat_time.text = dataList[position]
            todo_iv_repeat_time_cancel.setOnClickListener {
                dataListCopy.remove(dataList[position])
                refreshList()
                dataList.remove(dataList[position])
            }
        }
    }

    fun addString(value: String) {
        if (!dataListCopy.contains(value)){
            dataListCopy.add(0, value)
            refreshList()
            dataList.add(0,value)
        }
    }

    fun removeAll(){
        dataListCopy.clear()
        refreshList()
        dataList.clear()
    }

    private fun refreshList() {
        val diffRes =
            DiffUtil.calculateDiff(DiffCallBack())
        diffRes.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = dataListCopy.size

    inner class DiffCallBack : DiffUtil.Callback() {
        override fun getOldListSize(): Int = dataList.size

        override fun getNewListSize(): Int = dataListCopy.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = true

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            dataList[oldItemPosition] == dataListCopy[newItemPosition]
    }
}