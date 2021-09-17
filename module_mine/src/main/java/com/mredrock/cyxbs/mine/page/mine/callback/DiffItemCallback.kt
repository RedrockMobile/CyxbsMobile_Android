package com.mredrock.cyxbs.mine.page.mine.callback

import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.mine.page.mine.binder.BaseDataBinder

class DiffItemCallback<T: BaseDataBinder<*>> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.layoutId() == newItem.layoutId() && oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}