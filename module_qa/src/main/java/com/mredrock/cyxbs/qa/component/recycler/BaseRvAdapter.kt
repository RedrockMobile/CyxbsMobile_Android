package com.mredrock.cyxbs.qa.component.recycler

import com.mredrock.cyxbs.common.utils.extensions.*


/**
 * Created By jay68 on 2018/8/26.
 */
abstract class BaseRvAdapter<T> : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder<T>>() {
    protected val dataList = ArrayList<T>()

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        if (position >= dataList.size) {
            return
        }
        holder.refresh(dataList[position])
        holder.itemView.setOnSingleClickListener { onItemClickListener(holder, position, dataList[position]) }
    }

    protected open fun onItemClickListener(holder: BaseViewHolder<T>, position: Int, data: T) = Unit

    open fun refreshData(dataCollection: Collection<T>) {
        notifyItemRangeRemoved(0, dataList.size)
        dataList.clear()
        dataList.addAll(dataCollection)
        notifyItemRangeInserted(0, dataList.size)
    }

    open fun addData(dataCollection: Collection<T>) {
        dataList.addAll(dataCollection)
        notifyItemRangeInserted(itemCount - dataCollection.size - 1, dataCollection.size)
    }
}