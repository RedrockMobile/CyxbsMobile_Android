package com.mredrock.cyxbs.mine.page.feedback.history.detail

import com.mredrock.cyxbs.mine.page.feedback.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.history.detail.bean.Feedback
import com.mredrock.cyxbs.mine.page.feedback.history.detail.bean.Reply

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:12
 *@signature 我们不明前路，却已在路上
 */
class HistoryDetailPresenter : BasePresenter<HistoryDetailViewModel>() {
    override fun fetch() {
        val defaultFeedback = getDefaultFeedback()
        vm?.setFeedback(defaultFeedback)
        val defaultReply = getDefaultReply()
        vm?.setReply(defaultReply)
    }

    private fun getDefaultReply(): Reply {
        return Reply(System.currentTimeMillis(), "你的问题我们已收到，感谢同学你的反馈。", true)
    }

    private fun getDefaultFeedback(): Feedback {
        return Feedback(System.currentTimeMillis(),
            "参与买一送一活动",
            "今天喝了脉动呐，吃了果冻呐，打了电动呐， 还是挡不住对你的心动呐~",
            "账号问题")
    }
}