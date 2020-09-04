package com.mredrock.cyxbs.volunteer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.volunteer.VolunteerLoginActivity
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.config.Config.Authorization
import com.mredrock.cyxbs.volunteer.network.ApiService
import com.mredrock.cyxbs.volunteer.widget.EncryptPassword

/**
 * Created by yyfbe, Date on 2020/9/3.
 */
class VolunteerLoginViewModel : BaseViewModel() {
    var loginCode = MutableLiveData<Int>()
    var volunteerTime = MutableLiveData<VolunteerTime>()
    fun login(account: String, encodingPassword: String, uid: String?, onError: () -> Unit) {
        if (uid.isNullOrEmpty()) return
        ApiGenerator.getApiService(ApiService::class.java)
                .volunteerLogin("Basic enNjeTpyZWRyb2Nrenk=", account, encodingPassword, uid)
                .flatMap {
                    loginCode.postValue(it.code)
                    if (it.code == VolunteerLoginActivity.BIND_SUCCESS) {
                        ApiGenerator.getApiService(ApiService::class.java)
                                .getVolunteerRecord(Authorization, EncryptPassword.encrypt(uid))
                    } else {
                        throw RedrockApiException("response code not correct")
                    }

                }
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = {
                            volunteerTime.value = it
                        },
                        onError = {
                            if (it !is RedrockApiException) {
                                onError()
                            }
                        })
    }
}