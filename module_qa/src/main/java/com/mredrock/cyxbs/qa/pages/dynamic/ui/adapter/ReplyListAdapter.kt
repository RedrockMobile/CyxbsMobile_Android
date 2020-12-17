package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_reply_inner.view.*

/**
 *@author zhangzhe
 *@date 2020/12/15
 *@description
 */

class ReplyListAdapter(private val onReplyInnerClickEvent: (nickname: String, replyId: String) -> Unit, private val onReplyInnerLongClickEvent: (comment: Comment) -> Unit) : BaseRvAdapter<Comment>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Comment> = ReplyViewHolder(parent)


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
                qa_tv_reply_inner_praise_count.text = data.praiseCount.toString()
                qa_iv_reply_inner_praise_count_image.isChecked = data.isPraised
                qa_iv_reply_inner_avatar.setAvatarImageFromUrl(data.avatar)
            }
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment) {
        onReplyInnerClickEvent.invoke(data.nickName, data.commentId)
        Toast.makeText(BaseApp.context, "${data.nickName}, ${data.commentId}", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment) {
        onReplyInnerLongClickEvent.invoke(data)
        Toast.makeText(BaseApp.context, data.toString(), Toast.LENGTH_SHORT).show()
    }


}