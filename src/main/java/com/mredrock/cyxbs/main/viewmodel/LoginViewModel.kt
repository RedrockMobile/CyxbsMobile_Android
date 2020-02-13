package com.mredrock.cyxbs.main.viewmodel

import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.takeIfNoException
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.ui.MainActivity
import com.umeng.analytics.MobclickAgent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import kotlin.concurrent.thread

/**
 * Created By jay68 on 2018/8/12.
 */
class LoginViewModel : BaseViewModel() {
    var userAgreementIsCheck = false

    //是否正在登陆，防止用户多次点击
    var isLanding = false

    fun login(stuNum: String?, idNum: String?, landing: () -> Unit) {
        if (isLanding) return
        if (stuNum?.length ?: 0 < 10) {
            toastEvent.value = R.string.main_activity_login_not_input_account
            return
        } else if (idNum?.length ?: 0 < 6) {
            toastEvent.value = R.string.main_activity_login_not_input_password
            return
        }
        isLanding = true
        landing()
        verifyByWeb(stuNum!!, idNum!!, landing)
    }

    private fun verifyByWeb(stuNum: String, idNum: String, landing: () -> Unit) {
        thread {

        }
        Observable.create<LoginState> {
            val startTime = System.currentTimeMillis()
            var loginState = LoginState.NotLanded
            takeIfNoException(
                    action = {
                        val accountService = ServiceManager.getService(IAccountService::class.java)
                        accountService.getVerifyService().login(BaseApp.context, stuNum, idNum)
                        MobclickAgent.onProfileSignIn(accountService.getUserService().getStuNum())
                        EventBus.getDefault().post(LoginStateChangeEvent(true))
                        loginState = LoginState.LandingSuccessfully
                    },
                    doOnException = { e ->
                        loginState = LoginState.LandingFailed
                        e
                    },
                    doFinally = {
                        //网速太好的时候对话框只会闪一下，像bug一样
                        val curTime = System.currentTimeMillis()
                        val waitTime = 2000
                        if (curTime - startTime < waitTime) {
                            takeIfNoException {
                                Thread.sleep(waitTime - curTime + startTime)
                                it.onNext(loginState)
                            }
                        } else {
                            it.onNext(loginState)
                        }
                        isLanding = false
                    }
            )
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            when (it) {
                LoginState.LandingSuccessfully -> context.startActivity<MainActivity>()
                LoginState.LandingFailed -> {
                    landing()
                    CyxbsToast.makeText(context, R.string.main_login_error_prompt, Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        }.isDisposed
    }

    enum class LoginState {
        LandingSuccessfully, NotLanded, LandingFailed
    }
}