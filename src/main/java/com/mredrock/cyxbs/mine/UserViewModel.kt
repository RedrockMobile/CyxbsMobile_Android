package com.mredrock.cyxbs.mine

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/8/26.
 */
class UserViewModel : BaseViewModel() {

    val mUser = MutableLiveData<User>()
    val status = MutableLiveData<ScoreStatus>()//签到状态


    fun getUserInfo() {
        apiService.getPersonInfo(user!!.stuNum!!, user!!.idNum!!)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy { mUser.value = it }
    }


    fun getScoreStatus(user: User) {
        apiService.getScoreStatus(user.stuNum ?: return, user.idNum ?: return)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy {
                    status.postValue(it)
                }
                .lifeCycle()


    }
}