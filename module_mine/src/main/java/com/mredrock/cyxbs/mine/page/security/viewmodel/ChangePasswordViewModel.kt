package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService

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
    var inputNewPasswordFormat: Int = 0

    //是否绑定邮箱
    var bindingEmail: Boolean = false

    //是否绑定密保
    var bindingPasswordProtect: Boolean = false

    //是否为默认密码
    var isDefaultPassword: Boolean = false

    //检查旧密码输入是否相同
    fun originPassWordCheck(originPassword: String) {
        apiService.originPassWordCheck(originPassword)
                .doOnErrorWithDefaultErrorHandler {
                    context.toast(it.toString())
                    true
                }
                .setSchedulers()
                .safeSubscribeBy {
                    originPassWordIsCorrect.value = it.status == 10000
                }
    }

    //进行新密码的传入
    fun newPassWordInput(origin_password: String, new_password: String) {
        apiService.resetPassword(origin_password, new_password)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    context.toast("对不起，目前无法修改密码，原因为:$it")
                    true
                }
                .safeSubscribeBy {
                    when (it.status) {
                        10000 -> {
                            inputNewPasswordCorrect.value = true
                            inputNewPasswordFormat = 10000
                        }
                        10002 -> {
                            inputNewPasswordCorrect.value = false
                            inputNewPasswordFormat = 10002
                            context.toast("原密码错误！")
                        }
                        10004 -> {
                            inputNewPasswordCorrect.value = false
                            inputNewPasswordFormat = 10004
                            context.toast("密码格式有问题！")
                        }
                        10020 -> {
                            inputNewPasswordCorrect.value = false
                            inputNewPasswordFormat = 10020
                            context.toast("新旧密码重复！")
                        }
                    }
                }
    }

    //在未登录的条件下修改密码（需要随机数）
    fun resetPasswordFromLogin(stu_num: String, new_password: String, code: Int) {
        apiService.resetPasswordFromLogin(stu_num, new_password, code)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    context.toast(it.toString())
                    true
                }
                .safeSubscribeBy {
                    when (it.status) {
                        10000 -> {
                            inputNewPasswordCorrect.value = true
                            //修改成功的toast要在主界面弹出，不然只会闪一下
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
                }
    }

    //检查是否绑定信息
    fun checkBinding(stu_num: String, onSuccess: () -> Unit) {
        apiService.checkBinding(stu_num)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    context.toast(it.toString())
                    true
                }
                .safeSubscribeBy {
                    if (it.status == 10000) {
                        //设置信息绑定的情况
                        bindingEmail = it.data.email_is == 1
                        bindingPasswordProtect = it.data.question_is == 1
                        onSuccess()
                    } else {
                        context.toast("检查绑定失败")
                    }
                }
    }

    //检查是否为默认密码
    fun checkDefaultPassword(stu_num: String, onSuccess: () -> Unit) {
        apiService.checkDefaultPassword(stu_num)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    context.toast(it.toString())
                    true
                }
                .safeSubscribeBy {
                    isDefaultPassword = it.status == 10000
                    onSuccess()
                }
    }
}