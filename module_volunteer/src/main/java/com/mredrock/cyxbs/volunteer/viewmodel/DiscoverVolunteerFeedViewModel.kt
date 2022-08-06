package com.mredrock.cyxbs.volunteer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiIllegalStateException
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.network.ApiService
import com.ndhzs.api.store.IStoreService

class DiscoverVolunteerFeedViewModel : BaseViewModel() {
    val volunteerData = MutableLiveData<VolunteerTime?>()
    var isQuerying: Boolean = false
    val loadFailed = SingleLiveEvent<Boolean?>()
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

                    // 登录了就更新积分商城的任务, 后端已做重复处理
                  ServiceManager.getService(IStoreService::class.java)
                    .postTask(IStoreService.Task.LOGIN_VOLUNTEER, "")
                }
    }

    fun unbind() {
        volunteerData.value = null
        loadFailed.value = null
        isBind = false
        requestUnBind = true
    }
}