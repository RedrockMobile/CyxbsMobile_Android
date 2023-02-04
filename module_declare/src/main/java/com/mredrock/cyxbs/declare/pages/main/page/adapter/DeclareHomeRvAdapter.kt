package com.mredrock.cyxbs.declare.pages.main.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.declare.R
import com.mredrock.cyxbs.declare.pages.main.HomeDataBean

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
class DeclareHomeRvAdapter() : ListAdapter<HomeDataBean, DeclareHomeRvAdapter.InnerViewHolder>(
    object : DiffUtil.ItemCallback<HomeDataBean>() {
        override fun areItemsTheSame(oldItem: HomeDataBean, newItem: HomeDataBean): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: HomeDataBean, newItem: HomeDataBean): Boolean =
            oldItem.title == newItem.title

    }
) {
    inner class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView

        init {
            titleTv = itemView.findViewById(R.id.declare_home_item_title)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder =
        InnerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.declare_item_home_recyclerview, parent, false)
        )

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.titleTv.text = getItem(position).title
    }
}