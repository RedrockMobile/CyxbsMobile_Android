package com.mredrock.cyxbs.volunteer.Adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime

class VolunteerRecyclerAdapter(var recordBeanList: MutableList<VolunteerTime.RecordBean>?
                               , var context: Context
                               , var yearList: MutableList<String>
                               , var allList: MutableList<MutableList<VolunteerTime.RecordBean>>?)
    : RecyclerView.Adapter<VolunteerRecyclerAdapter.ViewHolder>() {

    private lateinit var adapter: VolunteerRecyclerChildAdapter

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var yearText: TextView = itemView.findViewById<View>(R.id.volunteer_time_year) as TextView
        var monthRecycler: RecyclerView = itemView.findViewById<View>(R.id.volunteer_time_child_recycler) as RecyclerView
        var divider: TextView = itemView.findViewById<View>(R.id.volunteer_time_divider_line) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_volunteer_child_year, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: VolunteerRecyclerAdapter.ViewHolder, position: Int) {
        //        if (yearList.get(yearList.size() - 1).equals("全部"))
        holder.yearText.text = yearList[position]
        if (allList == null) {
            if (recordBeanList != null) {
                adapter = VolunteerRecyclerChildAdapter(recordBeanList!!)
            }
        } else {
            if (allList != null){
                adapter =VolunteerRecyclerChildAdapter(allList!![position])

            }
        }

        holder.monthRecycler.adapter = adapter
        holder.monthRecycler.layoutManager = LinearLayoutManager(context)

        if (position == yearList.size - 1) {
            holder.divider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return yearList.size
    }
}