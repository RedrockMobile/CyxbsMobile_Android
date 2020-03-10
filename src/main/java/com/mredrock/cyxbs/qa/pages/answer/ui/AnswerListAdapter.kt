package com.mredrock.cyxbs.qa.pages.answer.ui

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.utils.setPraise
import com.mredrock.cyxbs.qa.utils.timeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_answer.view.*

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

    var onPraiseClickListener: ((Int, Answer) -> Unit)? = null
    var onReportClickListener: ((String) -> Unit)? = null
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
            sortByDefault -> {
                dataList.sortWith(Comparator { o1, o2 ->
                    compareValuesBy(o2, o1, Answer::isAdopted, Answer::hotValue, Answer::createdAt)
                })
            }
            sortByTime -> {
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

    override fun onBindViewHolder(holder: BaseViewHolder<Answer>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            tv_answer_praise_count.setOnClickListener {
                onPraiseClickListener?.invoke(position, dataList[position])
            }
            btn_answer_more.setOnClickListener {
                onReportClickListener?.invoke(dataList[position].id)
            }
        }
    }

    inner class AnswerViewHolder(parent: ViewGroup) : BaseViewHolder<Answer>(parent, R.layout.qa_recycler_item_answer) {
        override fun refresh(data: Answer?) {
            data ?: return
            itemView.apply {
                //判断是否显示
                if (data.userId == ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()) {
                    btn_answer_more.gone()
                    tv_answer_praise_count.gone()
                }
                if (data.commentNumInt == 0) {
                    tv_answer_reply_count.gone()
                }
                iv_answer_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_answer_nickname.text = data.nickname
                if (hasAdoptedAnswer) {
                    tv_adopted.visible()
                }
                tv_answer_content.text = data.content
                tv_answer_publish_at.text = timeDescription(System.currentTimeMillis(), data.createdAt)
                tv_answer_reply_count.text = context.getString(R.string.qa_answer_reply_count, data.commentNum)
                tv_answer_praise_count.setPraise(data.praiseNum, data.isPraised)

            }
        }
    }
}