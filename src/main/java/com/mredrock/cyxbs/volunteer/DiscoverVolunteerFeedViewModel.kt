package com.mredrock.cyxbs.volunteer

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.network.ApiService

class DiscoverVolunteerFeedViewModel : BaseViewModel() {
    val volunteerData = MutableLiveData<VolunteerTime>()
    fun loadVolunteerTime(uid: String) {
        val apiService = ApiGenerator.getApiService(ApiService::class.java)

        apiService.getVolunteerRecord("Basic enNjeTpyZWRyb2Nrenk=", uid)
                .setSchedulers()
                .safeSubscribeBy(onNext = { volunteerTime: VolunteerTime ->
                    volunteerData.value = volunteerTime
                }, onError = {
                    LogUtils.d("volunteer", it.toString())
                })
    }
}