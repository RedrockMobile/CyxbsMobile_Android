package com.mredrock.cyxbs.mine.page.answer

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AnswerPosted
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment

/**
 * Created by roger on 2019/12/3
 */
class AnswerPostedFm : BaseRVFragment<AnswerPosted>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AnswerViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        loadMore()
//        val answer = AnswerPosted("id", "15", "房子在涨房子在涨房子在涨房子在涨房子在涨房子在涨", "2019.3.1", "这是个问题", "2019.8.22", "2019-7-1", 20)
//        val list: MutableList<AnswerPosted> = mutableListOf()
//        for (i in 1..20) {
//            list.add(answer)
//        }
//        viewModel.answerPostedEvent.postValue(list)
    }

    private fun initObserver() {
        viewModel.errorEvent.observe(this, Observer {
            getFooter().showLoadError()
        })
        viewModel.answerPostedEvent.observe(this, Observer {
            loadIntoRv(it)
        })
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        getFooter().showLoading()
    }

    /**
     * 添加数据到recyclerView中，并显示没有更多
     */
    private fun loadIntoRv(list: List<AnswerPosted>?) {
        if (list == null) {
            return
        }
        if (list.isEmpty()) {
            getFooter().showNoMore()
        } else {
            addData(list)
        }
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_answer_posted
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        getFooter().showLoading()
        loadMore()
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AnswerPosted) {
        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_content).text = data.content
//        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_disappear_at).text = data.disappearAt
//        holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_integral).text = data.integral.toString()
//        if (data.state == 0) {
//            holder.itemView.findViewById<TextView>(R.id.mine_answer_posted_tv_state).text = "已采纳"
//        }
    }


    override fun onSwipeLayoutRefresh() {
        viewModel.cleanPage()
        clearData()
        loadMore()
        getSwipeLayout().isRefreshing = false
    }
}