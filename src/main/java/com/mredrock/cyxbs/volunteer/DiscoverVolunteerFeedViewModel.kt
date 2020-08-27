package com.mredrock.cyxbs.volunteer

import com.mredrock.cyxbs.common.config.DISCOVERY_VOLUNTEER
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.network.ApiService
import java.util.concurrent.TimeUnit

class DiscoverVolunteerFeedViewModel : BaseViewModel() {
    val volunteerData = SingleLiveEvent<VolunteerTime>()
    private val apiService: ApiService by lazy {
        ApiGenerator.getApiService(DISCOVERY_VOLUNTEER, ApiService::class.java)
    }

    fun loadVolunteerTime(uid: String) {
        apiService.getVolunteerRecord("Basic enNjeTpyZWRyb2Nrenk=", uid)
                .setSchedulers()
                .safeSubscribeBy(onNext = { volunteerTime: VolunteerTime ->
                    volunteerData.value = volunteerTime
                }, onError = {
                    LogUtils.d("volunteer", it.toString())
                })
    }
}