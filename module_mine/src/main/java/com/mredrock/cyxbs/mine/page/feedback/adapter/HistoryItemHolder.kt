package com.mredrock.cyxbs.mine.page.feedback.adapter

import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.databinding.MineFeedbackRecycleItemDetailBinding
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  10:43
 *@signature 我们不明前路，却已在路上
 */
class HistoryItemHolder(private val binding: MineFeedbackRecycleItemDetailBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(history: History, listener: RvListAdapter.ItemClickListener) {
        binding.apply {
            data = history
            itemClickListener = listener
        }
    }
}