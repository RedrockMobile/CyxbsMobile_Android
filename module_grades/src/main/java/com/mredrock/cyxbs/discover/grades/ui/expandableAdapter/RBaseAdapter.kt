package com.mredrock.cyxbs.discover.grades.ui.expandableAdapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by roger on 2020/3/22
 * 一个多type的可以展开的adapter
 */
@Deprecated("封装的不是很好")
abstract class RBaseAdapter<VH:RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected var dataMirror: ArrayList<Any> = arrayListOf()


    override fun getItemCount(): Int {
        return dataMirror.size
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val type = getItemViewType(position)
        onBind(holder, position, type)
    }

    abstract fun onBind(holder: VH, position: Int, type: Int)

    fun getDataMirror(): Any {
        return dataMirror.clone()
    }

    fun refreshUI(newDataMirror: ArrayList<Any>) {
        val oldArray = dataMirror
        dataMirror = newDataMirror

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return this@RBaseAdapter.areItemsTheSame(oldItemPosition, newItemPosition, oldArray[oldItemPosition], dataMirror[newItemPosition])
            }

            override fun getOldListSize(): Int {
                return oldArray.size
            }

            override fun getNewListSize(): Int {
                return dataMirror.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return this@RBaseAdapter.areContentsTheSame(oldItemPosition, newItemPosition, oldArray[oldItemPosition], dataMirror[newItemPosition])

            }
        })
        diffResult.dispatchUpdatesTo(this)
    }


    abstract fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int, oldItem: Any, newItem: Any): Boolean

    abstract fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int, oldItem: Any, newItem: Any): Boolean
}