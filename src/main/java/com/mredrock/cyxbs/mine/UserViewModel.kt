package com.mredrock.cyxbs.mine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.QANumber
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/8/26.
 */
class UserViewModel : BaseViewModel() {

    private val _user = MutableLiveData<User>()
    val mUser: LiveData<User>
        get() = _user

    private val _status = MutableLiveData<ScoreStatus>()//签到状态
    val status: LiveData<ScoreStatus>
        get() = _status

    private val _qaNumber = MutableLiveData<QANumber>()
    val qaNumber: LiveData<QANumber>
        get() = _qaNumber

    fun getUserInfo() {
        val stuNum = user?.stuNum ?: return
        val idNum = user?.idNum ?: return
        apiService.getPersonInfo(stuNum, idNum)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(onNext = {
                    //更新BaseUser
                    freshBaseUser(it)
                    _user.postValue(it)

                }).lifeCycle()
    }


    fun getScoreStatus(user: User) {
        apiService.getScoreStatus(user.stuNum ?: return, user.idNum ?: return)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy {
                    _status.postValue(it)
                }
                .lifeCycle()
    }

    fun getQANumber() {
        apiService.getQANumber(user?.stuNum ?: return, user?.idNum ?: return)
                .normalWrapper(this)
                .safeSubscribeBy {
                    _qaNumber.postValue(it)
                }
                .lifeCycle()

    }

    /**
     * 更新BaseApp.user
     */
    private fun freshBaseUser(user: User) {
        val finalUser = BaseApp.user ?: return
        finalUser.nickname = user.nickname
        finalUser.introduction = user.introduction
        finalUser.qq = user.qq
        finalUser.phone = user.phone
        finalUser.photoSrc = user.photoSrc
        finalUser.photoThumbnailSrc = user.photoThumbnailSrc
        BaseApp.user = finalUser
    }

    /**
     * 清除User的信息，唯一会调用这个方法的时候是在用户登出
     */
    fun clearUser() {
        _user.postValue(null)
        BaseApp.user = null
    }
}