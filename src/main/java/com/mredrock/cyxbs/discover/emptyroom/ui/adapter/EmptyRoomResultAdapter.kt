package com.mredrock.cyxbs.emptyroom.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.emptyroom.R
import com.mredrock.cyxbs.emptyroom.bean.EmptyRoom
import kotlinx.android.synthetic.main.emptyroom_recycle_item_room_result.view.*

/**
 * Created by Cynthia on 2018/9/19
 */
class EmptyRoomResultAdapter(var data: MutableList<EmptyRoom>, private val context: Context) :
        RecyclerView.Adapter<EmptyRoomResultAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tv_building.text = data[position].floor
        holder.itemView.no_scroll_grid_view.adapter = EmptyGvAdapter(context, data[position].emptyRooms)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.emptyroom_recycle_item_room_result, parent, false))

    override fun getItemCount() = data.size

    fun updateData(newData : List<EmptyRoom>){
        if (data.isNotEmpty()){
            data.clear()
            data.addAll(newData)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}