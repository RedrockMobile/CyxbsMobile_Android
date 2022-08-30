package com.mredrock.cyxbs.discover.map.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.discover.map.R

class DetailTagRvAdapter : ListAdapter<String, DetailTagRvAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return true
        }
    
        override fun getChangePayload(oldItem: String, newItem: String): Any {
            return "" // 不返回 null 可以避免 ViewHolder 互换，减少性能消耗
        }
    }
) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tag: TextView = view.findViewById<TextView>(R.id.map_tv_recycle_item_detail_tag).apply { pressToZoomOut() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.map_recycle_item_detail_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tag.text = getItem(position)
    }
}