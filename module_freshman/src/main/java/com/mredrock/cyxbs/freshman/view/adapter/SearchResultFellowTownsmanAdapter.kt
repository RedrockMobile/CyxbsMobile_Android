package com.mredrock.cyxbs.freshman.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.FellowTownsmanGroupText
import com.mredrock.cyxbs.freshman.config.INTENT_DATA
import com.mredrock.cyxbs.freshman.config.INTENT_NAME
import com.mredrock.cyxbs.freshman.view.activity.CopyQQNumberActivity

/**
 * Create by yuanbing
 * on 2019/8/5
 */
class SearchResultFellowTownsmanAdapter : RecyclerView.Adapter<SearchResultFellowTownsmanViewHolder>() {
    private var mFellowTownsmanGroup: List<FellowTownsmanGroupText> = listOf()
    private var mIsNeedShowHint = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultFellowTownsmanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.freshman_recycle_item_online_communication_group_search_result, parent, false)
        return SearchResultFellowTownsmanViewHolder(view)
    }

    override fun getItemCount() = mFellowTownsmanGroup.size

    override fun onBindViewHolder(holder: SearchResultFellowTownsmanViewHolder, position: Int) {
        val fellowTownsmanGroup = mFellowTownsmanGroup[position]
        holder.mName.text = fellowTownsmanGroup.name

        if (!mIsNeedShowHint) {
            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, CopyQQNumberActivity::class.java)
                intent.putExtra(INTENT_NAME, fellowTownsmanGroup.name)
                intent.putExtra(INTENT_DATA, fellowTownsmanGroup.data)
                it.context.startActivity(intent)
            }
        }
    }

    fun refreshData(fellowTownsmanGroup: List<FellowTownsmanGroupText>) {
        mFellowTownsmanGroup = if (fellowTownsmanGroup.isEmpty()) {
            mIsNeedShowHint = true
            List(1) { FellowTownsmanGroupText("",
                    BaseApp.context.resources.getString(R.string.freshman_no_search_result)) }
        } else {
            mIsNeedShowHint = false
            fellowTownsmanGroup
        }
        notifyDataSetChanged()
    }

    fun mostMatch() = if (mFellowTownsmanGroup.isNotEmpty()) mFellowTownsmanGroup[0].name else ""
}

class SearchResultFellowTownsmanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mName: TextView = view.findViewById(R.id.tv_recycle_item_online_communication_group_search_result)
}