package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_reply.view.*

/**
 *@author zhangzhe
 *@date 2020/12/14
 *@description
 */

class CommentListAdapter(
        private val onItemClickEvent: (comment: Comment) -> Unit,
        private val onReplyInnerClickEvent: (comment: Comment) -> Unit,
        private val onItemLongClickEvent: (comment: Comment, view: View) -> Unit,
        private val onReplyInnerLongClickEvent: (comment: Comment, view: View) -> Unit,
        private val onMoreReplyClickEvent: (replyList: String) -> Unit
) : BaseRvAdapter<Comment>() {


//    val managerMap: HashMap<CommentViewHolder, RecyclerView.LayoutManager> by lazy {
//        HashMap<CommentViewHolder, RecyclerView.LayoutManager>()
//    }

    override fun onItemClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment) {
        onItemClickEvent.invoke(data)
    }

    override fun onItemLongClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment, itemView: View) {
        onItemLongClickEvent.invoke(data, itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Comment> = CommentViewHolder(parent)

    inner class CommentViewHolder(parent: ViewGroup) : BaseViewHolder<Comment>(parent, R.layout.qa_recycler_item_dynamic_reply) {
        override fun refresh(data: Comment?) {
            data ?: return
            itemView.apply {
                qa_tv_reply_nickname.text = data.nickName
                qa_tv_reply_publish_at.text = dynamicTimeDescription(System.currentTimeMillis(), data.publishTime * 1000)

                qa_tv_reply_content.setContent(data.content)
                qa_iv_reply_praise_count_image.isChecked = data.isPraised
                qa_iv_reply_avatar.setAvatarImageFromUrl(data.avatar)
                if (data.pics.isNullOrEmpty())
                    qa_reply_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                else {
                    data.pics!!.apply {
                        val tag = qa_reply_nine_grid_view.tag
                        if (null == tag || tag == this) {
                            val tagStore = qa_reply_nine_grid_view.tag
                            qa_reply_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                            qa_reply_nine_grid_view.tag = tagStore
                        } else {
                            val tagStore = this
                            qa_reply_nine_grid_view.tag = null
                            qa_reply_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                            qa_reply_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                            qa_reply_nine_grid_view.tag = tagStore
                        }
                        qa_reply_nine_grid_view.setOnItemClickListener { _, index ->
                            ViewImageActivity.activityStart(context, this.toTypedArray(), index)
                        }
                    }

                }

                if (qa_rv_reply.layoutManager == null) {
                    qa_rv_reply.layoutManager = LinearLayoutManager(context)
                }
                if (qa_rv_reply.adapter == null) {
                    qa_rv_reply.adapter = ReplyListAdapter(onReplyInnerClickEvent, onReplyInnerLongClickEvent, {})
                }
                if (data.replyList.isNullOrEmpty()) {
                    (qa_rv_reply.adapter as ReplyListAdapter).refreshData(listOf())
                    (qa_rv_reply.adapter as ReplyListAdapter).onMoreClickEvent = {}

                } else {
                    (qa_rv_reply.adapter as ReplyListAdapter).refreshData(data.replyList)
                    (qa_rv_reply.adapter as ReplyListAdapter).onMoreClickEvent = { onMoreReplyClickEvent.invoke(data.commentId) }
                }
                qa_iv_reply_praise_count_image.registerLikeView(data.commentId, CommentConfig.PRAISE_MODEL_COMMENT, data.isPraised, data.praiseCount)
                qa_iv_reply_praise_count_image.setOnSingleClickListener {
                    qa_iv_reply_praise_count_image.click()
                }
            }
        }
    }

//    override fun onViewDetachedFromWindow(holder: BaseViewHolder<Comment>) {
//
//        super.onViewDetachedFromWindow(holder)
//    }


}