package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.extension.logr
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import kotlinx.android.synthetic.main.mine_list_item_my_ask_draft.view.*

/**
 * Created by roger on 2019/12/3
 */
class AskDraftFm : BaseRVFragment<Draft>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AskViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        loadMore()
    }

    private fun initObserver() {
        viewModel.errorEvent.observe(this, Observer {
            getFooter().showLoadError()
        })
        viewModel.askDraftEvent.observe(this, Observer {
            loadIntoRv(it)
        })
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        viewModel.loadAskDraftList()
        getFooter().showLoading()
    }

    /**
     * 添加数据到recyclerView中，并显示没有更多
     */
    private fun loadIntoRv(list: List<Draft>?) {
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
        return R.layout.mine_list_item_my_ask_draft
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        getFooter().showLoading()
        loadMore()
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: Draft) {
        holder.itemView.mine_ask_draft_tv_title.text = data.question?.title
        holder.itemView.mine_ask_draft_tv_description.text = data.question?.description
        holder.itemView.mine_ask_draft_tv_lastedit_at.text = data.createdAt
    }


    override fun onSwipeLayoutRefresh() {
        viewModel.cleanPage()
        clearData()
        loadMore()
        getSwipeLayout().isRefreshing = false
    }
}