package com.mredrock.cyxbs.mine.page.comment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.ANSWER_ID
import com.mredrock.cyxbs.common.config.QA_COMMENT_LIST
import com.mredrock.cyxbs.common.config.QUESTION_ID
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Comment
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_comment_comment.view.*

/**
 * Created by roger on 2019/12/5
 * 发出的评论
 */
class CommentFragment : BaseRVFragment<Comment>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(CommentViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.loadCommentList()
        viewModel.eventOnComment.observe(this, Observer {
            when (it) {
                RvFooter.State.ERROR -> {
                    getFooter().showLoadError()
                }
                RvFooter.State.NOMORE -> {
                    getFooter().showNoMore()
                }
                RvFooter.State.NOTHING -> {
                    getFooter().showNothing()
                }
            }
        })
        viewModel.commentList.observe(this, Observer {
            setNewData(it)
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
            val bundle = Bundle()
            bundle.putInt(ANSWER_ID, data.answerId)
            bundle.putInt(QUESTION_ID, data.questionId)
            ARouter.getInstance().build(QA_COMMENT_LIST).with(bundle).navigation()
        }
    }

    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        viewModel.cleanCommentPage()
        viewModel.loadCommentList()
        getSwipeLayout().isRefreshing = false
    }
}