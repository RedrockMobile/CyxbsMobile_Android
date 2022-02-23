package com.mredrock.cyxbs.mine.page.mine.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.page.mine.callback.DiffItemCallback
import com.mredrock.cyxbs.mine.page.mine.extention.inflateDataBinding
import com.mredrock.cyxbs.mine.page.mine.binder.BaseDataBinder

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class DataBindingAdapter(val layoutManager: RecyclerView.LayoutManager) : RecyclerView.Adapter<DataBindingAdapter.DataBindingViewHolder>(){

    private val mAsyncListChange by lazy { AsyncListDiffer(this, DiffItemCallback<BaseDataBinder<*>>()) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {

        return DataBindingViewHolder(parent.inflateDataBinding(viewType))
    }

    class DataBindingViewHolder(private val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bindData(binder: BaseDataBinder<ViewDataBinding>){
            binder.bindDataBinding(dataBinding)
        }
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder, position: Int) {
        holder.bindData(mAsyncListChange.currentList[position] as BaseDataBinder<ViewDataBinding>)
    }

    fun notifyAdapterChanged(binders: List<BaseDataBinder<*>>){
        mAsyncListChange.submitList(binders)
    }

    override fun getItemViewType(position: Int): Int {
        return mAsyncListChange.currentList[position].layoutId()
    }

    override fun getItemCount(): Int = mAsyncListChange.currentList.size

}