package com.mredrock.cyxbs.main.viewmodel

import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created By jay68 on 2018/8/10.
 */
class SplashViewModel : BaseViewModel() {
    val finishModel = SingleLiveEvent<Boolean>()

    private val accountService : IAccountService by lazy(LazyThreadSafetyMode.NONE) {
        ServiceManager.getService(IAccountService::class.java)
    }

    fun finishAfter(time: Long) {
        AndroidSchedulers.mainThread().scheduleDirect({ finishModel.value = true }, time, TimeUnit.MILLISECONDS)
    }

}