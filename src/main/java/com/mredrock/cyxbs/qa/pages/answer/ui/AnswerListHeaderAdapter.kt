package com.mredrock.cyxbs.qa.pages.answer.ui

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.questionTimeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_recycler_item_answer_header.view.*

/**
 * Created By jay68 on 2018/9/30.
 */
class AnswerListHeaderAdapter : BaseRvAdapter<Question>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HeaderViewHolder(parent)
    class HeaderViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycler_item_answer_header) {
        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                tv_question_content.text = data.description
                ngv_question.setImages(data.photoUrl)
                ngv_question.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.photoUrl.toTypedArray(), index)
                }
                val drawable = if (data.hasAdoptedAnswer) {
                    ContextCompat.getDrawable(context, R.drawable.qa_ic_reward_accept)
                } else {
                    ContextCompat.getDrawable(context, R.drawable.qa_ic_answer_list_reward)
                }
                tv_reward.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                iv_questioner_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_questioner_nickname.text = data.nickname
                tv_question_publish_at.text = questionTimeDescription(System.currentTimeMillis(), data.createdAt.toDate().time)
                tv_reward.text = context.getString(R.string.qa_question_item_reward, data.reward)
            }
        }

    }
}