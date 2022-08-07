package com.mredrock.cyxbs.login.service

import android.app.Activity
import android.content.Context
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.login.page.LoginActivity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/7 21:11
 */
class LoginServiceImpl : ILoginService {
  
  override fun startLoginActivity(context: Context, targetActivity: Class<out Activity>) {
    LoginActivity.startActivity(context, targetActivity)
  }
  
  override fun init(context: Context) {
  }
}