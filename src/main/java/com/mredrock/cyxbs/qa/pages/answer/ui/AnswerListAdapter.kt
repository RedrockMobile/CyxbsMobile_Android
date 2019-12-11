package com.mredrock.cyxbs.qa.pages.answer.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.comment.AdoptAnswerEvent
import com.mredrock.cyxbs.qa.utils.*
import kotlinx.android.synthetic.main.qa_recycler_item_answer.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/9/30.
 */
class AnswerListAdapter(context: Context) : BaseRvAdapter<Answer>() {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Answer>() {
            override fun areItemsTheSame(oldItem: Answer, newItem: Answer) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Answer, newItem: Answer) = oldItem == newItem
        }
    }

    var onItemClickListener: ((Int, Answer) -> Unit)? = null

    private val sortByTime = context.getString(R.string.qa_answer_list_sort_by_time)
    private val sortByDefault = context.getString(R.string.qa_answer_list_sort_by_default)

    private var isEmotion = false
    private var isSelf = false
    private var hasAdoptedAnswer = false
    private var sortBy = sortByDefault

    fun resortList(sortBy: String) {
        this.sortBy = sortBy
        when (sortBy) {
            sortByDefault->{
                dataList.sortWith(Comparator { o1, o2 ->
                    compareValuesBy(o2, o1, Answer::isAdopted, Answer::hotValue, Answer::createdAt)
                })
            }
            sortByTime->{
                dataList.sortByDescending(Answer::createdAt)
            }
        }
        notifyItemRangeChanged(0, this@AnswerListAdapter.itemCount)
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
                tv_adopt.setOnClickListener {
                    EventBus.getDefault().post(AdoptAnswerEvent(data.id))
                }
                tv_answer_content.text = data.content
                setDate(tv_answer_publish_at, data.createdAt)
                tv_answer_reply_count.text = data.commentNum
                tv_answer_praise_count.setPraise(data.praiseNum, data.isPraised)
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