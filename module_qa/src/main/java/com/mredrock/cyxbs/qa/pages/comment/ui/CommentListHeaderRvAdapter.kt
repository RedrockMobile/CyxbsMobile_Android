package com.mredrock.cyxbs.qa.pages.comment.ui

import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.AnswerDetail
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.setAdoptedTv
import kotlinx.android.synthetic.main.qa_recycler_item_comment_header.view.*

/**
 * Created By jay68 on 2018/10/8.
 */
class CommentListHeaderRvAdapter(
        private val removeAdoptIcon: Boolean
) : BaseRvAdapter<AnswerDetail>() {
    var onAdoptClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            HeaderViewHolder(removeAdoptIcon, parent)

    inner class HeaderViewHolder(private val removeAdoptIcon: Boolean,
                                 parent: ViewGroup) : BaseViewHolder<AnswerDetail>(parent, R.layout.qa_recycler_item_comment_header) {

        override fun refresh(data: AnswerDetail?) {
            data ?: return
            itemView.apply {
                iv_answer_avatar.setAvatarImageFromUrl(data.avatar)
                tv_answer_nickname.text = data.nikeName
                tv_answer_content.text = data.content
//                tv_answer_create_at.text = timeDescription(System.currentTimeMillis(), data.createAt.toString())
                ngv_answer.setImages(data.photoUrls)
                ngv_answer.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.photoUrls.toTypedArray(), index)
                }
                setAdoptedTv(tv_adopted, tv_adopt, data.answerIsAdopted, removeAdoptIcon)
                tv_adopt.setOnClickListener {
                    onAdoptClickListener?.invoke(data.answerId)
                }
            }
        }
    }
}