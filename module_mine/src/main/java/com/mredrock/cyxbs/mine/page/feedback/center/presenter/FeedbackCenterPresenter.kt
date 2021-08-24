package com.mredrock.cyxbs.mine.page.feedback.center.presenter

import com.mredrock.cyxbs.mine.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.center.viewmodel.FeedbackCenterViewModel
/**
 * @Date : 2021/8/23   20:56
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterPresenter:
        BasePresenter<FeedbackCenterViewModel>() ,FeedbackCenterContract.IPresenter{
    override fun fetch() {
        vm?.setContentList(listOf(
                "标题",
                "标题",
                "标题",
                "标题",
                "标题",
                "标题"
        ))
    }
}