package com.mredrock.cyxbs.volunteer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffair
import kotlinx.android.synthetic.main.volunteer_item_volunteer_affair.view.*

/**
 * Created by yyfbe, Date on 2020/9/4.
 */
class VolunteerAffairAdapter(private var dataList: List<VolunteerAffair>, private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<VolunteerAffairAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.tv_test
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerAffairAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.volunteer_item_volunteer_affair, parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: VolunteerAffairAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClick(dataList[position].id)
        }
        holder.textView.text = dataList[position].name
    }
    fun refreshData(newList: List<VolunteerAffair>){
        this.dataList = newList
        this.notifyDataSetChanged()
    }

}