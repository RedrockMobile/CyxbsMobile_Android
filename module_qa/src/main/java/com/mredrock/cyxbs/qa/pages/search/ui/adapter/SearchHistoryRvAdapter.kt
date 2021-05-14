package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_history.view.*
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchHistoryRvAdapter(private val onHistoryClick: (Int) -> Unit, private val onCleanIconClick: ((Int) -> Unit)) : BaseRvAdapter<String>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> = ViewHolder(parent)

    override fun onBindViewHolder(holder: BaseViewHolder<String>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.tv_history_title.setOnSingleClickListener {
            onHistoryClick(holder.adapterPosition)
        }
        holder.itemView.iv_history_delete.setOnSingleClickListener {
            val pos = holder.adapterPosition
            dataList.removeAt(pos)
            notifyDataSetChanged()
            onCleanIconClick(pos)
        }
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<String>(parent, R.layout.qa_recycler_item_history) {
        override fun refresh(data: String?) {
            itemView.tv_history_title.text = data ?: ""
        }
    }
}