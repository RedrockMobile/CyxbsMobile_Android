package com.mredrock.cyxbs.main.viewmodel

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.TokenUser
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.UnsetUserInfoException
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.network.ApiService
import com.umeng.analytics.MobclickAgent
import io.reactivex.rxkotlin.zipWith
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/12.
 */
class LoginViewModel : BaseViewModel() {
    val backToMainOrEditInfoEvent: LiveData<Boolean> = MutableLiveData()

    fun login(stuNum: String?, idNum: String?) {
        if (stuNum?.length ?: 0 < 10) {
            toastEvent.value = R.string.main_activity_login_not_input_account
            return
        } else if (idNum?.length ?: 0 < 6) {
            toastEvent.value = R.string.main_activity_login_not_input_password
            return
        }
        verifyByWeb(stuNum!!, idNum!!)
    }

    fun register(stuNum: String?, idNum: String?, name: String?): Boolean {
        if (stuNum?.length ?: 0 < 10 || idNum?.length ?: 0 < 6) {
            toastEvent.value = R.string.main_activity_login_input_error
            return true
        } else if (name.isNullOrBlank()) {
            toastEvent.value = R.string.main_user_info_input_blank
            return false
        } else {
            ApiGenerator.getApiService(ApiService::class.java)
                    .updateUserInfo(stuNum ?: "", idNum ?: "", name)
                    .checkError()
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        toastEvent.value = R.string.main_activity_login_input_error
                        return@doOnErrorWithDefaultErrorHandler false
                    }
                    .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                    .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                    .safeSubscribeBy {
                        verifyByWeb(stuNum ?: "", idNum ?: "")
                    }
                    .lifeCycle()
            return true
        }
    }

    private fun verifyByWeb(stuNum: String, idNum: String) {
        val g = LoginBody(idNum, stuNum)
        val apiService = ApiGenerator.getApiService(ApiService::class.java)
        //未作校验
        val observableSource = apiService.getPersonInfoByToken(g)
                .map {
                    val tokenBean = it.data
                    check(!tokenBean.token.isNullOrEmpty()) { BaseApp.context.getString(R.string.main_user_info_error) }

                    val lstValues: List<String> = tokenBean.token!!.split(".")
                    Gson().fromJson(String(Base64.decode(lstValues[0], Base64.DEFAULT)), TokenUser::class.java)
                }


        apiService.verify(stuNum, idNum)
                .mapOrThrowApiException()
                .zipWith(observableSource, User.CREATOR::cloneFromTokenUserInfo)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    if (it is UnsetUserInfoException) {
                        (backToMainOrEditInfoEvent as MutableLiveData).value = false
                        return@doOnErrorWithDefaultErrorHandler true
                    } else if (it is IllegalStateException && it.message.equals(BaseApp.context.getString(R.string.main_user_info_error))) {
                        toastEvent.value = R.string.main_user_info_error
                        return@doOnErrorWithDefaultErrorHandler true
                    } else if (it.message.equals(BaseApp.context.getString(R.string.main_user_login_error))) {
                        toastEvent.value = R.string.main_user_pwd_error
                        return@doOnErrorWithDefaultErrorHandler true
                    }
                    return@doOnErrorWithDefaultErrorHandler false
                }
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    (backToMainOrEditInfoEvent as MutableLiveData).value = true
                    BaseApp.user = it
                    MobclickAgent.onProfileSignIn(it.stuNum)
                    EventBus.getDefault().post(LoginStateChangeEvent(true))
                }
                .lifeCycle()
    }
}

data class LoginBody(
        val idNum: String,
        val stuNum: String

)
