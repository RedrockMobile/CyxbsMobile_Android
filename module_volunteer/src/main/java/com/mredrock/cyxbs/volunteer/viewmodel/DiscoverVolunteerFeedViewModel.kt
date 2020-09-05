package com.mredrock.cyxbs.volunteer.viewmodel

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
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
    private val apiService: ApiService by lazy {
        ApiGenerator.getApiService(ApiService::class.java)
    }

    fun loadVolunteerTime(uid: String) {
        isQuerying = true
        apiService.getVolunteerRecord(Authorization, uid)
                .setSchedulers()
                .safeSubscribeBy(onNext = { volunteerTime: VolunteerTime ->
                    volunteerData.value = volunteerTime
                    isQuerying = false
                }, onError = {
                    LogUtils.d("volunteer", it.toString())
                    loadFailed.value = true
                    isQuerying = false
                })
    }
}