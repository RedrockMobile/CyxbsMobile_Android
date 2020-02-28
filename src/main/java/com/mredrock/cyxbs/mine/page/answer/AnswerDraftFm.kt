package com.mredrock.cyxbs.mine.page.answer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AnswerDraft
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_answer_draft.view.*

/**
 * Created by roger on 2019/12/3
 */
class AnswerDraftFm : BaseRVFragment<AnswerDraft>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AnswerViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadAnswerDraftList()
        viewModel.answerDraft.observe(this, Observer {
            setNewData(it)
        })
        viewModel.eventOnAnswerDraft.observe(this, Observer {
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
        return R.layout.mine_list_item_my_answer_draft
    }

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AnswerDraft) {
        holder.itemView.mine_answer_draft_tv_content.text = data.draftAnswerContent
        holder.itemView.mine_answer_draft_tv_lastedit_at.text = data.latestEditTime
    }


    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        viewModel.cleanAnswerDraftPage()
        viewModel.loadAnswerDraftList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}