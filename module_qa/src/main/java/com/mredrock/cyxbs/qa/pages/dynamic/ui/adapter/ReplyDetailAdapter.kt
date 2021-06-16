package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_reply_detail.view.*

/**
 *@author zhangzhe
 *@date 2020/12/29
 *@description
 */

class ReplyDetailAdapter(private val isReplyDetail: Boolean, private val onReplyInnerClickEvent: (comment: Comment) -> Unit, private val onReplyInnerLongClickEvent: (comment: Comment, itemView: View) -> Unit, private val onReplyMoreDetailClickEvent: (replyIdScreen: String) -> Unit) : BaseRvAdapter<Comment>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Comment> = ReplyViewHolder(parent)


    inner class ReplyViewHolder(parent: ViewGroup) : BaseViewHolder<Comment>(parent, R.layout.qa_recycler_item_dynamic_reply_detail) {
        @SuppressLint("SetTextI18n")
        override fun refresh(data: Comment?) {
            data ?: return
            itemView.apply {
                qa_tv_reply_detail_date.text = dynamicTimeDescription(System.currentTimeMillis(), data.publishTime * 1000)
                qa_tv_reply_detail_nickname.text = data.nickName
                if (data.fromNickname.isEmpty()) {
                    qa_tv_reply_detail_content.setContent(data.content)
                    qa_tv_reply_detail_show_detail.gone()
                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        // 回复时，被回复人名称显示颜色
//                        val span = SpannableString("回复 @${data.fromNickname} : ${data.content}").apply {
//                            setSpan(
//                                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.qa_reply_inner_reply_name_color)),
//                                    3, 3 + data.fromNickname.length + 1,
//                                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
//                            )
//                        }
//                        qa_tv_reply_detail_content.text = span
//                    } else {
//                        qa_tv_reply_detail_content.text = "回复 @${data.fromNickname} : ${data.content}"
//                    }
                    qa_tv_reply_detail_content.setContent("回复 @${data.fromNickname} : ${data.content}")


                    qa_tv_reply_detail_show_detail.visible()
                    qa_tv_reply_detail_show_detail.setOnSingleClickListener {
                        onReplyMoreDetailClickEvent.invoke(data.replyId)
                    }
                }
                if (isReplyDetail) {
                    qa_tv_reply_detail_show_detail.gone()
                }
                qa_iv_reply_detail_praise_count_image.registerLikeView(data.commentId, CommentConfig.PRAISE_MODEL_COMMENT, data.isPraised, data.praiseCount)
                qa_iv_reply_detail_praise_count_image.setOnSingleClickListener {
                    qa_iv_reply_detail_praise_count_image.click()
                }
                qa_iv_reply_detail_avatar.setAvatarImageFromUrl(data.avatar)
            }
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment) {
        onReplyInnerClickEvent.invoke(data)
    }

    override fun onItemLongClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment, itemView: View) {
        onReplyInnerLongClickEvent.invoke(data, itemView)
    }

    override fun refreshData(dataCollection: Collection<Comment>) {
        notifyItemRangeRemoved(0, dataList.size)
        dataList.clear()
        dataList.addAll(dataCollection)
        notifyItemRangeInserted(0, dataList.size)
    }


}