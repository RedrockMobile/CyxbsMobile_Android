package com.mredrock.cyxbs.mine.page.security.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.StuNumBody
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.logr

/**
 * Author: SpreadWater
 * Time: 2020-10-31 1:33
 */
class ChangePasswordViewModel : BaseViewModel() {

    //旧密码是否输入正确
    var originPassWordIsCorrect = MutableLiveData<Boolean>()

    //新密码上传是否成功
    var inputNewPasswordCorrect = MutableLiveData<Boolean>()

    //新密码上传格式是否正确
    var inputNewPasswordFormat = MutableLiveData<Int>()

    //是否绑定邮箱
    var bindingEmail = MutableLiveData<Boolean>()

    //是否绑定密保
    var bindingPasswordProtect = MutableLiveData<Boolean>()

    //是否为默认密码
    var isDefaultPassword = MutableLiveData<Boolean>()

    //检查旧密码输入是否相同
    fun originPassWordCheck(originPassword: String) {
        apiService.originPassWordCheck(originPassword)
                .setSchedulers()
                .safeSubscribeBy(onNext = {
                    if (it.status == 10000) {
                        originPassWordIsCorrect.value = true
                    } else {
                        originPassWordIsCorrect.value = false
                    }
                }, onError = {
                    context.toast("对不起，目前无法修改密码")
                })
    }

    //进行新密码的传入
    fun newPassWordInput(origin_password: String, new_password: String) {
        apiService.resetPassword(origin_password, new_password)
                .setSchedulers()
                .safeSubscribeBy(onNext = {
                    when (it.status) {
                        10000 -> {
                            inputNewPasswordCorrect.value = true
                            inputNewPasswordFormat.value = 10000
                            context.toast("修改密码成功！")
                        }
                        10002 -> {
                            inputNewPasswordCorrect.value = false
                            inputNewPasswordFormat.value = 10002
                            context.toast("原密码错误！")
                        }
                        10004 -> {
                            inputNewPasswordCorrect.value = false
                            inputNewPasswordFormat.value = 10004
                            context.toast("密码格式有问题！")
                        }
                        10020 -> {
                            inputNewPasswordCorrect.value = false
                            inputNewPasswordFormat.value = 10020
                            context.toast("新旧密码重复！")
                        }
                    }
                }, onError = {
                    context.toast("对不起，目前无法修改密码")
                })
    }

    //在未登录的条件下修改密码（需要随机数）
    fun resetPasswordFromLogin(stu_num: String, new_password: String, code: Int) {
        LogUtils.d("RayleighZ", "")
        apiService.resetPasswordFromLogin(stu_num, new_password, code)
                .setSchedulers()
                .safeSubscribeBy(onNext = {
                    when (it.status) {
                        10000 -> {
                            inputNewPasswordCorrect.value = true
                            context.toast("修改密码成功！")
                        }
                        10003 -> {
                            inputNewPasswordCorrect.value = false
                            context.toast("后端返回的认证码存在问题，修改失败")
                        }
                        10004 -> {
                            inputNewPasswordCorrect.value = false
                            context.toast("密码格式有问题！")
                        }
                    }
                }, onError = {
                    context.toast("对不起，目前无法修改密码")
                    context.toast(it.toString())
                })
    }

    //检查是否绑定信息
    fun checkBinding(stu_num: String, onSuccess: () -> Unit) {
        apiService.checkBinding(stu_num)
                .setSchedulers()
                .safeSubscribeBy(onNext = {
                    if (it.status == 10000) {
                        //设置信息绑定的情况
                        bindingEmail.value = it.data.email_is == 1
                        bindingPasswordProtect.value = it.data.question_is == 1
                        onSuccess()
                    } else {
                        context.toast("检查绑定失败")
                    }
                }, onError = {
                    context.toast("检查绑定失败")
                })
    }

    //检查是否为默认密码
    fun checkDefaultPassword(stu_num: String, onSuccess: () -> Unit) {
        apiService.checkDefaultPassword(stu_num)
                .setSchedulers()
                .safeSubscribeBy(onNext = {
                    if (it.status == 10000) {
                        isDefaultPassword.value = true
                    } else {
                        isDefaultPassword.value = false
                    }
                    onSuccess()
                }, onError = {
                    Log.d("zt", it.toString())
                })
    }

}