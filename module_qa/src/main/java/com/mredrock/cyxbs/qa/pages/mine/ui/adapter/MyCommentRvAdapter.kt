package com.mredrock.cyxbs.qa.pages.mine.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder.MyCommentViewHolder
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyCommentViewModel
import kotlinx.android.synthetic.main.qa_recycler_item_my_comment.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @date 2021-03-06
 * @author Sca RayleighZ
 */
class MyCommentRvAdapter(private val viewModel: MyCommentViewModel): BaseEndlessRvAdapter<CommentWrapper>(DIFF_CALLBACK) {

    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommentWrapper>() {
            override fun areItemsTheSame(oldItem: CommentWrapper, newItem: CommentWrapper) = oldItem.comment.postId == newItem.comment.postId
            override fun areContentsTheSame(oldItem: CommentWrapper, newItem: CommentWrapper) = oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<CommentWrapper>, position: Int) {
        //这里仅仅注册点击事件，不执行view的填充
        holder.itemView.apply {
            qa_cl_my_comment_item.setOnClickListener {
                //TODO: 跳转到问题详情界面
            }
            qa_iv_my_comment_item_praise.setOnClickListener {
                //TODO: 执行回复
            }
            qa_iv_my_comment_item_praise.setOnClickListener {
                //TODO: 执行点赞
            }
        }
        /*val comment = cwList[position].comment
        val from = cwList[position].from
        val dateFormat = SimpleDateFormat("回复了你的评论      yyyy.mm.dd  hh:mm", Locale.getDefault())
        //TODO: 超过24小时不再显示时间
        holder.itemView.apply {
            qa_tv_my_comment_item_nickname.text = comment.nickName
            qa_tv_my_comment_from.text = from
            qa_tv_my_comment_item_tip_and_date.text = dateFormat.format(Date(comment.publishTime))
            qa_tv_my_comment_item_comment.text = comment.content
            Glide.with(holder.itemView.context).load(comment.avatar).into(qa_circle_iv_my_comment_item_avatar)
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CommentWrapper> {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.qa_recycler_item_my_comment, parent, false)
        return MyCommentViewHolder(itemView)
    }
}