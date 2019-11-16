package com.mredrock.cyxbs.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.takeIfNoException
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.main.R
import com.umeng.analytics.MobclickAgent
import org.greenrobot.eventbus.EventBus
import kotlin.concurrent.thread

/**
 * Created By jay68 on 2018/8/12.
 */
class LoginViewModel : BaseViewModel() {
    val backToMainEvent: LiveData<Boolean> = MutableLiveData()

    fun login(stuNum: String?, idNum: String?) {
        if (stuNum?.length ?: 0 < 10) {
            toastEvent.value = R.string.main_activity_login_not_input_account
            return
        } else if (idNum?.length ?: 0 < 6) {
            toastEvent.value = R.string.main_activity_login_not_input_password
            return
        }
        verifyByWeb(stuNum!!, idNum!!)
    }

    private fun verifyByWeb(stuNum: String, idNum: String) {
        thread {
            val startTime = System.currentTimeMillis()

            takeIfNoException(
                    action = {
                        progressDialogEvent.postValue(ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT)
                        val accountService = ServiceManager.getService(IAccountService::class.java)
                        accountService.getVerifyService().login(BaseApp.context, stuNum, idNum)
                        MobclickAgent.onProfileSignIn(accountService.getUserService().getStuNum())
                        EventBus.getDefault().post(LoginStateChangeEvent(true))
                        (backToMainEvent as MutableLiveData).postValue(true)
                    },
                    doOnException = { e ->
                        //todo 处理密码错误、HTTP异常等
                    },
                    doFinally = {
                        //网速太好的时候对话框只会闪一下，像bug一样
                        val curTime = System.currentTimeMillis()
                        if (curTime - startTime < 500) {
                            takeIfNoException { Thread.sleep(500 - curTime + startTime) }
                        }
                        progressDialogEvent.postValue(ProgressDialogEvent.DISMISS_DIALOG_EVENT)
                    }
            )
        }
    }
}