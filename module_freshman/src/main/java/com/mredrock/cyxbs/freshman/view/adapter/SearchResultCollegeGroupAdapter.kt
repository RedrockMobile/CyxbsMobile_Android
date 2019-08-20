package com.mredrock.cyxbs.freshman.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.CollegeGroupText
import com.mredrock.cyxbs.freshman.config.INTENT_DATA
import com.mredrock.cyxbs.freshman.config.INTENT_NAME
import com.mredrock.cyxbs.freshman.view.activity.CopyQQNumberActivity

/**
 * Create by yuanbing
 * on 2019/8/5
 */
class SearchResultCollegeGroupAdapter : RecyclerView.Adapter<SearchResultCollegeGroupViewHolder>() {
    private var mCollegeGroupText: List<CollegeGroupText> = listOf()
    private var mIsNeedShowHint = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultCollegeGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.freshman_recycle_item_online_communication_group_search_result, parent, false)
        return SearchResultCollegeGroupViewHolder(view)
    }

    override fun getItemCount() = mCollegeGroupText.size

    override fun onBindViewHolder(holder: SearchResultCollegeGroupViewHolder, position: Int) {
        val collegeGroupText = mCollegeGroupText[position]
        holder.mName.text = collegeGroupText.name

        if (!mIsNeedShowHint) {
            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, CopyQQNumberActivity::class.java)
                intent.putExtra(INTENT_NAME, collegeGroupText.name)
                intent.putExtra(INTENT_DATA, collegeGroupText.data)
                it.context.startActivity(intent)
            }
        }
    }

    fun refreshData(collegeGroupText: List<CollegeGroupText>) {
        mCollegeGroupText = if (collegeGroupText.isEmpty()) {
            mIsNeedShowHint = true
            List(1) { CollegeGroupText("",
                    BaseApp.context.resources.getString(R.string.freshman_no_search_result)) }
        } else {
            mIsNeedShowHint = false
            collegeGroupText
        }
        notifyDataSetChanged()
    }

    fun mostMatch() = if (mCollegeGroupText.isNotEmpty()) mCollegeGroupText[0].name else ""
}

class SearchResultCollegeGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mName: TextView = view.findViewById(R.id.tv_recycle_item_online_communication_group_search_result)
}