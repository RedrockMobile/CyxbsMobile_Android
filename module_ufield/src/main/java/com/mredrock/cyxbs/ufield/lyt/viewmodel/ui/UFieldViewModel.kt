package com.mredrock.cyxbs.ufield.lyt.viewmodel.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.base.utils.safeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.lyt.bean.AllActivityBean
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import com.mredrock.cyxbs.ufield.lyt.repository.UFieldActivityRepository

/**
 *  author : lytMoon
 *  date : 2023/8/20 14:32
 *  description :
 *  version ： 1.0
 */
class UFieldViewModel : BaseViewModel() {
    init {
        getAllActivityList()
    }

    /**
     * 全部活动
     */
    private val _allList = MutableLiveData<List<AllActivityBean.ItemAll>>()
    val allList: LiveData<List<AllActivityBean.ItemAll>>
        get() = _allList


    /**
     * 得到所有的数据
     */
    private fun getAllActivityList() {

        UFieldActivityRepository
            .receiveAllData()
            .mapOrInterceptException { Log.d("getAllActivityList", "测试结果-->> ${it.message}");}
            .doOnError { Log.d("getAllActivityList", "测试结果-->> ${it.message}"); }
            .safeSubscribeBy{
                _allList.value= it.ongoing+it.ended
            }



    }
}