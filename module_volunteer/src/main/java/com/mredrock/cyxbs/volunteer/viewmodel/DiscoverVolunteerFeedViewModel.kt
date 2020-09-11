package com.mredrock.cyxbs.volunteer.viewmodel

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiIllegalStateException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.config.Config.Authorization
import com.mredrock.cyxbs.volunteer.network.ApiService

class DiscoverVolunteerFeedViewModel : BaseViewModel() {
    val volunteerData = SingleLiveEvent<VolunteerTime>()
    var isQuerying: Boolean = false
    val loadFailed = SingleLiveEvent<Boolean>()
    var isBind = false

    //是否用户主动退出
    var requestUnBind = false
    private val apiService: ApiService by lazy {
        ApiGenerator.getApiService(ApiService::class.java)
    }

//    fun judgeBind(uid: String) {
//        apiService.judgeBind(uid)
//                .setSchedulers()
//                .doOnError {
//                    isBind.value = false
//                }
//                .safeSubscribeBy {
//                    isBind.value = it.code == 0
//                }
//    }

    fun loadVolunteerTime(uid: String) {
        isQuerying = true
        apiService.judgeBind(uid)
                .setSchedulers()
                .flatMap {
                    if (it.code != 0) {
                        throw RedrockApiIllegalStateException()
                    }
                    apiService.getVolunteerRecord(Authorization, uid)
                }
                .doOnError {
                    loadFailed.value = true
                    isQuerying = false
                }
                .safeSubscribeBy {
                    isBind = true
                    volunteerData.value = it
                    isQuerying = false
                }
    }

    fun unbind() {
        volunteerData.value = null
        loadFailed.value = null
        isBind = false
        requestUnBind = true
    }
}