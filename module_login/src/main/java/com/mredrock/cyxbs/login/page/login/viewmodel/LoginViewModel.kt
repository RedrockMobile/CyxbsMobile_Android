package com.mredrock.cyxbs.login.page.login.viewmodel

import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * Created By jay68 on 2018/8/12.
 */
class LoginViewModel : BaseViewModel() {
  
  // 用户隐私是否同意检查
  var userAgreementIsCheck = false
  
  //是否正在登录，防止用户多次点击
  private var isLanding = false
  
  private val _loginEvent = MutableSharedFlow<Boolean>()
  val loginEvent: SharedFlow<Boolean>
    get() = _loginEvent
  
  fun login(stuNum: String, password: String) {
    if (isLanding) return
    isLanding = true
    val startTime = System.currentTimeMillis()
    Completable.create {
      try {
        IAccountService::class.impl
          .getVerifyService()
          .login(appContext, stuNum, password)
        it.onComplete()
      } catch (e: Exception) {
        it.tryOnError(e)
      }
    }.subscribeOn(Schedulers.io())
      .delay(
        // 网络太快会闪一下，像bug，就让它最少待两秒吧
        (System.currentTimeMillis() - startTime).let { if (it > 2000) 0 else it },
        TimeUnit.MILLISECONDS
      ).observeOn(AndroidSchedulers.mainThread())
      .doOnTerminate {
        // 这个不能使用 doOnComplete
        isLanding = false
      }.doOnError {
        when (it) {
          is SocketTimeoutException,
          is ConnectException,
          is UnknownHostException,
          -> toast("网络中断，请检查您的网络状态")
          is HttpException -> toast("登录服务暂时不可用")
          is IllegalStateException -> toast("登录失败：学号或者密码错误,请检查输入")
          else -> {
            toast(it.message)
          }
        }
        viewModelScope.launch {
          _loginEvent.emit(false)
        }
      }.safeSubscribeBy {
        viewModelScope.launch {
          _loginEvent.emit(true)
        }
      }
  }
}