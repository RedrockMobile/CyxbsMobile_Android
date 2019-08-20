package com.mredrock.cyxbs.freshman.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.config.INTENT_COLLEGE
import com.mredrock.cyxbs.freshman.view.activity.DataDisclosureActivity

class CollegeDataAdapter : RecyclerView.Adapter<CollegeDataViewHolder>() {
    private var mCollegeData: List<String> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollegeDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.freshman_recycle_item_college, parent, false)
        return CollegeDataViewHolder(view)
    }

    override fun getItemCount() = mCollegeData.size

    override fun onBindViewHolder(holder: CollegeDataViewHolder, position: Int) {
        holder.mName.text = mCollegeData[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DataDisclosureActivity::class.java)
            intent.putExtra(INTENT_COLLEGE, mCollegeData[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    fun refreshData(collegeData: List<String>) {
        mCollegeData = collegeData
        notifyDataSetChanged()
    }
}

class CollegeDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mName: TextView = view.findViewById(R.id.tv_college_name)
}