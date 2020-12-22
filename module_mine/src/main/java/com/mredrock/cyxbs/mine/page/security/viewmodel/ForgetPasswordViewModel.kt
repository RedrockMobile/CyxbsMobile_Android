package com.mredrock.cyxbs.mine.page.security.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService

/**
 *@Date 2020-11-03
 *@Time 22:11
 *@author SpreadWater
 *@description
 */
class ForgetPasswordViewModel : BaseViewModel() {
    var defaultPassword = MutableLiveData<Boolean>()

    //是否绑定邮箱
    var bindingEmail = MutableLiveData<Boolean>()

    //是否绑定密保
    var bindingPasswordProtect = MutableLiveData<Boolean>()

    //检查是否为默认密码
    fun checkDefaultPassword(stu_num: String, onError: () -> Unit) {
        apiService.checkDefaultPassword(stu_num)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    BaseApp.context.toast(it.toString())
                    onError()
                    true
                }
                .safeSubscribeBy {
                    defaultPassword.value = it.status == 10000
                }
    }

    //检查是否绑定信息
    fun checkBinding(stu_num: String, onSucceed: () -> Unit) {
        apiService.checkBinding(stu_num)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    BaseApp.context.toast(it.toString())
                    true
                }
                .safeSubscribeBy {
                    if (it.status == 10000) {
                        //设置信息绑定的情况
                        bindingEmail.value = it.data.email_is == 1
                        bindingPasswordProtect.value = it.data.question_is == 1
                        onSucceed()
                    } else {
                        BaseApp.context.toast("检查绑定失败")
                    }
                }
    }
}