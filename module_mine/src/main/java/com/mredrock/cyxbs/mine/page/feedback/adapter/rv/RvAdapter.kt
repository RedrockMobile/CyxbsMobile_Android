package com.mredrock.cyxbs.mine.page.feedback.adapter.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 *@author ZhiQiang Tu
 *@time 2021/8/11  11:19
 *@signature 我们不明前路，却已在路上
 */
class RvAdapter() : ListAdapter<RvBinder<*>, RvHolder>(diff) {

    companion object {
        private val diff = object : DiffUtil.ItemCallback<RvBinder<*>>() {
            override fun areItemsTheSame(oldItem: RvBinder<*>, newItem: RvBinder<*>): Boolean =
                oldItem.itemId == newItem.itemId

            override fun areContentsTheSame(oldItem: RvBinder<*>, newItem: RvBinder<*>): Boolean =
                /*oldItem.hashCode() == newItem.hashCode() &&*/ oldItem.areContentsTheSame(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvHolder {
        return RvHolder(parent.inflateBinding(viewType))
    }

    override fun onBindViewHolder(holder: RvHolder, position: Int) {
        val binder = currentList[position] as RvBinder<ViewDataBinding>
        holder.bind(binder)
    }

    override fun submitList(list: MutableList<RvBinder<*>>?) {
        super.submitList(if (list != null) ArrayList(list) else null)
    }

    override fun getItemViewType(position: Int): Int = currentList[position].layoutId()

    private fun <T : ViewDataBinding> ViewGroup.inflateBinding(layoutId: Int): T =
        DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, false)

}