package com.mredrock.cyxbs.mine

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.QANumber
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.network.model.UserCount
import com.mredrock.cyxbs.mine.network.model.UserUncheckCount
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences


/**
 * Created by zia on 2018/8/26.
 */
class UserViewModel : BaseViewModel() {

    companion object {
        const val UNCHECK_PRAISE_KEY = "mine/uncheck_praise"
        const val UNCHECK_COMMENT_KEY = "mine/uncheck_comment"
    }

    private val _status = MutableLiveData<ScoreStatus>()//签到状态
    val status: LiveData<ScoreStatus>
        get() = _status

    private val _qaNumber = MutableLiveData<QANumber>()
    val qaNumber: LiveData<QANumber>
        get() = _qaNumber

    private val _userCount = MutableLiveData<UserCount>()
    val userCount: LiveData<UserCount>
        get() = _userCount

    private val _userUncheckCount = MutableLiveData<UserUncheckCount>()
    val userUncheckCount: LiveData<UserUncheckCount>
        get() = _userUncheckCount

    fun getScoreStatus() {
        apiService.getScoreStatus()
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { true }
                .safeSubscribeBy(
                        onNext = {
                            _status.postValue(it)
                        }
                )
                .lifeCycle()
    }

    fun getQANumber() {
        apiService.getQANumber()
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            _qaNumber.postValue(it)
                        }
                )
                .lifeCycle()

    }

    //获取用户三大数据的数量
    fun getUserCount() {
        apiService.getUserCount()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { true }
                .safeSubscribeBy(
                        onNext = {
                            if (_userCount.value == null || it.data != _userCount.value)
                                _userCount.postValue(it.data)
                        },
                        onError = {
                            BaseApp.context.toast("请求异常:${it.message}")
                        }
                )
    }

    fun getUserUncheckCount(type: Int) {
        LogUtils.d("RayleighZ", "requestForUncheckCount")
        val sp = BaseApp.context.defaultSharedPreferences
        val lastCheckTimeStamp = if (type == 1) sp.getLong(UNCHECK_COMMENT_KEY, 0L) else sp.getLong(UNCHECK_PRAISE_KEY, 0L)
        apiService.getUncheckCount(
                lastCheckTimeStamp,
                type
        )
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { true }
                .safeSubscribeBy(
                        onNext = {
                                _userUncheckCount.postValue(it.data)
                        },
                        onError = {
                            BaseApp.context.toast("请求异常:${it.message}")
                        }
                )
    }

    fun setLeftMargin(textView: TextView, count: Int) {
        var leftMargin = 17
        if (count > 99) {
            leftMargin += 45
        } else {
            leftMargin += if (count % 10 == 1) {
                12
            } else {
                15
            }
            leftMargin += when (count / 10) {
                1 -> {
                    12
                }
                0 -> 0
                else -> 15
            }
        }
        val lp = textView.layoutParams as ConstraintLayout.LayoutParams
        lp.leftMargin = BaseApp.context.dip(leftMargin)
        textView.layoutParams = lp
    }

    //转换数字为对应字符
    fun getNumber(number: Int): String = when {
        number in 0..99 -> number.toString()
        number > 99 -> "99+"
        else -> "0"
    }

    fun saveCheckTimeStamp(type: Int){
        BaseApp.context.defaultSharedPreferences.editor {
            if (type == 1){//刷新未读回复数的本地记录时间戳
                putLong(UNCHECK_COMMENT_KEY,System.currentTimeMillis()/1000)
            } else if (type == 2){//刷新点赞数的本地记录时间戳
                putLong(UNCHECK_PRAISE_KEY,System.currentTimeMillis()/1000)
            }
            apply()
        }
    }

    /**
     * 清除User的信息，唯一会调用这个方法的时候是在用户登出
     */
    fun clearUser() {
        ServiceManager.getService(IAccountService::class.java).getVerifyService().logout(BaseApp.context)
    }
}