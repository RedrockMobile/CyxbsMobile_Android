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
class SearchNoResultAdapter(private val hint: String, private val onItemClick: () -> Unit) : BaseRvAdapter<Int>() {
    private var showed = false

    companion object {
        const val INITIAL = 0//初始化的加载布局
        const val RESULT_REFRESH = 1//有动态的刷新布局
        const val NO_RESULT_REFRESH = 2//没有动态的刷新布局
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EmptyViewHolder(hint, parent)
    override fun onBindViewHolder(holder: BaseViewHolder<Int>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.btn_no_result_ask_question.setOnSingleClickListener {
            //回调到外面进行页面跳转
            onItemClick()
        }
    }

    fun showInitialHolder(size: Int = 1) {
        if (!showed) {
            showed = true
            refreshData(IntArray(size) { INITIAL}.toList())
        }
    }

    fun showResultRefreshHolder() {
        refreshData(listOf(RESULT_REFRESH))
    }
    fun showNOResultRefreshHolder(){
        refreshData(listOf(NO_RESULT_REFRESH))
    }
    class EmptyViewHolder(private val hint: String, parent: ViewGroup) : BaseViewHolder<Int>(parent, R.layout.qa_item_search_no_result) {
        override fun refresh(data: Int?) {
            when (data) {
                INITIAL -> {
                    itemView.card_holder.visible()
                    itemView.tv_hint.gone ()
                    itemView.iv_hint.gone ()
                    itemView.btn_no_result_ask_question.gone ()
                }
                RESULT_REFRESH -> {
                    itemView.card_holder.gone()
                    itemView.tv_hint.text = hint
                    itemView.iv_hint.visible()
                    itemView.tv_hint.visible()
                    itemView.btn_no_result_ask_question.gone ()
                }
                NO_RESULT_REFRESH ->{
                    itemView.card_holder.gone()
                    itemView.tv_hint.text = hint
                    itemView.iv_hint.visible()
                    itemView.tv_hint.visible()
                    itemView.btn_no_result_ask_question.visible()
                }
            }
        }
    }
}