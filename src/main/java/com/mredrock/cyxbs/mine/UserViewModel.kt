package com.mredrock.cyxbs.mine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IUserEditorService
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.service.account.IUserStateService
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.network.model.UserLocal
import com.mredrock.cyxbs.mine.util.apiService

/**
 * Created by zia on 2018/8/26.
 */
class UserViewModel : BaseViewModel() {
    //提示视图Fragment，User的信息已更新
    private val _isUserUpdate = SingleLiveEvent<Boolean>()
    val isUserUpdate: SingleLiveEvent<Boolean>
        get() = _isUserUpdate

    private val _status = MutableLiveData<ScoreStatus>()//签到状态
    val status: LiveData<ScoreStatus>
        get() = _status

    fun getUserInfo() {

        val stuNum = ServiceManager.getService(IUserService::class.java).getStuNum()
        val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
                ?: return
        apiService.getPersonInfo(stuNum, idNum)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(onNext = {
                    //更新BaseUser
                    freshBaseUser(it)
                    _isUserUpdate.postValue(true)

                })
    }


    fun getScoreStatus() {
        val stuNum = ServiceManager.getService(IUserService::class.java).getStuNum()
        val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
                ?: return
        apiService.getScoreStatus(stuNum, idNum)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy {
                    _status.postValue(it)
                }
                .lifeCycle()


    }

    /**
     * 更新BaseApp.user
     */
    private fun freshBaseUser(user: UserLocal) {
        ServiceManager.getService(IUserEditorService::class.java).apply {
            setIntroduction(user.introduction)
            setNickname(user.nickname)
            setQQ(user.qq)
            setPhone(user.phone)
            setAvatarImgUrl(user.photoSrc)
        }

    }

    /**
     * 清除User的信息，唯一会调用这个方法的时候是在用户登出
     */
    fun clearUser() {
        ServiceManager.getService(IUserStateService::class.java).logout(BaseApp.context)
    }
}