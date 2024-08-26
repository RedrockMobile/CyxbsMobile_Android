package com.mredrock.cyxbs.ufield.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.bean.IsAdminBean
import com.mredrock.cyxbs.ufield.bean.ItemActivityBean
import com.mredrock.cyxbs.ufield.repository.UFieldRepository

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


    fun getAllActivityList() {
        UFieldRepository
            .receiveAllData()
            .mapOrInterceptException {}
            .doOnError {}
            .safeSubscribeBy { _allList.value = it.ongoing.plus(it.ended) }
    }


    fun getCultureActivityList() {
        UFieldRepository
            .receiveCultureData()
            .mapOrInterceptException {}
            .doOnError {}
            .safeSubscribeBy { _cultureList.value = it.ongoing.plus(it.ended) }
    }

    fun getSportsActivityList() {
        UFieldRepository
            .receiveSportsData()
            .mapOrInterceptException {}
            .doOnError {}
            .safeSubscribeBy { _sportsList.value = it.ongoing.plus(it.ended) }
    }


    fun getEducationActivityList() {
        UFieldRepository
            .receiveEductionData()
            .mapOrInterceptException {}
            .doOnError {}
            .safeSubscribeBy { _educationList.value = it.ongoing.plus(it.ended) }
    }


    fun getIsAdmin() {
        UFieldRepository
            .receiveIsAdmin()
            .mapOrInterceptException {}
            .doOnError {}
            .safeSubscribeBy { _isAdmin.postValue(listOf(it)) }
    }
}