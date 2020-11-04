package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.ConfirmCode
import com.mredrock.cyxbs.mine.util.apiService
import io.reactivex.Observable
import java.lang.Exception

/**
 * Author: RayleighZ
 * Time: 2020-11-03 1:08
 */
class FindPasswordViewModel : BaseViewModel() {
    //获取验证码部分的text，含有倒计时提醒
    val timerText = ObservableField<String>()
    //输入框内部的字符
    val inputText = ObservableField<String>()
    //在输入框下面的第一个提示的内容
    val firstTipText = ObservableField<String>()
    private var canClick = true
    private var lastTime = 30
    //用户的邮箱地址
    private lateinit var emailAddress : String
    //单例的用于倒计时的子线程
    private val clockRunnable: Runnable by lazy {
        Runnable {
            //在倒计时过程中不允许点击
            canClick = false
            //使用dataBinding，不需要进行线程调度
            lastTime = 30
            while (lastTime > -1) {

                timerText.set("正在发送(${lastTime})")
                LogUtils.d("SendConfirmCode", "正在倒计时")
                try {
                    lastTime--
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //届时倒计时已经为0
            timerText.set("重新发送")
            canClick = true
        }
    }

    //启动倒计时以及网络请求
    //TODO:尚未配置网络请求
    //此处需要防止暴击，在lastTime未归零之前不允许点击
    //可能存在线程安全问题
    fun sendConfirmCodeAndStartBackTimer() {
        //如果用户已经登陆，则使用个人界面的接口
        var observable : Observable<RedrockApiWrapper<ConfirmCode>>? = null
        if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()){
            if (canClick) {
                //TODO:在这里适配网络请求
                observable = apiService.sendEmailConfirmCode(
                        //用户的邮箱地址
                        emailAddress
                )
            }
        } else {
            observable = apiService.sendConfirmCodeWithoutLogin(
                    //用户的邮箱地址
                    emailAddress
            )
        }
        observable?.safeSubscribeBy(
                onError = {
                    BaseApp.context.toast("对不起，验证码发送失败")
                },
                onNext = {
                    //TODO:接口文档尚未更新
                }
        )
    }

    //启动校验验证码的网络请求
    fun sendConfirmCodeRequest(){

    }

    //获取用户的绑定邮箱
    fun getBindingEmail(){

    }
}