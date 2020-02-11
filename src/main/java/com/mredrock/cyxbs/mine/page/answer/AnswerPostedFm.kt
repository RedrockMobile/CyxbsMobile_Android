package com.mredrock.cyxbs.mine.page.answer

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.ANSWER_ID
import com.mredrock.cyxbs.common.config.QA_COMMENT_LIST
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AnswerPosted
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter

/**
 * Created by roger on 2019/12/3
 */
class AnswerPostedFm : BaseRVFragment<AnswerPosted>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AnswerViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loadAnswerPostedList()
        viewModel.eventOnAnswerPosted.observe(this, Observer {
            if (it == RvFooter.State.ERROR) {
                getFooter().showLoadError()
            } else if (it == RvFooter.State.NOMORE) {
                getFooter().showNoMore()
            }
        })
        viewModel.answerPosted.observe(this, Observer {
            setNewData(it)
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_answer_posted
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (getFooter().state == RvFooter.State.LOADING) {
            viewModel.loadAnswerPostedList()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AnswerPosted) {
        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_content).text = data.content
        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_disappear_at).text = data.answerTime
        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_integral).text = data.integral.toString()
        if (data.type == "已采纳") {
            holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_state).text = "已采纳"
        } else {
            holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_state).text = "未采纳"

        }
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(ANSWER_ID, data.answerId)
            ARouter.getInstance().build(QA_COMMENT_LIST).with(bundle).navigation()
        }
    }


    override fun onSwipeLayoutRefresh() {

        getFooter().showLoading()
        viewModel.cleanAnswerPostedPage()
        viewModel.loadAnswerPostedList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}