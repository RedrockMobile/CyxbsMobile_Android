package com.mredrock.cyxbs.qa.pages.mine.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Ignore
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder.MyIgnoreViewHolder

/**
 * Author: RayleighZ
 * Time: 2021-03-12 12:44
 */
class MyIgnoreRvAdapter: BaseEndlessRvAdapter<Ignore>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ignore>() {
            override fun areItemsTheSame(oldItem: Ignore, newItem: Ignore) = oldItem.uid == newItem.uid
            override fun areContentsTheSame(oldItem: Ignore, newItem: Ignore) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Ignore> {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.qa_recycler_item_ignore, parent, false)
        return MyIgnoreViewHolder(item)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Ignore>, position: Int) {
        super.onBindViewHolder(holder, position)
        //TODO: 设置点击事件
    }
}