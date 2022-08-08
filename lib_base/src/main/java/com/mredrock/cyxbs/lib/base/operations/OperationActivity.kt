package com.mredrock.cyxbs.lib.base.operations

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.lib.base.ui.BaseUi
import com.mredrock.cyxbs.lib.utils.extensions.RxjavaLifecycle
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.service.impl

/**
 *
 * 业务层的 Activity
 *
 * 主要用于书写与业务相耦合的代码，比如需要使用到 api 模块时
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/8 19:16
 */
abstract class OperationActivity(
  private val options: Options
) : AppCompatActivity(), BaseUi, RxjavaLifecycle {
  
  @CallSuper
  override fun onResume() {
    super.onResume()
    
    // 选择在这里打开登录界面的原因在于：登录界面直接返回，就强制跳回去
    if (options.isCheckLogin) {
      var isStartLoginActivity = false
      val accountService = IAccountService::class.impl
      val isTouristMode = accountService.getVerifyService().isTouristMode()
      val isLogin = accountService.getVerifyService().isLogin()
      if (!isTouristMode) {
        // 不是游客模式
        if (!isLogin) {
          // 但没有登录
          if (options.isShowToastIfNoLogin) {
            toast("掌友，你还没有登录哦")
          }
          isStartLoginActivity = true
        }
      } else {
        val isRefreshTokenExpired = accountService.getVerifyService().isRefreshTokenExpired()
        if (isLogin && isRefreshTokenExpired) {
          // 已登录但 refreshToken 过期
          if (options.isShowToastIfNoLogin) {
            toast("身份信息已过期，请重新登录")
          }
          isStartLoginActivity = true
        }
      }
      if (isStartLoginActivity) {
        ILoginService::class.impl.startLoginActivity(this, this::class.java)
        // 取消动画，让用户感觉直接就进入了界面
        overridePendingTransition(0, 0)
        // 再让 decorView 不显示，完美
        window.decorView.invisible()
        finish() // 因为此时该 activity 已经被加载，网络请求已经发送，但会因为没有登录而拿不到数据，所以要先 finish 掉
      }
    }
  }
  
  interface Options {
    /**
     * 是否检查登录，如果为 true，则会在未登录时直接跳转到登录界面
     */
    val isCheckLogin: Boolean
      get() = true
    
    val isShowToastIfNoLogin: Boolean
      get() = true
  }
}