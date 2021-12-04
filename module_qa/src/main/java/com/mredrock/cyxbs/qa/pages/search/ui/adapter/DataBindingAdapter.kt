package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.util.Log
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
// 这里将LayoutManager向外扩展，方便操作RecyclerView滚动平移等操作
class DataBindingAdapter(val layoutManager: RecyclerView.LayoutManager) : RecyclerView.Adapter<DataBindingAdapter.DataBindingViewHolder>(){

    /**
     * <position —— BaseDataBinder>哈希表
     */
    private val mAsyncListChange by lazy { AsyncListDiffer(this, DiffItemCallback<BaseDataBinder<*>>()) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {

        return DataBindingViewHolder(parent.inflateDataBinding(viewType)).apply {
            itemView.setOnClickListener {
                mCurrBinder?.onClick(it)
            }
        }
    }

    class DataBindingViewHolder(private val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        var mCurrBinder: BaseDataBinder<ViewDataBinding>? = null

        fun bindData(binder: BaseDataBinder<ViewDataBinding>,position: Int){
            if (mCurrBinder == binder) close()
            binder.bindDataBinding(dataBinding,position)
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
        holder.bindData(binder as BaseDataBinder<ViewDataBinding>,position)
        Log.d("TAG","(DataBindingAdapter.kt:55)->position = $position ; nickname = ${binder.getName()}")
    }

    fun notifyAdapterChanged(binders: List<BaseDataBinder<*>>){
        mAsyncListChange.submitList(binders)
    }

    override fun getItemViewType(position: Int): Int {
        return mAsyncListChange.currentList[position].layoutId()
    }

    override fun getItemCount(): Int = mAsyncListChange.currentList.size

}