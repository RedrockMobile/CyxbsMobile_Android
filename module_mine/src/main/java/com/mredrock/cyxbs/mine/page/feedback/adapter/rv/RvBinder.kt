package com.mredrock.cyxbs.mine.page.feedback.adapter.rv

import androidx.databinding.ViewDataBinding

/**
 *@author ZhiQiang Tu
 *@time 2021/8/23  9:49
 *@signature 我们不明前路，却已在路上
 */
abstract class RvBinder<Binding : ViewDataBinding> {
    open var binding: Binding? = null
    open fun onBind(position: Int) {}
    abstract fun layoutId(): Int
    abstract fun areContentsTheSame(oldItem: RvBinder<*>): Boolean
}