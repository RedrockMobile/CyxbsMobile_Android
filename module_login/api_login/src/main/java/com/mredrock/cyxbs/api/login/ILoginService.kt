package com.mredrock.cyxbs.api.login

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/7 21:03
 */
interface ILoginService : IProvider {
  
  /**
   * @param targetActivity 登录过后启动的 Activity
   */
  fun startLoginActivity(
    context: Context,
    targetActivity: Class<out Activity>,
  )
}