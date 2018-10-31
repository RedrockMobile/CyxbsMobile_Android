package com.mredrock.cyxbs.discover.electricity.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_item_dormitory.view.*

/**
 * Author: Hosigus
 * Date: 2018/9/17 0:02
 * Description: com.mredrock.cyxbs.electricity.ui.adapter
 */
class DormitoryAdapter(private val nameList: Array<String>, private val select: (pos: Int) -> Unit) : RecyclerView.Adapter<DormitoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.electricity_item_dormitory, parent, false)
    )

    override fun getItemCount() = nameList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            tv_item_building.text = nameList[position]
            setOnClickListener { select(position) }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}