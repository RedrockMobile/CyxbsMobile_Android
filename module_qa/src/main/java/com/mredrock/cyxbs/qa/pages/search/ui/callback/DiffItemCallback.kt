package com.mredrock.cyxbs.qa.pages.search.ui.callback

import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.qa.pages.search.ui.binder.BaseDataBinder

class DiffItemCallback<T: BaseDataBinder<*>> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}