package com.mredrock.cyxbs.ufield.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.bean.ItemActivityBean
import com.mredrock.cyxbs.ufield.repository.SearchRepository

/**
 *  description :负责搜索模块的viewModel
 *  author : lytMoon
 *  date : 2023/8/22 11:10
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
class SearchViewModel : BaseViewModel() {


    private val _allSearchList = MutableLiveData<List<ItemActivityBean.ItemAll>>()
    val allSearchList: LiveData<List<ItemActivityBean.ItemAll>>
        get() = _allSearchList

    private val _cultureSearchList = MutableLiveData<List<ItemActivityBean.ItemAll>>()
    val cultureSearchList: LiveData<List<ItemActivityBean.ItemAll>>
        get() = _cultureSearchList

    private val _sportsSearchList = MutableLiveData<List<ItemActivityBean.ItemAll>>()
    val sportsSearchList: LiveData<List<ItemActivityBean.ItemAll>>
        get() = _sportsSearchList

    private val _educationSearchList = MutableLiveData<List<ItemActivityBean.ItemAll>>()
    val educationSearchList: LiveData<List<ItemActivityBean.ItemAll>>
        get() = _educationSearchList


    fun iniSearchList(keyword: String) {
        searchAll(keyword)
        searchCulture(keyword)
        searchSports(keyword)
        searchEducation(keyword)
    }


    private fun searchAll(keyword: String) {
        SearchRepository
            .receiveSearchData("all", 50, "start_timestamp_but_ongoing_first", keyword)
            .mapOrInterceptException {}
            .doOnError {}
            .safeSubscribeBy {
                if (it.isEmpty()) {
                    toast("暂无更多内容~")
                } else {
                    _allSearchList.postValue(it)
                }
            }
    }


    private fun searchCulture(keyword: String) {
        SearchRepository
            .receiveSearchData("culture", 50, "start_timestamp_but_ongoing_first", keyword)
            .mapOrInterceptException { }
            .doOnError { }
            .safeSubscribeBy { _cultureSearchList.postValue(it) }
    }


    private fun searchSports(keyword: String) {
        SearchRepository
            .receiveSearchData("sports", 50, "start_timestamp_but_ongoing_first", keyword)
            .mapOrInterceptException { }
            .doOnError { }
            .safeSubscribeBy { _sportsSearchList.postValue(it) }
    }

    private fun searchEducation(keyword: String) {
        SearchRepository
            .receiveSearchData("education", 50, "start_timestamp_but_ongoing_first", keyword)
            .mapOrInterceptException { }
            .doOnError {}
            .safeSubscribeBy { _educationSearchList.postValue(it) }


    }


}