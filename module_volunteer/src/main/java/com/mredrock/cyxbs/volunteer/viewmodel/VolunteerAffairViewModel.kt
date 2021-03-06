package com.mredrock.cyxbs.volunteer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffair
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffairDetail
import com.mredrock.cyxbs.volunteer.network.ApiService

/**
 * Created by yyfbe, Date on 2020/9/5.
 */
class VolunteerAffairViewModel : BaseViewModel() {

    val volunteerAffairs = MutableLiveData<List<VolunteerAffair>>()

    val volunteerAffairDetail = MutableLiveData<VolunteerAffairDetail>()
    fun getVolunteerAffair() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getVolunteerAffair()
                .setSchedulers()
                .mapOrThrowApiException()
                .safeSubscribeBy {
                    volunteerAffairs.value = it
                }
    }

    fun getVolunteerAffairDetail(id: Int) {
        ApiGenerator.getApiService(ApiService::class.java)
                .getVolunteerAffairDetail(id)
                .setSchedulers()
                .mapOrThrowApiException()
                .safeSubscribeBy {
                    volunteerAffairDetail.value = it
                }
    }
}