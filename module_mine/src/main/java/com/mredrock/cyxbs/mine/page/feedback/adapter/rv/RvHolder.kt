package com.mredrock.cyxbs.mine.page.feedback.adapter.rv

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 *@author ZhiQiang Tu
 *@time 2021/8/11  11:20
 *@signature 我们不明前路，却已在路上
 */
class RvHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(binder: RvBinder<ViewDataBinding>, position: Int) {
        //讲View绑定入Data
        binder.binding = binding
        binder.onBind(position)
    }
}