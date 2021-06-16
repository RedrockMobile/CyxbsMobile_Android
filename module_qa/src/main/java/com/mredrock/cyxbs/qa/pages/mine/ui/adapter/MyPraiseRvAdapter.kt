package com.mredrock.cyxbs.qa.pages.mine.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Praise
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder.MyPraiseViewHolder
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyPraiseViewModel

/**
 * Author: RayleighZ
 * Time: 2021-03-11 1:04
 */
class MyPraiseRvAdapter(val activity: Activity): BaseEndlessRvAdapter<Praise>(DIFF_CALLBACK) {

    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Praise>() {
            override fun areItemsTheSame(oldItem: Praise, newItem: Praise) = oldItem.id == newItem.id && oldItem.type == newItem.type
            override fun areContentsTheSame(oldItem: Praise, newItem: Praise) = oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Praise>, position: Int) {
        super.onBindViewHolder(holder, position)
        getItem(position)?.apply {
            holder.itemView.setOnSingleClickListener {
                DynamicDetailActivity.activityStart(activity ,this.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Praise> {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.qa_recycler_item_my_praise, parent, false)
        return MyPraiseViewHolder(itemView)
    }
}