package com.mredrock.cyxbs.qa.pages.question.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_freshman_hot_question.view.*

/**
 * Created by yyfbe, Date on 2020/8/11.
 */
class FreshManHeaderVpAdapter(private val onItemClickEvent: (Question) -> Unit) : BaseEndlessRvAdapter<Question>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Question, newItem: Question) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionViewHolder(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Question>, position: Int, data: Question) {
        super.onItemClickListener(holder, position, data)
        if (holder !is QuestionViewHolder) return
        onItemClickEvent.invoke(data)
    }

    class QuestionViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycler_item_freshman_hot_question) {
        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                qa_freshman_hot_item_title.text = data.title
                qa_freshman_hot_item_subtitle.text = data.description

            }
        }

    }
}