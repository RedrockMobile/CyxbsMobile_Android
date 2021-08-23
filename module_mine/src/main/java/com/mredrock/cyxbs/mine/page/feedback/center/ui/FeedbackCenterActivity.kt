package com.mredrock.cyxbs.mine.page.feedback.center.ui

import com.mredrock.cyxbs.common.ui.BaseMVPVMActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackCenterBinding
import com.mredrock.cyxbs.mine.page.feedback.center.presenter.FeedbackCenterPresenter
import com.mredrock.cyxbs.mine.page.feedback.center.viewmodel.FeedbackCenterViewModel

/**
 * @Date : 2021/8/23   20:51
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterActivity :
        BaseMVPVMActivity<FeedbackCenterViewModel, MineActivityFeedbackCenterBinding, FeedbackCenterPresenter>() {
    override fun createPresenter(): FeedbackCenterPresenter {
        return FeedbackCenterPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.mine_activity_feedback_center
    }
}