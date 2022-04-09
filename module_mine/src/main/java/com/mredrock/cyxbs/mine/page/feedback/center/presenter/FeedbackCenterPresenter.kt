package com.mredrock.cyxbs.mine.page.feedback.center.presenter

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.appContext
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.center.viewmodel.FeedbackCenterViewModel
import com.mredrock.cyxbs.mine.page.feedback.network.bean.apiServiceNew

/**
 * @Date : 2021/8/23   20:56
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterPresenter :
    BasePresenter<FeedbackCenterViewModel>(), FeedbackCenterContract.IPresenter {
    /**
     * 初始化数据
     */
    override fun fetch() {
        apiServiceNew.getNormalFeedback("1")
            .setSchedulers()
            .doOnSubscribe {}
            .doOnError { }
            .safeSubscribeBy(
                onNext = {
                    it?.let {
                        vm?.setContentList(it.data)
                    }
                },
                onError = {
                    appContext.toast("网络请求失败")
                },
                onComplete = {
                }
            )
    }
}