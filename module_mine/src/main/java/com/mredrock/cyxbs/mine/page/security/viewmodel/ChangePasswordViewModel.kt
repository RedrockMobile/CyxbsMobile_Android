package com.mredrock.cyxbs.mine.page.security.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService

/**
 * Author: RayleighZ
 * Time: 2020-10-31 1:33
 */
class ChangePasswordViewModel : BaseViewModel() {

    //旧密码是否输入正确
    var originPassWordIsCorrect = MutableLiveData<Boolean>()

    //新密码上传是否成功
    var inputNewPasswordCorrect = MutableLiveData<Boolean>()

    //是否绑定邮箱
    var bindingEmail = MutableLiveData<Boolean>()

    //是否绑定密保
    var bindingPasswordProtect = MutableLiveData<Boolean>()

    //是否为默认密码
    var isDefaultPassword = MutableLiveData<Boolean>()

    //检查旧密码输入是否相同
    fun originPassWordCheck(originPassword: String) {
        apiService.originPassWordCheck(originPassword)
                .safeSubscribeBy(onNext = {
                    if (it.status == 10000) {
                        originPassWordIsCorrect.value = true
                        Log.d("zt", "3")
                        Log.d("zt", "旧密码输入正确")
                    } else {
                        originPassWordIsCorrect.value = false
                        Log.d("zt", "4")
                        context.toast("旧密码输入错误")
                    }
                }, onError = {
                    Log.d("zt", "5")
                    context.toast("对不起，目前无法修改密码")
                })
    }

    //进行新密码的传入
    fun newPassWordInput(origin_password: String, new_password: String) {
        apiService.changePassword(origin_password, new_password)
                .safeSubscribeBy(onNext = {
                    when (it.status) {
                        10000 -> {
                            //TODO这里进行toast提示
                            inputNewPasswordCorrect.value = true
                            context.toast("修改密码成功！")
                        }
                        10002 -> {
                            inputNewPasswordCorrect.value = false
                            context.toast("原密码错误！")
                        }
                        10004 -> {
                            inputNewPasswordCorrect.value = false
                            context.toast("密码格式有问题！")
                        }
                        10020 -> {
                            inputNewPasswordCorrect.value = false
                            context.toast("新旧密码重复！")
                        }
                    }
                }, onError = {
                    context.toast("对不起，目前无法修改密码")
                })
    }

    //检查是否绑定信息
    fun checkBinding(stu_num: String) {
        apiService.checkBinding(stu_num)
                .safeSubscribeBy(onNext = {
                    if (it.status == 10000) {
                        //设置信息绑定的情况
                        bindingEmail.value = if (it.data.email_is == 1) true else false
                        bindingPasswordProtect.value = if (it.data.question_is == 1) true else false
                    } else {
                        Log.d("zt", "检查绑定失败")
                    }
                }, onError = {
                    Log.d("zt", "检查绑定失败！")
                })
    }

    //检查是否为默认密码
    fun checkDefaultPassword(stu_num: String) {
        apiService.checkDefaultPassword(stu_num)
                .safeSubscribeBy(onNext = {
                    if (it.status == 10000) {
                        isDefaultPassword.value = true
                        Log.d("zt", "原密码正确")
                    } else {
                        isDefaultPassword.value = false
                        Log.d("zt", "原密码错误")
                    }
                }, onError = {
                    Log.d("zt", "检查原密码出现错误")
                })
    }

}