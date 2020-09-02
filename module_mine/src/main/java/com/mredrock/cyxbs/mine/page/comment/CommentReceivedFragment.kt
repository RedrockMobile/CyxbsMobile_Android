package com.mredrock.cyxbs.mine.page.comment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.IS_COMMENT
import com.mredrock.cyxbs.common.config.NAVIGATE_FROM_WHERE
import com.mredrock.cyxbs.common.config.QA_COMMENT_LIST
import com.mredrock.cyxbs.common.event.OpenShareCommentEvent
import com.mredrock.cyxbs.common.utils.extensions.loadAvatar
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.CommentReceived
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_comment_repsonse.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by roger on 2020/2/10
 * 收到的评论
 */
class CommentReceivedFragment : BaseRVFragment<CommentReceived>() {
    private val viewModel by lazy { ViewModelProvider(this).get(CommentViewModel::class.java) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.eventOnCommentReceived.observe(viewLifecycleOwner, Observer {
            setState(it)
        })
        viewModel.commentReceivedList.observe(viewLifecycleOwner, Observer {
            setNewData(it)
        })
        viewModel.loadCommentReceivedList()
        viewModel.navigateEventOnReComment.observe(viewLifecycleOwner, Observer {
            EventBus.getDefault().postSticky(OpenShareCommentEvent(it.qid.toString(), it.data))
            ARouter.getInstance().build(QA_COMMENT_LIST).withInt(NAVIGATE_FROM_WHERE, IS_COMMENT).navigation()
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_comment_repsonse
    }

    override fun bindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun bindDataHolder(holder: RecyclerView.ViewHolder, position: Int, data: CommentReceived) {
        holder.itemView.context.loadAvatar(data.commenterImageUrl, holder.itemView.mine_comment_iv_avatar)
        holder.itemView.mine_comment_tv_nickname.text = data.commenterNickname
        holder.itemView.mine_comment_tv_response.text = data.commentContent
        holder.itemView.setOnClickListener {
            //点击评论的item实际上也是跳转到具体的回答页面
            viewModel.getAnswerFromReComment(data.questionId, data.answerId)
        }
    }

    override fun onSwipeLayoutRefresh() {
        setState(RvFooter.State.LOADING)
        viewModel.cleanCommentReceivedPage()
        viewModel.loadCommentReceivedList()
        getSwipeLayout().isRefreshing = false
    }

}