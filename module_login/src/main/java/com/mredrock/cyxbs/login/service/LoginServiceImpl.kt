package com.mredrock.cyxbs.login.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.api.login.LOGIN_SERVICE
import com.mredrock.cyxbs.login.page.login.ui.LoginActivity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/7 21:11
 */
@Route(path = LOGIN_SERVICE)
class LoginServiceImpl : ILoginService {
  
  override fun startLoginActivity(intent: (Intent.() -> Unit)?) {
    LoginActivity.start(intent)
  }
  
  override fun startLoginActivity(
    successActivity: Class<out Activity>,
    intent: (Intent.() -> Unit)?
  ) {
    LoginActivity.start(successActivity, intent)
  }
  
  override fun startLoginActivityReboot(intent: (Intent.() -> Unit)?) {
    LoginActivity.startReboot(intent)
  }
  
  override fun init(context: Context) {
  }
}