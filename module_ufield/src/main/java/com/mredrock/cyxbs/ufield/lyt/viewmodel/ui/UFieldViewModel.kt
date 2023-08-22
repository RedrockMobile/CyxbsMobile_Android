package com.mredrock.cyxbs.ufield.lyt.viewmodel.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.lyt.bean.IsAdminBean
import com.mredrock.cyxbs.ufield.lyt.bean.ItemActivityBean
import com.mredrock.cyxbs.ufield.lyt.repository.UFieldRepository

/**
 *  author : lytMoon
 *  date : 2023/8/20 14:32
 *  description :
 *  version ： 1.0
 */
class UFieldViewModel : BaseViewModel() {
    init {
        getAllActivityList()
        getCultureActivityList()
        getSportsActivityList()
        getEducationActivityList()
        Log.d("init520", "测试结果-->> ");
    }

    /**
     * 分别是 全部活动 文娱活动 体育活动 体育活动 教育活动 的livedata
     */
    private val _allList = MutableLiveData<List<ItemActivityBean.ItemAll>>()
    val allList: LiveData<List<ItemActivityBean.ItemAll>>
        get() = _allList

    private val _cultureList = MutableLiveData<List<ItemActivityBean.ItemAll>>()
    val cultureList: LiveData<List<ItemActivityBean.ItemAll>>
        get() = _cultureList

    private val _sportsList = MutableLiveData<List<ItemActivityBean.ItemAll>>()
    val sportsList: LiveData<List<ItemActivityBean.ItemAll>>
        get() = _sportsList

    private val _educationList = MutableLiveData<List<ItemActivityBean.ItemAll>>()
    val educationList: LiveData<List<ItemActivityBean.ItemAll>>
        get() = _educationList

    private val _isAdmin = MutableLiveData<List<IsAdminBean>>()
    val isAdmin: LiveData<List<IsAdminBean>>
        get() = _isAdmin

    /**
     * 得到所有的数据
     */
    fun getAllActivityList() {

        UFieldRepository
            .receiveAllData()
            .mapOrInterceptException { Log.d("getAllActivityList", "测试结果-->> ${it.message}"); }
            .doOnError { Log.d("getAllActivityList", "测试结果-->> ${it.message}"); }
            .safeSubscribeBy {
                _allList.value = it.ongoing + it.ended
            }
    }

    /**
     * 得到文娱活动数据
     */
    fun getCultureActivityList() {
        UFieldRepository
            .receiveCultureData()
            .mapOrInterceptException {
                Log.d(
                    "getCultureActivityList",
                    "测试结果-->> ${it.message}"
                );
            }
            .doOnError { Log.d("getCultureActivityList", "测试结果-->> ${it.message}"); }
            .safeSubscribeBy {
                _cultureList.value = it.ongoing + it.ended
                Log.d("8552", "测试结果-->> $it")
            }
    }


    /**
     * 得到体育活动数据
     */
    fun getSportsActivityList() {
        UFieldRepository
            .receiveSportsData()
            .mapOrInterceptException {
                Log.d(
                    "getSportsActivityList",
                    "测试结果-->> ${it.message}"
                );
            }
            .doOnError { Log.d("getSportsActivityList", "测试结果-->> ${it.message}"); }
            .safeSubscribeBy {
                _sportsList.value = it.ongoing + it.ended
            }
    }


    /**
     * 得到教育活动数据
     */
    fun getEducationActivityList() {
        UFieldRepository
            .receiveEductionData()
            .mapOrInterceptException {
                Log.d(
                    "getEducationActivityList",
                    "测试结果-->> ${it.message}"
                );
            }
            .doOnError { Log.d("getEducationActivityList", "测试结果-->> ${it.message}"); }
            .safeSubscribeBy {
                _educationList.value = it.ongoing + it.ended
            }
    }

    /**
     * 查看是否是管理员
     */

    fun getIsAdmin(){
        UFieldRepository
            .receiveIsAdmin()
            .mapOrInterceptException { Log.d("getIsAdmin", "测试结果-->> ${it.message}");}
            .doOnError { Log.d("getIsAdmin", "测试结果-->> ${it.message}"); }
            .safeSubscribeBy{
                _isAdmin.postValue(listOf(it))
            }
    }



}