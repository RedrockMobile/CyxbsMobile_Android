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
// 这里将LayoutManager向外扩展，方便操作RecyclerView滚动平移等操作
class DataBindingAdapter(val layoutManager: RecyclerView.LayoutManager) : RecyclerView.Adapter<DataBindingAdapter.DataBindingViewHolder>(){

    /**
     * <position —— BaseDataBinder>哈希表
     */
    private val mAsyncListChange by lazy { AsyncListDiffer(this, DiffItemCallback<BaseDataBinder<*>>()) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {

        return DataBindingViewHolder(parent.inflateDataBinding(mAsyncListChange.currentList[viewType].layoutId())).apply {
            itemView.setOnClickListener {
                mCurrBinder?.onClick(it)
            }
        }
    }


    class DataBindingViewHolder(private val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        var mCurrBinder: BaseDataBinder<ViewDataBinding>? = null

        fun bindData(binder: BaseDataBinder<ViewDataBinding>){
            if (mCurrBinder == binder) close()
            binder.bindDataBinding(dataBinding)
            mCurrBinder = binder
        }

        private fun close(){
            mCurrBinder?.unBindDataBinding()
            mCurrBinder = null
        }
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder, position: Int) {
        val binder = mAsyncListChange.currentList[position]
        holder.itemView.tag = binder.layoutId()
        holder.bindData(binder as BaseDataBinder<ViewDataBinding>)
    }

    fun notifyAdapterChanged(binders: List<BaseDataBinder<*>>){
        mAsyncListChange.submitList(binders)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = mAsyncListChange.currentList.size

}