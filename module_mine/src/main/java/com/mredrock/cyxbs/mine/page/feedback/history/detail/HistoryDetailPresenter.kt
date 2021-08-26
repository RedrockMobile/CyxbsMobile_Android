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
        //设置默认数据
        setDefaultData()
    }

    /**
     * 设置默认数据
     */
    private fun setDefaultData() {
        val defaultFeedback = getDefaultFeedback()
        vm?.setFeedback(defaultFeedback)
        val defaultReply = getDefaultReply()
        vm?.setReply(defaultReply)
        val defaultBannerPic = getDefaultBannerPic()
        vm?.setReplyPicUrls(defaultBannerPic)
    }

    private fun getDefaultBannerPic(): List<String> {
        return listOf(
            "https://images.unsplash.com/photo-1625730385203-8645ca4e42c3?ixid=MnwxMjA3fDB8MHx0b3BpYy1mZWVkfDEwfGJvOGpRS1RhRTBZfHxlbnwwfHx8fA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
            "https://images.unsplash.com/photo-1628272631915-68f17fee93fd?ixid=MnwxMjA3fDB8MHx0b3BpYy1mZWVkfDd8Ym84alFLVGFFMFl8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
            "https://images.unsplash.com/photo-1628233382517-8b3d1bdc5807?ixid=MnwxMjA3fDB8MHx0b3BpYy1mZWVkfDh8Ym84alFLVGFFMFl8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        )
    }

    private fun getDefaultReply(): Reply {
        return Reply(System.currentTimeMillis(), "你的问题我们已收到，感谢同学你的反馈。", false)
    }

    private fun getDefaultFeedback(): Feedback {
        return Feedback(System.currentTimeMillis(),
            "参与买一送一活动",
            "今天喝了脉动呐，吃了果冻呐，打了电动呐， 还是挡不住对你的心动呐~",
            "账号问题",
            listOf(
                "https://images.unsplash.com/photo-1628087237766-a2129e1ab8c7?ixid=MnwxMjA3fDB8MHx0b3BpYy1mZWVkfDMwfGJvOGpRS1RhRTBZfHxlbnwwfHx8fA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
                "https://images.unsplash.com/photo-1628029799784-50d803e64ea0?ixid=MnwxMjA3fDB8MHx0b3BpYy1mZWVkfDI4fGJvOGpRS1RhRTBZfHxlbnwwfHx8fA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
                "https://images.unsplash.com/photo-1628254747021-59531f59504b?ixid=MnwxMjA3fDB8MHx0b3BpYy1mZWVkfDE2fGJvOGpRS1RhRTBZfHxlbnwwfHx8fA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ))
    }
}