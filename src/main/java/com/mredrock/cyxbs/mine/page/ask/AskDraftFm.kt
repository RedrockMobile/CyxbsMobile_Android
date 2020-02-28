package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AskDraft
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_ask_draft.view.*

/**
 * Created by roger on 2019/12/3
 */
class AskDraftFm : BaseRVFragment<AskDraft>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AskViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadAskDraftList()
        viewModel.askDraft.observe(this, Observer {
            setNewData(it)
        })
        viewModel.eventOnAskDraft.observe(this, Observer {
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
    }


    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_ask_draft
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AskDraft) {
        holder.itemView.mine_ask_draft_tv_title.text = data.title
        holder.itemView.mine_ask_draft_tv_description.text = data.description
        holder.itemView.mine_ask_draft_tv_lastedit_at.text = data.latestEditTime
    }


    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        viewModel.cleanAskDraftPage()
        viewModel.loadAskDraftList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}