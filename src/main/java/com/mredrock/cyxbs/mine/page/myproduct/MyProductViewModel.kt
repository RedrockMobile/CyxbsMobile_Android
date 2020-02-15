package com.mredrock.cyxbs.mine.page.myproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.ApiGeneratorForSign
import com.mredrock.cyxbs.mine.network.ApiService
import com.mredrock.cyxbs.mine.network.model.MyProduct
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.mine.util.ui.RvFooter

/**
 * Created by roger on 2020/2/15
 */
class MyProductViewModel : BaseViewModel() {
    val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
    val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    private val apiServiceForSign: ApiService by lazy { ApiGeneratorForSign.getApiService(ApiService::class.java) }

    private val pageSize = 6
    private var unclaimedPage: Int = 1
    private var claimedPage: Int = 1

    //未领取的物品
    private val _eventOnUnclaimed = MutableLiveData<RvFooter.State>()
    val eventOnUnClaimed: LiveData<RvFooter.State>
        get() = _eventOnUnclaimed

    private val _unclaimedList = MutableLiveData<MutableList<MyProduct>>()
    val unclaimedList: LiveData<List<MyProduct>>
        get() = Transformations.map(_unclaimedList) {
            it.toList()
        }

    fun loadMyProductUnclaimed() {
        apiServiceForSign.getMyProducts(stuNum, idNum ?: return, unclaimedPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy { list ->
                    if (list.isEmpty()) {
                        _eventOnUnclaimed.postValue(RvFooter.State.NOMORE)
                        return@safeSubscribeBy
                    }
                    val local = _unclaimedList.value ?: mutableListOf()
                    local.addAll(list.filter {
                        it.isReceived == 0
                    })
                    _unclaimedList.postValue(local)
                }.lifeCycle()
    }

    fun cleanUnclaimedPage() {
        onCleared()

        unclaimedPage = 1
        _unclaimedList.value = mutableListOf()
    }

    //已领取的物品
    private val _eventOnClaimed = MutableLiveData<RvFooter.State>()
    val eventOnClaimed: LiveData<RvFooter.State>
        get() = _eventOnClaimed

    private val _claimedList = MutableLiveData<MutableList<MyProduct>>()
    val claimedList: LiveData<List<MyProduct>>
        get() = Transformations.map(_claimedList) {
            it.toList()
        }

    fun loadMyProductClaimed() {
        apiServiceForSign.getMyProducts(stuNum, idNum ?: return, claimedPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy { list ->
                    if (list.isEmpty()) {
                        _eventOnClaimed.postValue(RvFooter.State.NOMORE)
                        return@safeSubscribeBy
                    }
                    val local = _claimedList.value ?: mutableListOf()
                    local.addAll(list.filter {
                        it.isReceived == 1
                    })
                    _claimedList.postValue(local)
                }.lifeCycle()
    }

    fun cleanClaimedPage() {
        onCleared()

        claimedPage = 1
        _claimedList.value = mutableListOf()
    }
}