package com.mredrock.cyxbs.qa.pages.mine.ui.adapter

import android.app.Activity
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.dynamic.ui.activity.DynamicDetailActivity
import com.mredrock.cyxbs.qa.pages.mine.ui.MyCommentActivity
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder.MyCommentViewHolder
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyCommentViewModel
import kotlinx.android.synthetic.main.qa_activity_dynamic_detail.view.*
import kotlinx.android.synthetic.main.qa_activity_my_comment.*
import kotlinx.android.synthetic.main.qa_recycler_item_my_comment.*
import kotlinx.android.synthetic.main.qa_recycler_item_my_comment.view.*

/**
 * @date 2021-03-06
 * @author Sca RayleighZ
 */
class MyCommentRvAdapter(private val activity: MyCommentActivity, private val viewModel: MyCommentViewModel) : BaseEndlessRvAdapter<CommentWrapper>(DIFF_CALLBACK) {

    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommentWrapper>() {
            override fun areItemsTheSame(oldItem: CommentWrapper, newItem: CommentWrapper) = oldItem.comment.postId == newItem.comment.postId
            override fun areContentsTheSame(oldItem: CommentWrapper, newItem: CommentWrapper) = oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<CommentWrapper>, position: Int) {
        super.onBindViewHolder(holder, position)
        //这里仅仅注册点击事件，不执行view的填充，后者将在holder的Refresh方法进行实现
        getItem(position)?.comment?.let { comment ->
            holder.itemView.apply {
                qa_cl_my_comment_item.setOnClickListener {
                    //执行跳转操作
                    if (comment.postId == "0"){
                        BaseApp.context.toast("该评论已被删除")
                    } else {
                        DynamicDetailActivity.activityStart(activity, comment.postId)
                    }
                }
                qa_iv_my_comment_item_reply.setOnClickListener {
                    activity.qa_cl_my_comment_reply.visibility = View.VISIBLE
                    activity.showKeyboard(comment)
                }
                qa_like_view_my_comment_item_praise.setOnClickListener {
                    //执行点赞
                    viewModel.praise(comment.commentId){
                        //改变状态
                        qa_like_view_my_comment_item_praise.isChecked = !qa_like_view_my_comment_item_praise.isChecked
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CommentWrapper> {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.qa_recycler_item_my_comment, parent, false)
        return MyCommentViewHolder(itemView)
    }
}