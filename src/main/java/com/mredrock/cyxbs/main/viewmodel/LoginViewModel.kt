package com.mredrock.cyxbs.main.viewmodel

import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.network.ApiGeneratorForAnother
import com.mredrock.cyxbs.common.network.CommonApiService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.common.utils.down.params.DownMessageParams
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.takeIfNoException
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.network.ExecuteOnceObserver
import com.umeng.analytics.MobclickAgent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created By jay68 on 2018/8/12.
 */
class LoginViewModel : BaseViewModel() {
    var userAgreementIsCheck = false

    //是否正在登陆，防止用户多次点击
    private var isLanding = false

    val userAgreementList: MutableList<DownMessageText> = mutableListOf()

    fun login(stuNum: String?, idNum: String?, landing: () -> Unit, successAction: () -> Unit) {
        if (isLanding) return
        if (checkDataCorrect(stuNum, idNum)) return
        isLanding = true
        landing()
        Observable.create<LoginState> {
            val startTime = System.currentTimeMillis()
            var loginState = LoginState.NotLanded
            takeIfNoException(
                    action = {
                        val accountService = ServiceManager.getService(IAccountService::class.java)
                        accountService.getVerifyService().login(context, stuNum!!, idNum!!)
                        MobclickAgent.onProfileSignIn(accountService.getUserService().getStuNum())
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
                LoginState.LandingSuccessfully -> successAction()
                LoginState.LandingFailed -> {
                    landing()
                    CyxbsToast.makeText(context, R.string.main_login_error_prompt, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    //这种情况按道理来说不会出现，所以直接返回就好
                    landing()
                }
            }
        }.isDisposed
    }

    private fun checkDataCorrect(stuNum: String?, idNum: String?): Boolean {
        if ((stuNum?.length ?: 0) < 10) {
            toastEvent.value = R.string.main_activity_login_not_input_account
            return true
        } else if ((idNum?.length ?: 0) < 6) {
            toastEvent.value = R.string.main_activity_login_not_input_password
            return true
        }
        return false
    }

    fun getUserAgreement(successCallBack: () -> Unit) {
        val time = System.currentTimeMillis()
        ApiGeneratorForAnother.getApiService(CommonApiService::class.java)
                .getDownMessage(DownMessageParams("zscy-main-userAgreement"))
                .setSchedulers(observeOn = Schedulers.io())
                .errorHandler()
                .doOnNext {
                    //有时候网路慢会转一下圈圈，但是有时候网络快，圈圈就像是闪了一下，像bug，就让它最少转一秒吧
                    val l = 1000 - (System.currentTimeMillis() - time)
                    Thread.sleep(if (l > 0) l else 0)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ExecuteOnceObserver(
                        onExecuteOnceNext = {
                            userAgreementList.clear()
                            userAgreementList.addAll(it.data.textList)
                            successCallBack()
                        }
                ))
    }

    enum class LoginState {
        LandingSuccessfully, NotLanded, LandingFailed
    }
}