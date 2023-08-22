package com.mredrock.cyxbs.ufield.lyt.viewmodel.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.lyt.bean.DoneBean
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
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
        //要实现分页，初始化加载二十个
        getViewedData(10)
    }

    fun getViewedData(upID:Int) {
        Log.d("upID55555", "测试结果-->> ${upID}");
        CheckRepository
            .receiveDoneData(upID)
            .mapOrInterceptException { Log.d("getViewedData", "测试结果-->> $it"); }
            .doOnError { Log.d("getViewedData", "测试结果-->> $it"); }
            .safeSubscribeBy {
                _doneList.postValue(it)
            }


    }
}