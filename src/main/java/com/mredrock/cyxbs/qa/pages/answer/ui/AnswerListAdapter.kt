package com.mredrock.cyxbs.qa.pages.answer.ui

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.utils.*
import kotlinx.android.synthetic.main.qa_recycler_item_answer.view.*

/**
 * Created By jay68 on 2018/9/30.
 */
class AnswerListAdapter(context: Context) : BaseEndlessRvAdapter<Answer>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Answer>() {
            override fun areItemsTheSame(oldItem: Answer?, newItem: Answer?) = oldItem?.id == newItem?.id

            override fun areContentsTheSame(oldItem: Answer?, newItem: Answer?) = oldItem == newItem
        }
    }

    var onItemClickListener: ((Int, Answer) -> Unit)? = null

    private val sortByTime = context.getString(R.string.qa_answer_list_sort_by_time)
    private val sortByDefault = context.getString(R.string.qa_answer_list_sort_by_default)

    private var isEmotion = false
    private var isSelf = false
    private var hasAdoptedAnswer = false
    private var sortBy = sortByDefault

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            hasAdoptedAnswer = currentList?.find { it.isAdopted } != null
            resortList(sortBy)
        }
    }

    fun resortList(sortBy: String) {
        //todo 排序
        /*this.sortBy = sortBy
        if (sortBy == sortByDefault) {
            currentList?.sortWith(Comparator { o1, o2 ->
                compareValuesBy(o2, o1, Answer::isAdopted, Answer::hotValue, Answer::createdAt)
            })
        } else {
            currentList?.sortByDescending(Answer::createdAt)
        }
        notifyItemRangeChanged(0, this@AnswerListAdapter.itemCount)*/
    }

    fun setQuestionInfo(question: Question) {
        isEmotion = question.isEmotion
        isSelf = question.isSelf
        hasAdoptedAnswer = question.hasAdoptedAnswer
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AnswerViewHolder(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Answer>, position: Int, data: Answer) {
        super.onItemClickListener(holder, position, data)
        onItemClickListener?.invoke(position, data)
    }

    inner class AnswerViewHolder(parent: ViewGroup) : BaseViewHolder<Answer>(parent, R.layout.qa_recycler_item_answer) {
        override fun refresh(data: Answer?) {
            data ?: return
            itemView.apply {
                iv_answer_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_answer_nickname.setNicknameTv(data.nickname, isEmotion, data.isMale)
                setAdoptedTv(tv_adopted, tv_adopt, data.isAdopted, hasAdoptedAnswer || !isSelf)
                tv_answer_content.text = data.content
                setDate(tv_date, data.createdAt)
                tv_comment_count.text = data.commentNum
                tv_praise.setPraise(data.praiseNum, data.isPraised)
            }
        }

        private fun setDate(dateTv: TextView, date: String) {
            val desc = timeDescription(System.currentTimeMillis(), date.toDate().time)
            if (desc.last() in '0'..'9') {
                dateTv.text = desc
            } else {
                dateTv.text = context.getString(R.string.qa_answer_list_answer_date, desc)
            }
        }
    }
}