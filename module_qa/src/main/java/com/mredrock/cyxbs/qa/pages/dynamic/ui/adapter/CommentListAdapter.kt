package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_reply.view.*

/**
 *@author zhangzhe
 *@date 2020/12/14
 *@description
 */

class CommentListAdapter(
        private val onItemClickEvent: (commentId: String) -> Unit,
        private val onReplyInnerClickEvent: (nickname: String, replyId: String) -> Unit,
        private val onItemLongClickEvent: (comment: Comment) -> Unit,
        private val onReplyInnerLongClickEvent: (comment: Comment) -> Unit
) : BaseRvAdapter<Comment>() {


//    val managerMap: HashMap<CommentViewHolder, RecyclerView.LayoutManager> by lazy {
//        HashMap<CommentViewHolder, RecyclerView.LayoutManager>()
//    }

    override fun onItemClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment) {
        onItemClickEvent.invoke(data.commentId)
    }

    override fun onItemLongClickListener(holder: BaseViewHolder<Comment>, position: Int, data: Comment) {
        onItemLongClickEvent.invoke(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Comment> = CommentViewHolder(parent)

    inner class CommentViewHolder(parent: ViewGroup) : BaseViewHolder<Comment>(parent, R.layout.qa_recycler_item_dynamic_reply) {
        override fun refresh(data: Comment?) {
            data ?: return
            itemView.apply {
                qa_tv_reply_nickname.text = data.nickName
                qa_tv_reply_publish_at.text = dynamicTimeDescription(System.currentTimeMillis(), data.publishTime * 1000)
                qa_tv_reply_praise_count.text = data.praiseCount.toString()
                qa_tv_reply_content.text = data.content
                qa_iv_reply_praise_count_image.isChecked = data.isPraised
                qa_iv_reply_avatar.setAvatarImageFromUrl(data.avatar)
                if (data.pics.isNullOrEmpty())
                    qa_reply_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                else {
                    data.pics!!.map {
                        it
                    }.apply {
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
                    }

                }

                if (qa_rv_reply.layoutManager == null) {
                    qa_rv_reply.layoutManager = LinearLayoutManager(context)
                }
                if (qa_rv_reply.adapter == null) {
                    qa_rv_reply.adapter = ReplyListAdapter(onReplyInnerClickEvent, onReplyInnerLongClickEvent)
                }
                if (data.replyList.isNullOrEmpty()) {
                    (qa_rv_reply.adapter as ReplyListAdapter).refreshData(listOf())
                } else {
                    (qa_rv_reply.adapter as ReplyListAdapter).refreshData(data.replyList.takeLast(3))
                }
            }
        }
    }

//    override fun onViewDetachedFromWindow(holder: BaseViewHolder<Comment>) {
//
//        super.onViewDetachedFromWindow(holder)
//    }


}