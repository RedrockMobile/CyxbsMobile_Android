package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.ApiService

class BindEmailViewModel : BaseViewModel() {
    var code = 0
    var expireTime = 0
    val mldConfirmIsSucceed = MutableLiveData<Boolean>()
    val mldCode = MutableLiveData<Int>()

    fun getCode(email: String, onSuccess: () -> Unit) {
        ApiGenerator.getApiService(ApiService::class.java)
                .getEmailCode(email)
                .doOnErrorWithDefaultErrorHandler {
                    BaseApp.appContext.toast(it.toString())
                    true
                }
                .setSchedulers()
                .safeSubscribeBy {
                    when (it.status) {
                        10000 -> {
                            expireTime = it.data.expiredTime
                            mldCode.postValue(code)
                            onSuccess()
                        }
                        10022 -> {
                            toastLong("邮箱格式信息错误")
                        }
                        10009 -> {
                            toastLong("发送验证码邮件次数过多")
                        }
                    }
                }.lifeCycle()
    }

    fun confirmCode(email: String, code: String) {
        ApiGenerator.getApiService(ApiService::class.java)
                .confirmEmailCode(email, code)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    toast(it.toString())
                    true
                }
                .safeSubscribeBy {
                    when (it.status) {
                        10000 -> {
                            mldConfirmIsSucceed.value = true
                        }
                        10007 -> {
                            toastLong("验证码错误")
                        }
                        else -> {
                            toastLong("请求失败")
                        }
                    }
                }.lifeCycle()
    }
}