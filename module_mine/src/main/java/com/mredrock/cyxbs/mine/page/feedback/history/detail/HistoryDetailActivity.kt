package com.mredrock.cyxbs.mine.page.feedback.history.detail

import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHistoryDetailBinding
import com.mredrock.cyxbs.mine.page.feedback.base.ui.BaseMVPVMActivity

class HistoryDetailActivity :
    BaseMVPVMActivity<HistoryDetailViewModel, MineActivityHistoryDetailBinding, HistoryDetailPresenter>() {

    override fun createPresenter(): HistoryDetailPresenter = HistoryDetailPresenter()

    override fun getLayoutId(): Int = R.layout.mine_activity_history_detail

    override fun initView() {
        binding?.apply {
            vm = viewModel
        }
    }

}