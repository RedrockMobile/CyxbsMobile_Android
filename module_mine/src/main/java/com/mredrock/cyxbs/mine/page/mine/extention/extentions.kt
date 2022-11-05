package com.mredrock.cyxbs.mine.page.mine.extention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.page.mine.adapter.DataBindingAdapter

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
fun ViewGroup.inflateDataBinding(@LayoutRes resId: Int): ViewDataBinding {
    return DataBindingUtil.inflate(LayoutInflater.from(this.context),resId,this,false)
}

fun createBindingAdapter(recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager): DataBindingAdapter {
    recyclerView.layoutManager = layoutManager
    val mAdapter = DataBindingAdapter(layoutManager)
    recyclerView.adapter = mAdapter
    // 处理RecyclerView的触发回调
    recyclerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener{
        override fun onViewAttachedToWindow(v: View) {
        }

        override fun onViewDetachedFromWindow(v: View) {
            mAdapter.onDetachedFromRecyclerView(recyclerView)
        }
    })
    return mAdapter
}