package com.mredrock.cyxbs.discover.electricity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.discover.electricity.bean.ElecInf
import com.mredrock.cyxbs.discover.electricity.network.ApiService

/**
 * Author: Hosigus
 * Date: 2018/9/28 19:30
 * Description: com.mredrock.cyxbs.electricity.viewmodel
 */
class ChargeViewModel : BaseViewModel() {
    val chargeInfo: LiveData<ElecInf> = MutableLiveData()
    val loadFailed = SingleLiveEvent<Boolean>()
    private val service: ApiService by lazy {
        ApiGenerator.getApiService(ApiService::class.java)
    }

    fun getCharge(building: String, room: String) {
        service.getElectricityInfo(building, room)
                .map {
                    if (it.isSuccessful) {
                        it.elecInf
                    } else {
                        throw RedrockApiException(it.info, it.status)
                    }
                }
                .setSchedulers()
                .safeSubscribeBy {
                    (chargeInfo as MutableLiveData).value = it
                }.lifeCycle()
    }

    fun preGetCharge() {
        service.getElectricityInfo()
                .map {
                    if (it.isSuccessful) {
                        it.elecInf
                    } else {
                        throw RedrockApiException(it.info, it.status)
                    }
                }
                .setSchedulers()
                .doOnError {
                    loadFailed.value = true
                }
                .safeSubscribeBy {
                    if (it == null) loadFailed.value = true
                    (chargeInfo as MutableLiveData).value = it
                }
                .lifeCycle()
    }

}