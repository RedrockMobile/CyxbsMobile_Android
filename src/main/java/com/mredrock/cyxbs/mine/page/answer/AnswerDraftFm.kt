package com.mredrock.cyxbs.mine.page.answer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.mine.util.extension.logr
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.MineDialogFragment
import kotlinx.android.synthetic.main.mine_list_item_my_answer_draft.view.*

/**
 * Created by roger on 2019/12/3
 */
class AnswerDraftFm : BaseRVFragment<Draft>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AnswerViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        loadMore()
    }

    private fun initObserver() {
        viewModel.errorEvent.observe(this, Observer {
            getFooter().showLoadError()
        })
        viewModel.answerDraftEvent.observe(this, Observer {
            loadIntoRv(it)
        })
        viewModel.deleteEvent.observe(this, Observer {
            delete(it)
        })
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        viewModel.loadAnswerDraftList()
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
        return R.layout.mine_list_item_my_answer_draft
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        getFooter().showLoading()
        loadMore()
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: Draft) {
        holder.itemView.mine_answer_draft_tv_content.text = data.titleContent
        holder.itemView.mine_answer_draft_tv_lastedit_at.text = data.createdAt
        holder.itemView.mine_answer_draft_iv_garbage.setOnClickListener{
            dialogShowRemove(data)
        }
    }


    override fun onSwipeLayoutRefresh() {
        viewModel.cleanPage()
        clearData()
        loadMore()
        getSwipeLayout().isRefreshing = false
    }

    private fun removeItem(draft: Draft) {
        viewModel.deleteDraft(draft)
    }
    private fun dialogShowRemove(draft: Draft) {
        MineDialogFragment("删除草稿", "是否删除您编辑的草稿？", {removeItem(draft)}, {}).show(this.fragmentManager, "SaveInfo")

    }
}