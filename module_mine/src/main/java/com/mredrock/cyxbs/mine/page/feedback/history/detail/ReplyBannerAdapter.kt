package com.mredrock.cyxbs.mine.page.feedback.history.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemReplyBannerBinding

/**
 *@author ZhiQiang Tu
 *@time 2021/8/25  16:20
 *@signature 我们不明前路，却已在路上
 */
class ReplyBannerAdapter : ListAdapter<String, ReplyBannerViewHolder>(diff) {

    companion object {
        private val diff = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyBannerViewHolder {
        return ReplyBannerViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.mine_recycle_item_reply_banner,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReplyBannerViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }

}

class ReplyBannerViewHolder(val binding: MineRecycleItemReplyBannerBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(s: String?, position: Int) {
        binding.url = s
    }

}