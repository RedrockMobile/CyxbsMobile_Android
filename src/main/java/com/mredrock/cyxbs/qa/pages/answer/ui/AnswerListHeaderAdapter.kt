package com.mredrock.cyxbs.qa.pages.answer.ui

import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.setNicknameTv
import com.mredrock.cyxbs.qa.utils.timeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_recycler_item_answer_header.view.*

/**
 * Created By jay68 on 2018/9/30.
 */
class AnswerListHeaderAdapter(private val onSortOrderChangedListener: (sortOrder: String) -> Unit) : BaseRvAdapter<Question>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HeaderViewHolder(parent).apply {
        onSortOrderChangedListener = this@AnswerListHeaderAdapter.onSortOrderChangedListener
    }

    class HeaderViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycler_item_answer_header) {
        var onSortOrderChangedListener: ((sortOrder: String) -> Unit)? = null

        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                tv_question_content.text = data.description
                ngv_question.setImages(data.photoUrl)
                ngv_question.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.photoUrl[index])
                }
                iv_questioner_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_questioner_nickname.setNicknameTv(data.nickname, data.isEmotion && !data.isAnonymous, data.isMale)
                setDisappear(tv_question_publish_at, data.disappearAt)
                tv_reward.text = context.getString(R.string.qa_question_item_reward, data.reward)
            }
        }

        private fun setDisappear(tv: TextView, rowTime: String) {
            tv.text = context.getString(R.string.qa_question_item_disappear,
                    timeDescription(System.currentTimeMillis(), rowTime.toDate().time))
        }



    }
}