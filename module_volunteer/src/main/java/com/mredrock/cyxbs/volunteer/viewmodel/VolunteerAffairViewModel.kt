package com.mredrock.cyxbs.volunteer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffair
import com.mredrock.cyxbs.volunteer.network.ApiService

/**
 * Created by yyfbe, Date on 2020/9/5.
 */
class VolunteerAffairViewModel : BaseViewModel() {

    val volunteerAffairs = MutableLiveData<List<VolunteerAffair>>()

    fun getVolunteerAffair() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getVolunteerAffair()
                .setSchedulers()
                .mapOrThrowApiException()
                .unsafeSubscribeBy {
                    volunteerAffairs.value = it
                }
    }
}