package com.mredrock.cyxbs.mine.page.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.ApiService
import com.mredrock.cyxbs.mine.network.NetworkState
import com.mredrock.cyxbs.mine.network.model.Fan

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FanViewModel : BaseViewModel() {
    val fanList = MutableLiveData<List<Fan>>()
    val fanNetWorkState = MutableLiveData<Int>()
    fun changeFocusStatus(redid: String){
        ApiGenerator.getApiService(ApiService::class.java)
            .changeFocusStatus(redid)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.mine_person_change_focus_status_failed
            }
            .unsafeSubscribeBy {
            }
    }

    fun getFans(redid: String){
        ApiGenerator.getApiService(ApiService::class.java)
            .getFans(redid)
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnSubscribe {
                fanNetWorkState.value = NetworkState.LOADING
            }
            .doOnError {
                fanNetWorkState.value = NetworkState.FAILED
            }
            .unsafeSubscribeBy {
                fanNetWorkState.value = NetworkState.SUCCESSFUL
                fanList.value = it
            }
    }
}