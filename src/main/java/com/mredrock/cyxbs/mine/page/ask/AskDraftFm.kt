package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.component.CommonDialogFragment
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

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AskDraft) {
        holder.itemView.mine_ask_draft_tv_title.text = data.title
        holder.itemView.mine_ask_draft_tv_description.text = data.description
        holder.itemView.mine_ask_draft_tv_lastedit_at.text = data.latestEditTime
        holder.itemView.mine_ask_draft_iv_garbage.setOnClickListener {
            CommonDialogFragment().apply {
                initView(
                        containerRes = R.layout.mine_layout_dialog_with_title_and_content,
                        positiveString = "删除",
                        onPositiveClick = {
                            viewModel.deleteDraftById(data.draftQuestionId)
                            dismiss()
                        },
                        onNegativeClick = { dismiss() },
                        elseFunction = {
                            it.findViewById<TextView>(R.id.dialog_title).text = "删除草稿";
                            it.findViewById<TextView>(R.id.dialog_content).text = "确定要删除该草稿吗?";
                        }

                )
            }.show(fragmentManager, "delete_draft")
        }
    }


    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        viewModel.cleanAskDraftPage()
        viewModel.loadAskDraftList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}