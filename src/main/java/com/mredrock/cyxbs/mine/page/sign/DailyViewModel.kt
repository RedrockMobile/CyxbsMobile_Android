package com.mredrock.cyxbs.mine.page.sign

import android.arch.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.CheckInStatus
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.normalWrapper

/**
 * Created by zia on 2018/8/22.
 * 需要确保用户已登录
 */
class DailyViewModel : BaseViewModel() {

    val account = MutableLiveData<Int>()//账户积分
    val status = MutableLiveData<CheckInStatus>()//签到状态

    fun loadAllData(user: User) {
        account.value = user.integral
        apiService.getCheckInStatus(user.stuNum!!, user.idNum!!)
                .normalWrapper(this)
                .safeSubscribeBy {
                    status.postValue(it)
                }
                .lifeCycle()
    }

    fun checkIn(user: User, action: () -> Unit) {
        apiService.checkIn(user.stuNum!!, user.idNum!!)
                .setSchedulers()
                .subscribe { action.invoke() }
                .lifeCycle()
    }
}
