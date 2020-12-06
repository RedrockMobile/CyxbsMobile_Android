package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.databinding.ObservableField
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.SecurityQuestion
import com.mredrock.cyxbs.mine.util.apiService

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

    lateinit var email: String

    //用户的邮箱地址，同时这一行也展示用户的密保问题
    val emailAddressOrQuestion = ObservableField<String>()

    //是否允许用户点击发送验证码
    private var canClickSendCode = true

    //是否允许用户点击下一步（在基础的邮箱以及密保问题未获取前不允许点击）
    private var canClickNext = false

    //发送邮箱验证三十秒倒计时的剩余倒计时数
    private var lastTime = 30

    //用户的学号
    var stuNumber = ""

    //过期时间
    var expiredTime = 0;

    //获取的用户问题
    lateinit var question: SecurityQuestion

    //单例的用于倒计时的runnable
    private val clockRunnable: Runnable by lazy {
        Runnable {
            //在倒计时过程中不允许点击
            canClickSendCode = false
            //使用dataBinding，不需要进行线程调度
            lastTime = 30
            while (lastTime > -1) {

                timerText.set("正在发送(${lastTime})")
                try {
                    lastTime--
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //届时倒计时已经为0
            timerText.set("重新发送")
            canClickSendCode = true
        }
    }

    //启动倒计时以及网络请求
    //此处需要防止暴击，在lastTime未归零之前不允许点击
    //可能存在线程安全问题
    fun sendConfirmCodeAndStartBackTimer() {
        //如果用户已经登陆，则使用个人界面的接口
        if (canClickSendCode) {
            apiService.getEmailFindPasswordCode(
                    //用户的学号
                    stuNumber
            )
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        BaseApp.context.toast("对不起，验证码发送失败，原因为:$it")
                        true
                    }
                    .safeSubscribeBy {
                        when (it.status) {
                            10000 -> {
                                //发送成功
                                expiredTime = it.data.expired_time
                                BaseApp.context.toast("已向你的邮箱发送了一条验证码")
                                Thread(clockRunnable).start()
                            }
                            10008 -> {
                                BaseApp.context.toast("邮箱信息错误")
                            }
                            10009 -> {
                                BaseApp.context.toast("发送次数已达上限，请十分钟后再次尝试")
                            }
                        }
                    }

        }
    }

    //校验找回邮箱的验证码
    fun confirmCode(onSuccess: (code: Int) -> Unit, onField: () -> Unit) {
        if (canClickNext) {
            if (expiredTime < System.currentTimeMillis() / 1000) {
                BaseApp.context.toast("验证码过期")
                return
            }
            if (inputText.get() == null) {
                //输入验证码为空，弹出提示
                BaseApp.context.toast("验证码错误")
                return
            }
            canClickNext = false
            inputText.get()?.let {
                apiService.confirmCodeWithoutLogin(
                        stuNumber,
                        email,
                        it.toInt()
                )
                        .setSchedulers()
                        .doOnErrorWithDefaultErrorHandler { error ->
                            BaseApp.context.toast("验证失败，原因为$error")
                            canClickNext = true
                            true
                        }
                        .safeSubscribeBy(
                                onError = {

                                },
                                onNext = { cq ->
                                    if (cq.status == 10000) {
                                        //回调
                                        onSuccess(cq.data.code)
                                        //因为这里下一步就要去跳转页面了，没有必要再将canClickNext设置为true
                                    } else if (cq.status == 10007) {
                                        BaseApp.context.toast("验证码错误")
                                        onField()
                                        canClickNext = true
                                    }
                                }
                        )
            }
        }
    }

    //获取用户的绑定邮箱，虽然不需要在其他接口中使用，但需要给用户展示
    fun getBindingEmail() {
        apiService.getUserEmail(stuNumber)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    BaseApp.context.toast("获取邮箱信息失败，原因为$it")
                    true
                }
                .safeSubscribeBy {
                    if (it.status == 10000) {
                        email = it.data.email
                        if (email == null || email == "") {
                            BaseApp.context.toast("返回邮箱为空")
                        } else {
                            //下面是抄的齐哥的邮箱加密策略
                            val atLocation = email.indexOf("@")
                            var showUserEmail = email
                            when {
                                atLocation in 2..4 -> {
                                    showUserEmail = showUserEmail.substring(0, 1) + "*" + showUserEmail.substring(2, showUserEmail.length)
                                }
                                atLocation == 5 -> {
                                    showUserEmail = showUserEmail.substring(0, 2) + "**" + showUserEmail.substring(4, showUserEmail.length)
                                }
                                atLocation > 5 -> {
                                    var starString = ""
                                    for (i in 0 until atLocation - 4) starString += "*"
                                    showUserEmail = showUserEmail.substring(0, 2) + starString + showUserEmail.substring(atLocation - 2, showUserEmail.length)
                                }
                            }
                            emailAddressOrQuestion.set(showUserEmail)
                            canClickNext = true//数据加载完毕，允许用户点击next
                        }
                    } else if (it.status == 10024) {
                        BaseApp.context.toast("你尚未绑定邮箱")
                    }
                }
    }

    //获取用户的密保问题信息
    fun getUserQuestion() {
        apiService.getUserQuestion(stuNumber)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    BaseApp.context.toast("服务器君打盹了,$it")
                    true
                }
                .safeSubscribeBy {
                    if (it.status == 10000) {
                        //目前仅仅有一个密保问题
                        //后端为了拓展将这里的返回值设计成了一个集合
                        //就目前而言这个集合应该之后一个值
                        question = it.data[0]
                        emailAddressOrQuestion.set(it.data[0].content)
                        canClickNext = true//数据加载完毕，允许用户点击下一步
                    } else {
                        BaseApp.context.toast("您还没有设置密保")
                    }
                }

    }

    //验证用户输入的密保问题是否正确
    fun confirmAnswer(onSuccess: (code: Int) -> Unit) {
        if (canClickNext) {
            if (inputText.get() == null) {
                //展示最少输入两个字符
                firstTipText.set("请至少输入两个字符")
                return
            }
            inputText.get()?.let {
                if (it.length < 2) {
                    firstTipText.set("请至少输入两个字符")
                    return
                } else if (it.length >= 16) {
                    firstTipText.set("输入已达上限")
                    return
                }
                //输入情况正常以后，允许进行正常的网络请求
                canClickNext = false//防暴击
                apiService.confirmAnswer(
                        stuNumber,
                        question.id,//这里canClick就一定是成功获取了question的，可以不用担心空指针
                        it
                )
                        .setSchedulers()
                        .doOnErrorWithDefaultErrorHandler { exception ->
                            BaseApp.context.toast("验证密保问题失败，原因为:$exception")
                            canClickNext = true
                            true
                        }
                        .safeSubscribeBy { cq ->
                            when (cq.status) {
                                10006 -> {//用户尝试次数已经达到上限
                                    BaseApp.context.toast("输入次数已达上限，请10分钟后再次尝试")
                                    canClickNext = true
                                }
                                10005 -> {//密码错误
                                    BaseApp.context.toast("答案错误，请重新输入")
                                    canClickNext = true
                                }
                                10000 -> {//正确
                                    onSuccess(cq.data.code)
                                }
                            }
                        }.lifeCycle()
            }
        }
    }
}