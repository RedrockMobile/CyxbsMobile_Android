package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_label.view.*

/**
 *@Date 2020-12-14
 *@Time 21:21
 *@author SpreadWater
 *@description
 */
class SearchKnowledgeAdapter(private val onItemClick: (Int) -> Unit) : BaseRvAdapter<Knowledge>() {
    class ViewHolder(parent: ViewGroup) : BaseViewHolder<Knowledge>(parent, R.layout.qa_recycler_item_dynamic_label) {
        override fun refresh(data: Knowledge?) {
           itemView.tv_dynamic_label.text=data?.title ?: ""
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Knowledge>, position: Int, data: Knowledge) {
        super.onItemClickListener(holder, position, data)
        onItemClick(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Knowledge> = ViewHolder(parent)
}