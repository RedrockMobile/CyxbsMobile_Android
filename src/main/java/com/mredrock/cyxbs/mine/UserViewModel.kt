package com.mredrock.cyxbs.mine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.QANumber
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper

/**
 * Created by zia on 2018/8/26.
 */
class UserViewModel : BaseViewModel() {


    private val _status = MutableLiveData<ScoreStatus>()//签到状态
    val status: LiveData<ScoreStatus>
        get() = _status

    private val _qaNumber = MutableLiveData<QANumber>()
    val qaNumber: LiveData<QANumber>
        get() = _qaNumber

    private val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")




    fun getScoreStatus() {
        apiService.getScoreStatus()
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy {
                    _status.postValue(it)
                }
                .lifeCycle()
    }

    fun getQANumber() {
        apiService.getQANumber(stuNum, idNum ?: return)
                .normalWrapper(this)
                .safeSubscribeBy {
                    _qaNumber.postValue(it)
                }
                .lifeCycle()

    }



    /**
     * 清除User的信息，唯一会调用这个方法的时候是在用户登出
     */
    fun clearUser() {
        ServiceManager.getService(IAccountService::class.java).getVerifyService().logout(BaseApp.context)
    }
}