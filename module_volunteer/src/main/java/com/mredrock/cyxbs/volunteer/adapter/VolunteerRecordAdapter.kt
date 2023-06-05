package com.mredrock.cyxbs.volunteer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime

class VolunteerRecordAdapter(private var dataList: List<VolunteerTime.RecordBean>) : RecyclerView.Adapter<VolunteerRecordAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.volunteer_item_record, parent, false))

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.tv_volunteer_record_time).text = dataList[position].start_time
            findViewById<TextView>(R.id.tv_volunteer_item_title).text = dataList[position].content
            findViewById<TextView>(R.id.tv_volunteer_item_pos).text = dataList[position].addWay
            findViewById<TextView>(R.id.tv_volunteer_item_group).text = dataList[position].server_group
            findViewById<TextView>(R.id.tv_volunteer_item_value).text = dataList[position].hours
        }
    }

    fun refresh(dataList: List<VolunteerTime.RecordBean>) {
        this.dataList = dataList
        this.notifyDataSetChanged()
    }

}