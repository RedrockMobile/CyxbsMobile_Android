package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.MyAskQuestion
import com.mredrock.cyxbs.mine.util.TimeUtil
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import kotlinx.android.synthetic.main.mine_list_item_my_help.view.*

/**
 * Created by zia on 2018/8/17.
 */
class AskAdoptedFm : BaseRVFragment<MyAskQuestion>() {

    val TYPE_ASK_OVER = 1
    val TYPE_ASK_WAIT = 2

    private val viewModel by lazy { ViewModelProviders.of(this).get(AskViewModel::class.java) }

    var type = 1

    fun setFragmentType(type: Int) {
        this.type = type
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        loadMore()
    }

    private fun initObserver() {
        viewModel.errorEvent.observe(this, Observer {
            getFooter().showLoadError()
//            貌似viewmodel的封装内置了toast
//            if (it != null) {
//                toast(it)
//            }
        })

        viewModel.askOverEvent.observe(this, Observer {
            loadIntoRv(it)
        })

        viewModel.askWaitEvent.observe(this, Observer {
            loadIntoRv(it)
        })
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        getFooter().showLoading()
        when (type) {
            TYPE_ASK_OVER -> {
                viewModel.loadAskOverList()
            }
            TYPE_ASK_WAIT -> {
                viewModel.loadAskWaitList()
            }
        }
    }

    /**
     * 添加数据到recyclerView中，并显示没有更多
     */
    private fun loadIntoRv(list: List<MyAskQuestion>?) {
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
        return R.layout.mine_list_item_my_help
    }

    //自动加载更多
    override fun bindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getFooter().showLoading()
        loadMore()
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: RecyclerView.ViewHolder, position: Int, data: MyAskQuestion) {
        holder.itemView.apply {
            //隐藏全部小红点，以后可能需要使用小红点
            mine_help_item_red_point.visibility = View.INVISIBLE

            mine_help_item_tv_question.text = "提问：${data.title}"

            //todo 问一问的点击跳转
            when (type) {
                TYPE_ASK_OVER -> {
                    mine_help_item_tv_answer.text = "采纳：${data.description}"
                    mine_help_item_tv_time.text = "解决时间：${TimeUtil.wrapTime(data.updatedAt)}"
                    setOnClickListener {

                    }
                }
                TYPE_ASK_WAIT -> {
                    mine_help_item_tv_answer.visibility = View.GONE
                    mine_help_item_tv_time.text = "发布时间：${TimeUtil.wrapTime(data.createdAt)}"
                    setOnClickListener {

                    }
                }
            }
        }
    }

    override fun onSwipeLayoutRefresh() {
        viewModel.cleanPage()
        clearData()
        loadMore()
        getSwipeLayout().isRefreshing = false
    }
}