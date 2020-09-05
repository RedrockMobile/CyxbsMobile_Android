package com.mredrock.cyxbs.volunteer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime

class VolunteerRecyclerAdapter(
        private var recordBeanList: MutableList<VolunteerTime.RecordBean>?,
        var context: Context,
        private var yearList: MutableList<String>,
        private var allList: MutableList<MutableList<VolunteerTime.RecordBean>>?
) : androidx.recyclerview.widget.RecyclerView.Adapter<VolunteerRecyclerAdapter.ViewHolder>() {

    fun updaeList(yList: MutableList<String>,aList: MutableList<MutableList<VolunteerTime.RecordBean>>?) {
        yearList = yList
        allList = aList
        notifyDataSetChanged()
    }

    private lateinit var adapter: VolunteerRecyclerChildAdapter

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var yearText: TextView = itemView.findViewById<View>(R.id.volunteer_time_year) as TextView
        var monthRecycler: androidx.recyclerview.widget.RecyclerView = itemView.findViewById<View>(R.id.volunteer_time_child_recycler) as androidx.recyclerview.widget.RecyclerView
        var divider: TextView = itemView.findViewById<View>(R.id.volunteer_time_divider_line) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.volunteer_item_volunteer_child_year, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //        if (yearList.get(yearList.size() - 1).equals("全部"))
        holder.yearText.text = yearList[position]
        if (allList == null) {
            if (recordBeanList != null) {
                adapter = VolunteerRecyclerChildAdapter(recordBeanList!!)
            }
        } else {
            if (allList != null) {
                adapter = VolunteerRecyclerChildAdapter(allList!![position])

            }
        }

        holder.monthRecycler.adapter = adapter
        holder.monthRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        if (position == yearList.size - 1) {
            holder.divider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return yearList.size
    }
}