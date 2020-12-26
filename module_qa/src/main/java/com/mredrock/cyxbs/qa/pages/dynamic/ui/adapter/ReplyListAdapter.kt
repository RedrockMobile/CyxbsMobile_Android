package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.config.CommentConfig
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_reply_inner.view.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_reply_show_more.view.*

/**
 *@author zhangzhe
 *@date 2020/12/15
 *@description
 */

class ReplyListAdapter(private val onReplyInnerClickEvent: (nickname: String, commentId: String) -> Unit, private val onReplyInnerLongClickEvent: (comment: Comment, itemView: View) -> Unit, private val onMoreClickEvent: () -> Unit) : BaseRvAdapter<Comment>() {

    override fun getItemCount(): Int {
        return if (dataList.size > 3) 4 else super.getItemCount()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Comment>, position: Int) {
        if (position == 3) {
            holder.refresh(null)
            holder.itemView.setOnSingleClickListener { onMoreClickEvent.invoke() }
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            3 -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Comment> {
        return when (viewType) {
            0 -> ReplyViewHolder(parent)
            else -> MoreViewHolder(parent)
        }
    }


    inner class ReplyViewHolder(parent: ViewGroup) : BaseViewHolder<Comment>(parent, R.layout.qa_recycler_item_dynamic_reply_inner) {
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.N)
        override fun refresh(data: Comment?) {
            data ?: return
            itemView.apply {
                qa_tv_reply_inner_nickname.text = data.nickName
                if (data.fromNickname.isEmpty()) {
                    qa_tv_reply_inner_content.text = data.content
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val s = "回复 <font color=\"#0000FF\">@${data.fromNickname}</font> : ${data.content}"
                        qa_tv_reply_inner_content.text = Html.fromHtml(s, FROM_HTML_MODE_COMPACT)
                    } else {
                        qa_tv_reply_inner_content.text = "回复 @${data.fromNickname} : "
                    }
                }
                qa_iv_reply_inner_praise_count_image.registerLikeView(data.commentId, CommentConfig.PRAISE_MODEL_COMMENT, data.isPraised, data.praiseCount)
                qa_iv_reply_inner_praise_count_image.setOnSingleClickListener {
                    qa_iv_reply_inner_praise_count_image.click()
                }
                qa_iv_reply_inner_avatar.setAvatarImageFromUrl(data.avatar)
            }
        }
    }

    inner class MoreViewHolder(parent: ViewGroup) : BaseViewHolder<Comment>(parent, R.layout.qa_recycler_item_dynamic_reply_show_more) {
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.N)
        override fun refresh(data: Comment?) {
            itemView.apply {
                qa_tv_reply_show_more.text = "共${dataList.size}条回复 >"
            }
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment) {
        onReplyInnerClickEvent.invoke(data.nickName, data.commentId)
    }

    override fun onItemLongClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment, itemView: View) {
        onReplyInnerLongClickEvent.invoke(data, itemView)
    }

    override fun refreshData(dataCollection: Collection<Comment>) {
        notifyItemRangeRemoved(0, dataList.size)
        dataList.clear()
        dataList.addAll(dataCollection.sortedBy { it.praiseCount }.reversed())
        notifyItemRangeInserted(0, dataList.size)
    }


}