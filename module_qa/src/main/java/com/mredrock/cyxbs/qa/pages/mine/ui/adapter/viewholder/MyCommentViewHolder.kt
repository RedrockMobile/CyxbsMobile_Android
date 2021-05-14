package com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder

import android.view.View
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_my_comment.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @date 2021-03-05
 * @author Sca RayleighZ
 */
class MyCommentViewHolder(val item: View) : BaseViewHolder<CommentWrapper>(item){
    override fun refresh(data: CommentWrapper?) {
        val comment = data?.comment
        val from = data?.from
        val dateFormat = SimpleDateFormat("回复了你的评论      yyyy.MM.dd  HH:mm", Locale.getDefault())
        item.apply {
            comment?.apply {
                qa_like_view_my_comment_item_praise.setCheckedWithoutAnimator(comment.isPraised)
                qa_tv_my_comment_item_nickname.text = nickName
                qa_tv_my_comment_from.text = from
                qa_tv_my_comment_item_tip_and_date.text = dateFormat.format(Date(publishTime*1000L))
                qa_tv_my_comment_item_comment.text = content
                qa_circle_iv_my_comment_item_avatar.setAvatarImageFromUrl(avatar)
            }
        }
    }
}