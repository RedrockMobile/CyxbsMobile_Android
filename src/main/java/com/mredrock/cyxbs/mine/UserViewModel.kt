package com.mredrock.cyxbs.mine

import android.arch.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/8/26.
 */
class UserViewModel : BaseViewModel() {

    val mUser = MutableLiveData<User>()

    fun getUserInfo() {
        apiService.getPersonInfo(user!!.stuNum!!, user!!.idNum!!)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy { mUser.value = it }
    }
}