package com.mredrock.cyxbs.mine.page.feedback.history.list

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.mine.page.feedback.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History
import com.mredrock.cyxbs.mine.page.feedback.utils.change
import com.mredrock.cyxbs.mine.page.feedback.utils.getPointStateSharedPreference

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:13
 *@signature 我们不明前路，却已在路上
 */
class HistoryListPresenter : BasePresenter<HistoryListViewModel>() {
    override fun fetch() {
        val defaultHistoryList = getDefaultHistoryList()
        //获取状态
        defaultHistoryList.forEachIndexed { index, it ->
            val state = getState(it)
            defaultHistoryList[index].isRead = state
        }
        //对list进行排序
        val sortedList = sortHistoryList(defaultHistoryList)
        vm?.setListData(sortedList)
    }

    private fun sortHistoryList(historyList: List<History>): List<History> {
        var repliedIndex = 0
        var notReplyIndex = 0
        var checkedIndex = 0
        val list: MutableList<History> = mutableListOf()

        val repliedList = historyList.filter { !it.isRead && it.replyOrNot }.sortedByDescending { it.date }
        val checkedList = historyList.filter { it.isRead && it.replyOrNot }.sortedByDescending { it.date }
        val notReplyList = historyList.filter { !it.replyOrNot }.sortedByDescending { it.date }

        list.apply {
            addAll(repliedList)
            addAll(notReplyList)
            addAll(checkedList)
        }

        return list
    }

    private fun getDefaultHistoryList(): List<History> {
        return listOf(
            History(
                "参与买一送一活动",
                System.currentTimeMillis(),
                true,
                isRead = false,
                id = 1
            ),
            History(
                "无法切换账号",
                System.currentTimeMillis(),
                false,
                isRead = false,
                id = 2
            ),
            History(
                "a",
                System.currentTimeMillis(),
                true,
                isRead = false,
                id = 3
            ),
            History(
                "b",
                System.currentTimeMillis(),
                false,
                isRead = false,
                id = 4
            ),
            History(
                "c",
                System.currentTimeMillis(),
                false,
                isRead = false,
                id = 5
            ),
            History(
                "点击电费查询后数据为空",
                System.currentTimeMillis(),
                true,
                isRead = false,
                id = 6
            )
        )
    }

    fun savedState(data: History) {
        if (data.replyOrNot) {
            val pointSP = BaseApp.context.getPointStateSharedPreference()
            pointSP?.change {
                putBoolean(data.id.toString(), true)
            }
        }
    }

    private fun getState(data: History): Boolean {
        return if (data.replyOrNot) {
            val pointSP = BaseApp.context.getPointStateSharedPreference()
            pointSP?.getBoolean(data.id.toString(), false) ?: false
        } else {
            true
        }
    }

}