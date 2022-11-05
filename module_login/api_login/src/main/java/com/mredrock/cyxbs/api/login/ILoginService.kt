package com.mredrock.cyxbs.api.login

import android.app.Activity
import android.content.Intent
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/7 21:03
 */
interface ILoginService : IProvider {
  
  /**
   * 登录成功后直接返回上一个 Activity 界面
   * @param intent 用于设置一些启动 LoginActivity 的配置
   */
  fun startLoginActivity(intent: (Intent.() -> Unit)? = null)
  
  /**
   * 登录成功后打开指定的 Activity
   * @param successActivity 登录后启动的 Activity
   * @param intent 用于设置一些启动 LoginActivity 的配置
   */
  fun startLoginActivity(
    successActivity: Class<out Activity>,
    intent: (Intent.() -> Unit)? = null
  )
  
  /**
   * 登录成功后重新打开应用，会清空之前的所有 Activity
   */
  fun startLoginActivityReboot(intent: (Intent.() -> Unit)? = null)
}