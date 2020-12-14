package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_item_search_no_result.view.*


/**
 *@Date 2020-12-14
 *@Time 16:00
 *@author SpreadWater
 *@description
 */
class SearchNoResultAdapter(private val hint: String,private val onItemClick: () -> Unit) : BaseRvAdapter<Boolean>() {
    private var showed = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EmptyViewHolder(hint, parent)
    override fun onBindViewHolder(holder: BaseViewHolder<Boolean>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.btn_no_result_ask_question.setOnSingleClickListener {
            //回调到外面进行页面跳转
            onItemClick()
        }
    }

    fun showHolder(size: Int = 1) {
        if (!showed) {
            showed = true
            refreshData(BooleanArray(size) { false }.toList())
        }
    }

    fun hideHolder() {
        refreshData(listOf(true))
    }

    class EmptyViewHolder(private val hint: String, parent: ViewGroup) : BaseViewHolder<Boolean>(parent, R.layout.qa_item_search_no_result) {
        override fun refresh(data: Boolean?) {
            if (data == true) {
                itemView.card_holder.gone()
                itemView.tv_hint.text = hint
                itemView.iv_hint.visible()
                itemView.tv_hint.visible()
                itemView.btn_no_result_ask_question.visible()
            } else {
                itemView.card_holder.visible()
                itemView.tv_hint.gone()
                itemView.iv_hint.gone()
                itemView.btn_no_result_ask_question.gone()
            }
        }
    }
}