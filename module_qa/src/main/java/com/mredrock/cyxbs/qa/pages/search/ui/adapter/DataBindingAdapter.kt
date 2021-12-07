package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.qa.pages.search.ui.binder.BaseDataBinder
import com.mredrock.cyxbs.qa.pages.search.ui.callback.DiffItemCallback
import com.mredrock.cyxbs.qa.pages.search.ui.extention.inflateDataBinding

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class DataBindingAdapter : RecyclerView.Adapter<DataBindingAdapter.DataBindingViewHolder>(){

    private val mAsyncListChange by lazy { AsyncListDiffer(this, DiffItemCallback<BaseDataBinder<*>>()) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {
        return DataBindingViewHolder(parent.inflateDataBinding(viewType))
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder, position: Int) {
        holder.bindData(mAsyncListChange.currentList[position] as BaseDataBinder<ViewDataBinding>,position)
    }

    class DataBindingViewHolder(private val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bindData(binder: BaseDataBinder<ViewDataBinding>,position: Int){
            binder.bindDataBinding(dataBinding)
        }
    }

    fun notifyAdapterChanged(binders: List<BaseDataBinder<*>>){
        mAsyncListChange.submitList(binders)
    }

    override fun getItemViewType(position: Int): Int {
        return mAsyncListChange.currentList[position].layoutId()
    }

    override fun getItemCount(): Int = mAsyncListChange.currentList.size

}