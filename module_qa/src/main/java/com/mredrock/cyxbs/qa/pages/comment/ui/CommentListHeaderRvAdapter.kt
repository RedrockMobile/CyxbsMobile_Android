package com.mredrock.cyxbs.qa.pages.comment.ui

import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.setAdoptedTv
import com.mredrock.cyxbs.qa.utils.timeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_comment_header.view.*
import com.mredrock.cyxbs.common.utils.extensions.*


/**
 * Created By jay68 on 2018/10/8.
 */
class CommentListHeaderRvAdapter(
        private val removeAdoptIcon: Boolean
) : BaseRvAdapter<Answer>() {
    var onAdoptClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            HeaderViewHolder(removeAdoptIcon, parent)

    inner class HeaderViewHolder(private val removeAdoptIcon: Boolean,
                                 parent: ViewGroup) : BaseViewHolder<Answer>(parent, R.layout.qa_recycler_item_comment_header) {

        override fun refresh(data: Answer?) {
            data ?: return
            itemView.apply {
                iv_answer_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_answer_nickname.text = data.nickname
                tv_answer_content.text = data.content
                tv_answer_create_at.text = timeDescription(System.currentTimeMillis(), data.createdAt)
                ngv_answer.setImages(data.photoUrl)
                ngv_answer.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.photoUrl.toTypedArray(), index)
                }
                setAdoptedTv(tv_adopted, tv_adopt, data.isAdopted, removeAdoptIcon)
                tv_adopt.setOnSingleClickListener {
                    onAdoptClickListener?.invoke(data.id)
                }
            }
        }
    }
}