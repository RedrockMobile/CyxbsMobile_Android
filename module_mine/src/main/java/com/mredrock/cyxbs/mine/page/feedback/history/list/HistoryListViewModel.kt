package com.mredrock.cyxbs.mine.page.feedback.history.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:13
 *@signature 我们不明前路，却已在路上
 */
class HistoryListViewModel : BaseViewModel() {
    companion object {
        private const val TAG = "HistoryListViewModel"
    }

    //历史列表数据
    private val _listData: MutableLiveData<List<History>> = MutableLiveData()
    val listData: LiveData<List<History>> = _listData
    fun setListData(value: List<History>) {
        _listData.value = value
    }

    val hasHistory: LiveData<Boolean> = _listData.map {
        it.isNotEmpty()
    }


}