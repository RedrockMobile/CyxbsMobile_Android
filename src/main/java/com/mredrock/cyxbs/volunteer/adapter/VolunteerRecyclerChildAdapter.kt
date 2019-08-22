package com.mredrock.cyxbs.volunteer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime

class VolunteerRecyclerChildAdapter(private val recordBeanList: MutableList<VolunteerTime.RecordBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<VolunteerRecyclerChildAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var dateText: TextView = itemView.findViewById<View>(R.id.volunteer_time_day) as TextView
        var hourText: TextView = itemView.findViewById<View>(R.id.volunteer_time_hour) as TextView
        var activityText: TextView = itemView.findViewById<View>(R.id.volunteer_time_content) as TextView
        var addressText: TextView = itemView.findViewById<View>(R.id.volunteer_time_address) as TextView
        var cardView: androidx.cardview.widget.CardView = itemView as androidx.cardview.widget.CardView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_volunteer_child_month, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = recordBeanList[position]
        holder.dateText.text = record.start_time!!.substring(5)
        holder.hourText.text = record.hours.plus(" 小时")
        holder.activityText.text = record.title
        holder.addressText.text = record.server_group
    }

    override fun getItemCount(): Int {
        return recordBeanList.size
    }
}