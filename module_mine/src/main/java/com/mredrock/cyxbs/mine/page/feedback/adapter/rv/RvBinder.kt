package com.mredrock.cyxbs.mine.page.feedback.adapter.rv

import androidx.databinding.ViewDataBinding

/**
 *@author ZhiQiang Tu
 *@time 2021/8/23  9:49
 *@signature 我们不明前路，却已在路上
 */
abstract class RvBinder<Binding : ViewDataBinding> {

    var itemId: String = ""

    open var binding: Binding? = null
    open fun onBind(holder: RvHolder) {}
    abstract fun layoutId(): Int
    abstract fun areContentsTheSame(oldItem: RvBinder<*>): Boolean
}