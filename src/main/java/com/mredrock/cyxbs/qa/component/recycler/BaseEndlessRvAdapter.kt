package com.mredrock.cyxbs.qa.component.recycler

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil

/**
 * Created By jay68 on 2018/9/13.
 */
abstract class BaseEndlessRvAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) : PagedListAdapter<T, BaseViewHolder<T>>(diffCallback) {
    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val data = getItem(position)    //if a nul, placeholder is at the specified position
        holder.refresh(data)
        holder.itemView.setOnClickListener {
            data ?: return@setOnClickListener
            onItemClickListener(holder, position, data)
        }
    }

    protected open fun onItemClickListener(holder: BaseViewHolder<T>, position: Int, data: T) = Unit
}