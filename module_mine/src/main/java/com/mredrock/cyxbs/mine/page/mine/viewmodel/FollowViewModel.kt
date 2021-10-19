package com.mredrock.cyxbs.mine.page.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
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
class FollowViewModel :BaseViewModel(){
    val followList = MutableLiveData<List<Fan>>()
    val followNetWorkState = MutableLiveData<Int>()
    fun changeFocusStatus(redid: String){
        ApiGenerator.getApiService(ApiService::class.java)
            .changeFocusStatus(redid)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.mine_person_change_focus_status_failed
            }
            .safeSubscribeBy {

            }
    }

    fun getFollows(redid: String){
        ApiGenerator.getApiService(ApiService::class.java)
            .getFollows(redid)
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnSubscribe { followNetWorkState.value = NetworkState.LOADING }
            .doOnError {
                followNetWorkState.value = NetworkState.FAILED
            }
            .safeSubscribeBy {
                followNetWorkState.value = NetworkState.SUCCESSFUL
                followList.value = it
            }
    }
}