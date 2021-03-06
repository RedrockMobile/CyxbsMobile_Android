package com.mredrock.cyxbs.volunteer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiIllegalStateException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.network.ApiService

class DiscoverVolunteerFeedViewModel : BaseViewModel() {
    val volunteerData = MutableLiveData<VolunteerTime>()
    var isQuerying: Boolean = false
    val loadFailed = SingleLiveEvent<Boolean>()
    var isBind = false

    //是否用户主动退出
    var requestUnBind = false

    fun loadVolunteerTime() {
        isQuerying = true
        requestUnBind = false
        ApiGenerator.getApiService(ApiService::class.java).judgeBind()
                .flatMap {
                    if (it.code != 0) {
                        throw RedrockApiIllegalStateException()
                    }
                    ApiGenerator.getApiService(ApiService::class.java).getVolunteerRecord()
                }
                .setSchedulers()
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