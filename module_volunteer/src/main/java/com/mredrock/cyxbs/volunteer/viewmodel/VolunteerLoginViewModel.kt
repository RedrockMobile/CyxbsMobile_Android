package com.mredrock.cyxbs.volunteer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.config.StoreTask
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.volunteer.VolunteerLoginActivity
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.network.ApiService

/**
 * Created by yyfbe, Date on 2020/9/3.
 */
class VolunteerLoginViewModel : BaseViewModel() {
    var loginCode = MutableLiveData<Int?>()
    var volunteerTime = MutableLiveData<VolunteerTime>()
    fun login(account: String, encodingPassword: String, onError: () -> Unit) {
        ApiGenerator.getApiService(ApiService::class.java)
                .volunteerLogin(account, encodingPassword)
                .flatMap {
                    loginCode.postValue(it.code)
                    if (it.code == VolunteerLoginActivity.BIND_SUCCESS) {
                        ApiGenerator.getApiService(ApiService::class.java)
                                .getVolunteerRecord()
                    } else {
                        throw RedrockApiException("response code not correct")
                    }

                }
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = {
                            volunteerTime.value = it
                            // 登录了就更新积分商城的任务, 后端已做重复处理
                            StoreTask.postTask(StoreTask.Task.LOGIN_VOLUNTEER, null)
                        },
                        onError = {
                            if (it !is RedrockApiException) {
                                onError()
                            }
                        })
    }
}