package com.mredrock.cyxbs.mine.page.feedback.history.list

import android.content.Intent
import android.net.Uri
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.mine.page.feedback.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History
import com.mredrock.cyxbs.mine.page.feedback.utils.change
import com.mredrock.cyxbs.mine.page.feedback.utils.getPointStateSharedPreference
import top.limuyang2.photolibrary.LPhotoHelper

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:13
 *@signature 我们不明前路，却已在路上
 */
class HistoryListPresenter : BasePresenter<HistoryListViewModel>() {
    override fun fetch() {
        val defaultHistoryList = getDefaultHistoryList()
        vm?.setListData(defaultHistoryList)
        defaultHistoryList.forEachIndexed { index, it ->
            val state = getState(it)
            defaultHistoryList[index].isRead = state
        }
        vm?.setListData(defaultHistoryList)
    }

    private fun getDefaultHistoryList(): List<History> {
        return listOf(
            History(
                "参与买一送一活动",
                System.currentTimeMillis(),
                true,
                false,
                1
            ),
            History(
                "无法切换账号",
                System.currentTimeMillis(),
                false,
                true,
                2
            ),
            History(
                "点击电费查询后数据为空",
                System.currentTimeMillis(),
                true,
                true,
                3
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

    fun dealPic(data: Intent?) {
        //获取选择的图片
        val selectImageUris = ArrayList(LPhotoHelper.getSelectedPhotos(data))
        //把图片地址存入vm中
        if (selectImageUris.size != 0) {
            vm?.setUris(selectImageUris)
        }
    }

    fun removePic(i: Int) {
        //防止重复点击删除导致的数组越界
        try {
            val urls = vm?.uris?.value ?: return
            val list: MutableList<Uri> = mutableListOf<Uri>().apply { addAll(urls) }
            list.removeAt(i)
            vm?.setUris(list)
        } catch (e: Exception) {

        }
    }
}