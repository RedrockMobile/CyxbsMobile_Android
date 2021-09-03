package com.mredrock.cyxbs.mine.page.feedback.history.list

import android.util.Log
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.mine.page.feedback.api
import com.mredrock.cyxbs.mine.page.feedback.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History
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
        /*
         val defaultHistoryList = getDefaultHistoryList()
         //获取状态
         defaultHistoryList.forEachIndexed { index, it ->
             val state = getState(it)
             defaultHistoryList[index].isRead = state
         }
         //对list进行排序
         val sortedList = sortHistoryList(defaultHistoryList)
         vm?.setListData(sortedList)
         */

        api.getHistoryFeedback("1")
            .setSchedulers()
            .map {
                it.data.feedbacks
            }.map { it ->
                it.map {
                    val timePill = DateUtils.strToLong(it.createdAt)
                    it.updatedAt
                    History(it.title,
                        timePill,
                        it.replied,
                        getState(it.replied, it.iD, it.updatedAt),
                        it.iD,
                        it.updatedAt)
                }
            }
            .safeSubscribeBy(onError = {
                Log.e("TAG", "error ${it.message}")
            },
                onComplete = {
                    Log.e("TAG", "complete")
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