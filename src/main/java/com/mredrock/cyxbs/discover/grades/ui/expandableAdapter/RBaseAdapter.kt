package com.mredrock.cyxbs.discover.grades.ui.expandableAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.grades.utils.baseRv.BaseHolder
import java.util.*

/**
 * Created by roger on 2020/3/22
 * 一个多type的可以展开的adapter
 */
abstract class RBaseAdapter(
        //type到resId的映射:type为子类自己定义的常量
        private val type2ResId: HashMap<Int, Int>
) : RecyclerView.Adapter<BaseHolder>() {

    protected var dataMirror: ArrayList<Any> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val res = type2ResId[viewType] ?: throw Exception("Invalid viewType")
        return BaseHolder.getHolder(parent.context, parent, res)
    }

    override fun getItemCount(): Int {
        return dataMirror.size
    }


    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        val type = getItemViewType(position)
        onBind(holder, position, type)
    }

    abstract fun onBind(holder: BaseHolder, position: Int, type: Int)

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