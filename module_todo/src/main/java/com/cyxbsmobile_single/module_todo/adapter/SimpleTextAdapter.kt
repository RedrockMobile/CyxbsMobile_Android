package com.cyxbsmobile_single.module_todo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R

/**
 * Author: RayleighZ
 * Time: 2021-08-24 7:19
 */
@Suppress("UNCHECKED_CAST")
abstract class SimpleTextAdapter(private val dataList: ArrayList<String>, val resId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataListCopy: ArrayList<String> = dataList.clone() as ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(resId, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
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

    fun refreshList() {
        val diffRes =
            DiffUtil.calculateDiff(DiffCallBack())
        diffRes.dispatchUpdatesTo(this)
    }

    fun resetAll(newList: List<String>){
        dataListCopy.clear()
        dataListCopy.addAll(newList)
        refreshList()
        dataList.clear()
        dataList.addAll(newList)
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