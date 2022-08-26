package com.mredrock.cyxbs.lib.base

import android.os.Bundle
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.service.impl

/**
 * 单模块调试的 BaseDebugActivity
 *
 * 这个 Activity 设置简单一点，直接在 [onDebugCreate] 中启动主界面即可
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
          .startLoginActivity(this, this::class.java)
        finish()
      } else {
        onDebugCreate(savedInstanceState)
      }
    } else {
      onDebugCreate(savedInstanceState)
    }
  }

  /**
   * 主要是用来防止你直接在 onCreate 中 startActivity，导致登录界面被你的界面覆盖
   */
  abstract fun onDebugCreate(savedInstanceState: Bundle?)
}