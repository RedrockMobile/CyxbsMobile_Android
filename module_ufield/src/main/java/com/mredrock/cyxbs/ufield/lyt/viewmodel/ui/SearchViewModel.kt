package com.mredrock.cyxbs.ufield.lyt.viewmodel.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.base.utils.safeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.lyt.bean.ItemActivityBean
import com.mredrock.cyxbs.ufield.lyt.repository.SearchRepository

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

    /**
     * 搜索全部活动
     */
    private fun searchAll(keyword: String) {
        SearchRepository
            .receiveSearchData("all", 50, "create_timestamp", keyword)
            .mapOrInterceptException {
                Log.d("searchAll", "测试结果-->>$it ");
                toast("网络似乎有点问题~")
            }
            .doOnError { Log.d("searchAll", "测试结果-->> $it"); }
            .safeSubscribeBy {
                if (it.isEmpty()) {
                    toast("搜索为空，请检查搜索内容")
                } else {
                    _allSearchList.postValue(it)

                }
            }
    }

    /**
     * 搜索文娱活动
     */
    private fun searchCulture(keyword: String) {
        SearchRepository
            .receiveSearchData("culture", 50, "create_timestamp", keyword)
            .mapOrInterceptException { Log.d("searchCulture", "测试结果-->>$it "); }
            .doOnError { Log.d("searchCulture", "测试结果-->> $it"); }
            .safeSubscribeBy {
                _cultureSearchList.postValue(it)
            }
    }

    /**
     * 搜索体育活动
     */
    private fun searchSports(keyword: String) {
        SearchRepository
            .receiveSearchData("sports", 50, "create_timestamp", keyword)
            .mapOrInterceptException { Log.d("searchSports", "测试结果-->>$it "); }
            .doOnError { Log.d("searchSports", "测试结果-->> $it"); }
            .safeSubscribeBy {
                _sportsSearchList.postValue(it)
            }
    }

    /**
     * 搜索教育活动
     */
    private fun searchEducation(keyword: String) {
        SearchRepository
            .receiveSearchData("education", 50, "create_timestamp", keyword)
            .mapOrInterceptException { Log.d("searchEducation", "测试结果-->>$it "); }
            .doOnError { Log.d("searchEducation", "测试结果-->> $it"); }
            .safeSubscribeBy {
                _educationSearchList.postValue(it)
            }


    }


}