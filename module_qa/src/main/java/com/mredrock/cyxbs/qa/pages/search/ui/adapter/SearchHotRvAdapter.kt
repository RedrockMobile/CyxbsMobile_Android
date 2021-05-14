package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_label.view.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchHotRvAdapter(private val onItemClick: (String) -> Unit) : BaseRvAdapter<String>() {
    class ViewHolder(parent: ViewGroup) : BaseViewHolder<String>(parent, R.layout.qa_recycler_item_dynamic_label) {
        override fun refresh(data: String?) {
            itemView.tv_dynamic_label.text = data ?: ""
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<String>, position: Int, data: String) {
        super.onItemClickListener(holder, position, data)
        onItemClick(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> = ViewHolder(parent)
}