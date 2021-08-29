package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.config.RequestResultCode.ClickKnowledge
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_label.view.*

/**
 *@Date 2020-12-14
 *@Time 21:21
 *@author SpreadWater
 *@description
 */
class SearchKnowledgeAdapter( val recyclerView: RecyclerView) : BaseRvAdapter<Knowledge>() {
    var searchResultHeaderAdapter: SearchResultHeaderAdapter?=null

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<Knowledge>(parent, R.layout.qa_recycler_item_dynamic_label) {
        override fun refresh(data: Knowledge?) {
           itemView.tv_dynamic_label.text=data?.title ?: ""
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Knowledge>, position: Int, data: Knowledge) {
        super.onItemClickListener(holder, position, data)
        holder.itemView.setOnClickListener {
            ClickKnowledge = true
            searchResultHeaderAdapter?.knowledge=data
            recyclerView.adapter=searchResultHeaderAdapter
            recyclerView.layoutManager=LinearLayoutManager(BaseApp.context)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Knowledge> = ViewHolder(parent)
}