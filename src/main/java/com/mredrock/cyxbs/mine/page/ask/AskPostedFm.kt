package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.config.QUESTION_ID
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_ask_posted.view.*

/**
 * Created by roger on 2019/12/1
 */
class AskPostedFm : BaseRVFragment<AskPosted>() {


    private val viewModel by lazy { ViewModelProviders.of(this).get(AskViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loadAskPostedList()
        viewModel.askPosted.observe(this, Observer {
            setNewData(it)
        })
        viewModel.eventOnAskPosted.observe(this, Observer {
            if (it == true) {
                getFooter().showNoMore()
            } else {
                getFooter().showLoadError()
            }
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_ask_posted
    }

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        //通过footer来判断是否继续加载
        if (getFooter().state == RvFooter.State.LOADING) {
            viewModel.loadAskPostedList()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AskPosted) {
        holder.itemView.mine_ask_posted_tv_title.text = data.title
        holder.itemView.mine_ask_posted_tv_disappear_at.text = data.disappearAt
        holder.itemView.mine_ask_posted_tv_integral.text = data.integral.toString()
        holder.itemView.mine_ask_posted_tv_description.text = data.description
        holder.itemView.mine_ask_posted_tv_state.text = data.type
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(QUESTION_ID, data.questionId)
            ARouter.getInstance().build(QA_ANSWER_LIST).with(bundle).navigation()
        }
    }


    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        viewModel.cleanAskPostedPage()
        viewModel.loadAskPostedList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}