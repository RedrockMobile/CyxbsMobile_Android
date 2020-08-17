package com.mredrock.cyxbs.qa.pages.question.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.HotQA
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_freshman_hot_question.view.*

/**
 * Created by yyfbe, Date on 2020/8/11.
 */
class FreshManHeaderInnerVpAdapter(private val onItemClickEvent: (HotQA) -> Unit) : BaseEndlessRvAdapter<HotQA>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HotQA>() {
            override fun areItemsTheSame(oldItem: HotQA, newItem: HotQA) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: HotQA, newItem: HotQA) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FreshManQuestionViewHolder(parent)


    override fun onItemClickListener(holder: BaseViewHolder<HotQA>, position: Int, data: HotQA) {
        super.onItemClickListener(holder, position, data)
        if (holder !is FreshManQuestionViewHolder) return
        holder.itemView.context.doIfLogin {
            onItemClickEvent.invoke(data)
        }

    }

    class FreshManQuestionViewHolder(parent: ViewGroup) : BaseViewHolder<HotQA>(parent, R.layout.qa_recycler_item_freshman_hot_question) {
        override fun refresh(data: HotQA?) {
            itemView.qa_freshman_hot_item_title.text = data?.title
            itemView.qa_freshman_hot_item_subtitle.text = data?.description
        }

    }
}