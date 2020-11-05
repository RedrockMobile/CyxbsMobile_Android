package com.mredrock.cyxbs.mine.page.security.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService

/**
 *@Date 2020-11-03
 *@Time 22:11
 *@author SpreadWater
 *@description
 */
class ForgetPasswordViewModel :BaseViewModel() {
    var defaultPassword=MutableLiveData<Boolean>()

    //是否绑定邮箱
    var bindingEmail = MutableLiveData<Boolean>()

    //是否绑定密保
    var bindingPasswordProtect = MutableLiveData<Boolean>()

    //检查是否为默认密码
    fun checkDefaultPassword(stu_num: String) {
        apiService.checkDefaultPassword(stu_num)
                .safeSubscribeBy(onNext = {
                    if (it.status == 10000) {
                        defaultPassword.value = true
                        Log.d("zt", "原密码正确")
                    } else {
                        defaultPassword.value = false
                        Log.d("zt", "原密码错误")
                    }
                }, onError = {
                    Log.d("zt", "检查原密码出现错误")
                })
    }
    //检查是否绑定信息
    fun checkBinding(stu_num: String) {
        apiService.checkBinding(stu_num)
                .safeSubscribeBy(onNext = {
                    if (it.status == 10000) {
                        //设置信息绑定的情况
                        bindingEmail.value = it.data.email_is == 1
                        bindingPasswordProtect.value = it.data.question_is == 1
                    } else {
                        Log.d("zt", "检查绑定失败")
                    }
                }, onError = {
                    Log.d("zt", "检查绑定失败！")
                })
    }
}