package com.mredrock.cyxbs.main.viewmodel

import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.CommonApiService
import com.mredrock.cyxbs.common.network.exception.DefaultErrorHandler
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.common.utils.down.params.DownMessageParams
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
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

    //是否正在登录，防止用户多次点击
    private var isLanding = false

    val userAgreementList: MutableList<DownMessageText> = mutableListOf()

    fun login(stuNum: String?, idNum: String?, landing: () -> Unit, successAction: () -> Unit) {
        if (isLanding) return
        if (checkDataCorrect(stuNum, idNum)) return
        isLanding = true
        landing()
        var isSuccess = false
        val startTime = System.currentTimeMillis()
        Observable.create<Boolean> {
            takeIfNoException(
                    action = {
                        val accountService = ServiceManager.getService(IAccountService::class.java)
                        accountService.getVerifyService().login(context, stuNum!!, idNum!!)
                        MobclickAgent.onProfileSignIn(accountService.getUserService().getStuNum())
                        isSuccess = true
                    },
                    doOnException = {
                        isSuccess = false
                        throw it
                    },
                    doFinally = {
                        //网速太好的时候对话框只会闪一下，像bug一样
                        val curTime = System.currentTimeMillis()
                        val waitTime = 2000
                        if (curTime - startTime < waitTime) {
                            takeIfNoException {
                                Thread.sleep(waitTime - curTime + startTime)
                                it.onNext(isSuccess)
                            }
                        } else {
                            it.onNext(isSuccess)
                        }
                    }
            )


        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy(
                        onError = {
                            isLanding = false
                            landing()
                            //交给封装的异常处理，应对不同的情况，给用户提示对应的信息
                            DefaultErrorHandler.handle(it)
                        },
                        onNext = {
                            isLanding = false
                            if (it) successAction()
                        }
                ).isDisposed
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
        ApiGenerator.getCommonApiService(CommonApiService::class.java)
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

}