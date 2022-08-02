package com.mredrock.cyxbs.discover.emptyroom.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.discover.emptyroom.R
import com.mredrock.cyxbs.discover.emptyroom.bean.EmptyRoom
import com.mredrock.cyxbs.discover.emptyroom.ui.widget.NoScrollGridView

/**
 * Created by Cynthia on 2018/9/19
 */
class EmptyRoomResultAdapter(var data: MutableList<EmptyRoom>, private val context: Context) :
        androidx.recyclerview.widget.RecyclerView.Adapter<EmptyRoomResultAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTvBuilding.text = data[position].floor
        holder.mNoScrollGridView.adapter = EmptyGvAdapter(context, data[position].emptyRooms)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.emptyroom_recycle_item_room_result, parent, false))

    override fun getItemCount() = data.size

    fun updateData(newData: List<EmptyRoom>) {
        if (data.isNotEmpty()) {
            data.clear()
            data.addAll(newData)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        val mTvBuilding:TextView = itemView.findViewById(R.id.tv_building)
        val mNoScrollGridView:NoScrollGridView = itemView.findViewById(R.id.no_scroll_grid_view)
    }

}