package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_history.view.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchHistoryRvAdapter(private val onHistoryClick: (Int) -> Unit, private val onCleanIconClick: ((Int) -> Unit)) : BaseRvAdapter<String>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> = ViewHolder(parent)

    override fun onBindViewHolder(holder: BaseViewHolder<String>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.tv_history_title.setOnClickListener {
            onHistoryClick(position)
        }
        holder.itemView.iv_history_delete.setOnClickListener {
            dataList.removeAt(position)
            notifyItemRemoved(position)
            onCleanIconClick(position)

        }
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<String>(parent, R.layout.qa_recycler_item_history) {
        override fun refresh(data: String?) {
            itemView.tv_history_title.text = data ?: ""
        }
    }
}