package com.mredrock.cyxbs.qa.pages.mine.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Ignore
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder.MyIgnoreViewHolder
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyIgnoreViewModel
import kotlinx.android.synthetic.main.qa_recycler_item_ignore.view.*

/**
 * Author: RayleighZ
 * Time: 2021-03-12 12:44
 * todo 不要建议把 ViewModel 传进 Adapter，应该使用接口
 */
class MyIgnoreRvAdapter(val viewModel: MyIgnoreViewModel) :
    BaseEndlessRvAdapter<Ignore>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ignore>() {
            override fun areItemsTheSame(oldItem: Ignore, newItem: Ignore) =
                oldItem.uid == newItem.uid

            override fun areContentsTheSame(oldItem: Ignore, newItem: Ignore) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Ignore> {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.qa_recycler_item_ignore, parent, false)
        return MyIgnoreViewHolder(item)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Ignore>, position: Int) {
        super.onBindViewHolder(holder, position)
        val ignore = getItem(position)

        holder.itemView.qa_btn_ignore_item_anti_ignore.setOnSingleClickListener {
            ignore?.apply {
                viewModel.antiIgnore(uid) {
                    holder.itemView.context.toast("解除屏蔽成功")
                    viewModel.invalidateList()
                }
            }
        }
    }
}