package com.mredrock.cyxbs.mine.page.comment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.IS_COMMENT
import com.mredrock.cyxbs.common.config.NAVIGATE_FROM_WHERE
import com.mredrock.cyxbs.common.config.QA_COMMENT_LIST
import com.mredrock.cyxbs.common.config.QA_PARAM_ANSWER_ID
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Comment
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_comment_comment.view.*

/**
 * Created by roger on 2019/12/5
 * 发出的评论
 */
class CommentFragment : BaseRVFragment<Comment>() {

    private val viewModel by lazy { ViewModelProvider(this).get(CommentViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.loadCommentList()
        viewModel.eventOnComment.observe(viewLifecycleOwner, Observer {
            setState(it)
        })
        viewModel.commentList.observe(viewLifecycleOwner, Observer {
            setNewData(it)
        })
        viewModel.navigateEventOnComment.observe(viewLifecycleOwner, Observer {
            ARouter.getInstance().build(QA_COMMENT_LIST).withInt(NAVIGATE_FROM_WHERE, IS_COMMENT).withString(QA_PARAM_ANSWER_ID, it.id.toString()).navigation()
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_comment_comment
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: Comment) {
        holder.itemView.mine_comment_tv_at_who.text = data.answerer
        holder.itemView.mine_comment_tv_content.text = data.commentContent
        holder.itemView.setOnClickListener {
            //点击评论的item实际上也是跳转到具体的回答页面
            viewModel.getAnswerFromComment(data.questionId, data.answerId)
        }
    }

    override fun onSwipeLayoutRefresh() {
        setState(RvFooter.State.LOADING)
        viewModel.cleanCommentPage()
        viewModel.loadCommentList()
        getSwipeLayout().isRefreshing = false
    }
}