package com.mredrock.cyxbs.lib.base

import android.os.Bundle
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.common.service.impl
import com.mredrock.cyxbs.lib.base.ui.BaseActivity

/**
 * 单模块调试的 BaseDebugActivity
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/7 20:18
 */
abstract class BaseDebugActivity : BaseActivity() {
  
  protected open val isNeedLogin: Boolean
    get() = true
  
  @CallSuper
  final override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (isNeedLogin) {
      val isLogin = IAccountService::class.impl
        .getVerifyService()
        .isLogin()
      if (!isLogin) {
        ILoginService::class.impl
          .startLoginActivityReboot(this)
        finish()
      } else {
        onDebugCreate(savedInstanceState)
      }
    } else {
      onDebugCreate(savedInstanceState)
    }
  }
  
  abstract fun onDebugCreate(savedInstanceState: Bundle?)
}