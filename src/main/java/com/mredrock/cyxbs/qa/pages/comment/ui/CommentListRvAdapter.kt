package com.mredrock.cyxbs.qa.pages.comment.ui

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.component.JCardViewPlus
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Comment
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.utils.timeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_comment.view.*

/**
 * Created By jay68 on 2018/10/8.
 */
class CommentListRvAdapter(private val questionAnonymous: Boolean) : BaseEndlessRvAdapter<Comment>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
        }
    }

    var onReportClickListener: ((String) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(parent, questionAnonymous)

    class CommentViewHolder(parent: ViewGroup, private val questionAnonymous: Boolean) : BaseViewHolder<Comment>(parent, R.layout.qa_recycler_item_comment) {
        override fun refresh(data: Comment?) {
            data ?: return
            itemView.apply {
                if (questionAnonymous && data.questionIsSelf) {
                    iv_comment_avatar.setImageResource(R.drawable.common_default_avatar)
                    tv_comment_nickname.text = context.getString(R.string.qa_comment_anonymous_text)
                } else {
                    iv_comment_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                    tv_comment_nickname.text = data.nickname
                }

                tv_comment_create_at.text = timeDescription(System.currentTimeMillis(), data.createdAt)
                tv_comment_content.text = data.content
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Comment>, position: Int) {
        super.onBindViewHolder(holder, position)
        when (position % 3) {
            0 -> holder.itemView.findViewById<JCardViewPlus>(R.id.jCardView_comment).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.common_qa_comment_content_first_kind_color))
            1 -> holder.itemView.findViewById<JCardViewPlus>(R.id.jCardView_comment).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.common_qa_comment_content_second_kind_color))
            2 -> holder.itemView.findViewById<JCardViewPlus>(R.id.jCardView_comment).setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.common_qa_comment_content_third_kind_color))
        }
        holder.itemView.apply {
            when {
                (getItem(position) as Comment).isSelf -> {
                    ib_comment_item_more.invisible()
                }
                else -> {
                    ib_comment_item_more.visible()
                    ib_comment_item_more.setOnClickListener { onReportClickListener?.invoke((getItem(position) as Comment).id) }
                }
            }
        }
    }
}