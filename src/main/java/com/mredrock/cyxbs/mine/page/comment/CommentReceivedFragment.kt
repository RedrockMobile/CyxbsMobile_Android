package com.mredrock.cyxbs.mine.page.comment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.ANSWER_ID
import com.mredrock.cyxbs.common.config.QA_COMMENT_LIST
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.CommentReceived
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_comment_repsonse.view.*

/**
 * Created by roger on 2020/2/10
 * 收到的评论
 */
class CommentReceivedFragment : BaseRVFragment<CommentReceived>() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(CommentViewModel::class.java) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.eventOnCommentReceived.observe(this, Observer {
            if (it == RvFooter.State.ERROR) {
                getFooter().showLoadError()
            } else if (it == RvFooter.State.NOMORE) {
                getFooter().showNoMore()
            }
        })
        viewModel.commentReceivedList.observe(this, Observer {
            setNewData(it)
        })
        viewModel.loadCommentReceivedList()
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_comment_repsonse
    }

    override fun bindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getFooter().state == RvFooter.State.LOADING) {
            viewModel.loadCommentReceivedList()
        }
    }

    override fun bindDataHolder(holder: RecyclerView.ViewHolder, position: Int, data: CommentReceived) {
        holder.itemView.mine_comment_circleimageview_avatar.setImageFromUrl(data.commenterImageUrl)
        holder.itemView.mine_comment_tv_nickname.text = data.commenterNickname
        holder.itemView.mine_comment_tv_response.text = data.commentContent
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(ANSWER_ID, data.answerId)
            ARouter.getInstance().build(QA_COMMENT_LIST).with(bundle).navigation()
        }
    }

    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        viewModel.cleanCommentReceivedPage()
        viewModel.loadCommentReceivedList()
        getSwipeLayout().isRefreshing = false
    }

}