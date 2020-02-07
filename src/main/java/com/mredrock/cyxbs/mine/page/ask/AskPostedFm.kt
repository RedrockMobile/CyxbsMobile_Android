package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AskPosted
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import kotlinx.android.synthetic.main.mine_list_item_my_ask_posted.*

/**
 * Created by roger on 2019/12/1
 */
class AskPostedFm : BaseRVFragment<AskPosted>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AskViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        loadMore()
//        val ask = AskPosted("id", "这道题不好做", "这道题不好做这道题不好做这道题不好做这道题不好做这道题不好做", "2019.12.1", "2019.11.12", "2019.11.13", 20, 1)
//        val list: MutableList<AskPosted> = mutableListOf()
//        for (i in 1..20) {
//            list.add(ask)
//        }
//        viewModel.askPostedEvent.postValue(list)
    }

    private fun initObserver() {
        viewModel.errorEvent.observe(this, Observer {
            getFooter().showLoadError()
        })
        viewModel.askPostedEvent.observe(this, Observer {
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
    private fun loadIntoRv(list: List<AskPosted>?) {
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
        return R.layout.mine_list_item_my_ask_posted
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        getFooter().showLoading()
        loadMore()
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AskPosted) {
        holder.itemView.apply {
            mine_ask_posted_tv_title.text = data.title
            mine_ask_posted_tv_disappear_at.text = data.disappearAt
            mine_ask_posted_tv_integral.text = data.integral.toString()
            mine_ask_posted_tv_description.text = data.description
            mine_ask_posted_tv_state.text = data.type
        }
    }


    override fun onSwipeLayoutRefresh() {
        viewModel.cleanPage()
        clearData()
        loadMore()
        getSwipeLayout().isRefreshing = false
    }
}