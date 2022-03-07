package com.mredrock.cyxbs.mine.page.feedback.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineFeedbackRecycleItemDetailBinding
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  10:42
 *@signature 我们不明前路，却已在路上
 */
class RvListAdapter : ListAdapter<History, HistoryItemHolder>(diff) {

    private var itemClickListener: ItemClickListener? = null

    companion object {
        private val diff = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean =
                oldItem == newItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemHolder {
        val binding =
            DataBindingUtil.inflate<MineFeedbackRecycleItemDetailBinding>(LayoutInflater.from(parent.context),
                R.layout.mine_feedback_recycle_item_detail, parent, false)
        return HistoryItemHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryItemHolder, position: Int) {
        itemClickListener?.let { holder.bind(currentList[position], it) }
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

    interface ItemClickListener {
        fun clicked(view: View, id: History)
    }

}