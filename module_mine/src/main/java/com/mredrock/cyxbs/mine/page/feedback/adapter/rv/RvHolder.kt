package com.mredrock.cyxbs.mine.page.feedback.adapter.rv

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 *@author ZhiQiang Tu
 *@time 2021/8/11  11:20
 *@signature 我们不明前路，却已在路上
 */
class RvHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(binder: RvBinder<ViewDataBinding>) {
        //讲View绑定入Data
        Log.e("TAG", "bind: ")
        binder.binding = binding
        binder.onBind(this)
    }
}