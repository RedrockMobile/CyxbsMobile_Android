package com.mredrock.cyxbs.mine.page.feedback.history.detail

import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHistoryDetailBinding
import com.mredrock.cyxbs.mine.page.feedback.base.ui.BaseMVPVMActivity

class HistoryDetailActivity :
    BaseMVPVMActivity<HistoryDetailViewModel, MineActivityHistoryDetailBinding, HistoryDetailPresenter>() {

    override fun createPresenter(): HistoryDetailPresenter = HistoryDetailPresenter()

    override fun getLayoutId(): Int = R.layout.mine_activity_history_detail

    private val replyBannerRvAdapter by lazy {
        ReplyBannerAdapter()
    }


    override fun initView() {
        binding?.apply {
            vm = viewModel
        }
        binding?.rvReplyBanner?.apply {
            adapter = replyBannerRvAdapter
            layoutManager = GridLayoutManager(this@HistoryDetailActivity, 3)
        }
    }

    override fun observeData() {
        viewModel?.apply {
            observeReplyBannerUrl(replyPicUrls)
        }
    }

    private fun observeReplyBannerUrl(replyPicUrls: LiveData<List<String>>) {
        replyPicUrls.observe(this) {
            replyBannerRvAdapter.submitList(it)
        }
    }

}