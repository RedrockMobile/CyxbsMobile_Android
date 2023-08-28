package com.mredrock.cyxbs.ufield.lyt.viewmodel.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.lyt.bean.DoneBean
import com.mredrock.cyxbs.ufield.lyt.repository.CheckRepository


/**
 *  author : lytMoon
 *  date : 2023/8/19 18:19
 *  description :
 *  version ： 1.0
 */
class DoneViewModel : BaseViewModel() {


    private val _doneList = MutableLiveData<List<DoneBean>>()
    val doneList: LiveData<List<DoneBean>>
        get() = _doneList


    init {
        getViewedData()
    }

    /**
     * 初始化加载
     */
     fun getViewedData() {
        CheckRepository
            .receiveDoneData()
            .mapOrInterceptException { Log.d("getViewedData", "测试结果-->> $it"); }
            .doOnError { Log.d("getViewedData", "测试结果-->> $it"); }
            .safeSubscribeBy {
                _doneList.postValue(it)
            }

    }

    /**
     * 加载更多
     */
    fun getViewedUpData(upID: Int) {
        CheckRepository
            .receiveDoneUpData(upID)
            .mapOrInterceptException { Log.d("getViewedUpData", "测试结果-->> $it"); }
            .doOnError { Log.d("getViewedUpData", "测试结果-->> $it"); }
            .safeSubscribeBy {
                _doneList.value = _doneList.value?.plus(it)
            }
    }
}