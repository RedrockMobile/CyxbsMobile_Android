package com.mredrock.cyxbs.qa.pages.comment.ui

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Comment
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.utils.setNicknameTv
import com.mredrock.cyxbs.qa.utils.timeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_recycler_item_comment.view.*

/**
 * Created By jay68 on 2018/10/8.
 */
class CommentListRvAdapter(private val isEmotion: Boolean) : BaseEndlessRvAdapter<Comment>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(isEmotion, parent)

    class CommentViewHolder(private val isEmotion: Boolean,
                            parent: ViewGroup) : BaseViewHolder<Comment>(parent, R.layout.qa_recycler_item_comment) {
        override fun refresh(data: Comment?) {
            data ?: return
            itemView.apply {
                iv_comment_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_comment_nickname.setNicknameTv(data.nickname, isEmotion, data.isMale)
                setDate(tv_comment_create_at, data.createdAt)
                tv_comment_content.text = data.content
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