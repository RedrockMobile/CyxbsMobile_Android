package com.mredrock.cyxbs.mine.page.feedback.history.list

import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.page.feedback.api
import com.mredrock.cyxbs.mine.page.feedback.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History
import com.mredrock.cyxbs.mine.page.feedback.network.bean.HistoryFeedback
import com.mredrock.cyxbs.mine.page.feedback.utils.DateUtils
import com.mredrock.cyxbs.mine.page.feedback.utils.change
import com.mredrock.cyxbs.mine.page.feedback.utils.getPointStateSharedPreference

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:13
 *@signature 我们不明前路，却已在路上
 */
class HistoryListPresenter : BasePresenter<HistoryListViewModel>() {
    override fun fetch() {

        val list = api.getHistoryFeedback("1")
            .setSchedulers()
            .map {
                it.data?.feedbacks ?: listOf()
            }
            .map { it ->
                it.map {
                    val timePill = DateUtils.strToLong(it.createdAt)
                    History(
                        it.title,
                        timePill,
                        it.replied,
                        getState(it.replied, it.iD, it.updatedAt),
                        it.iD,
                        it.updatedAt
                    )
                }
            }
            .switchIfEmpty {
                emptyList<HistoryFeedback.Data.Feedback>()
            }
            .safeSubscribeBy(onError = {
                BaseApp.context.toast("出问题啦~ ${it.message}")
            },
                onComplete = {
                }, onNext = {
                    val sortHistoryList = sortHistoryList(it)
                    vm?.setListData(sortHistoryList)
                })

    }

    private fun sortHistoryList(historyList: List<History>): List<History> {
        val list: MutableList<History> = mutableListOf()

        val repliedList =
            historyList.filter { !it.isRead && it.replyOrNot }.sortedByDescending { it.date }
        val checkedList =
            historyList.filter { it.isRead && it.replyOrNot }.sortedByDescending { it.date }
        val notReplyList = historyList.filter { !it.replyOrNot }.sortedByDescending { it.date }

        list.apply {
            addAll(repliedList)
            addAll(notReplyList)
            addAll(checkedList)
        }

        return list
    }

    //默认数据获取
    @Deprecated("接口都有了还自己模拟数据？？")
    private fun getDefaultHistoryList(): List<History> {
        return listOf(
            History(
                "参与买一送一活动",
                System.currentTimeMillis(),
                true,
                isRead = false,
                id = 1,
                updateTime = ""
            ),
            History(
                "无法切换账号",
                System.currentTimeMillis(),
                false,
                isRead = false,
                id = 2,
                updateTime = ""
            ),
            History(
                "a",
                System.currentTimeMillis(),
                true,
                isRead = false,
                id = 3,
                updateTime = ""
            ),
            History(
                "b",
                System.currentTimeMillis(),
                false,
                isRead = false,
                id = 4,
                updateTime = ""
            ),
            History(
                "c",
                System.currentTimeMillis(),
                false,
                isRead = false,
                id = 5,
                updateTime = ""
            ),
            History(
                "点击电费查询后数据为空",
                System.currentTimeMillis(),
                true,
                isRead = false,
                id = 6,
                updateTime = ""
            )
        )
    }

    //数据保存工具类
    fun savedState(data: History) {
        if (data.replyOrNot) {
            val pointSP = BaseApp.context.getPointStateSharedPreference()
            pointSP?.change {
                putString(data.id.toString(), data.updateTime)
            }
        }
    }

    private fun getState(replyOrNot: Boolean, id: Long, updateTime: String): Boolean {
        return if (replyOrNot) {
            val pointSP = BaseApp.context.getPointStateSharedPreference()
            pointSP?.getString(id.toString(), "") == updateTime
        } else {
            true
        }
    }

}