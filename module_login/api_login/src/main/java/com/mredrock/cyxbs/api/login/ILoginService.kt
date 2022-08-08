package com.mredrock.cyxbs.api.login

import android.app.Activity
import android.content.Context
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
   * @param successActivity 登录过后启动的 Activity，传入 null 则直接 finish 掉 LoginActivity
   * @param touristModeActivity 游客模式的 Activity
   * @param intent 用于设置一些启动 LoginActivity 的配置
   */
  fun startLoginActivity(
    context: Context,
    successActivity: Class<out Activity>?,
    touristModeActivity: Class<out Activity>,
    intent: (Intent.() -> Unit)? = null
  ) {
    startLoginActivity(
      context,
      if (successActivity == null) null else Intent(context, successActivity),
      Intent(context, touristModeActivity),
      intent
    )
  }
  
  /**
   * 不管登录成功还是直接使用游客模式都强制重启应用
   */
  fun startLoginActivityReboot(
    context: Context,
    intent: (Intent.() -> Unit)? = null
  ) {
    // 不管登录成功还是游客模式都跳转到主界面启动的 Activity
    val launchActivityIntent =
      context.packageManager.getLaunchIntentForPackage(context.packageName)!!
    startLoginActivity(context, launchActivityIntent, launchActivityIntent, intent)
  }
  
  /**
   * @param successActivity 传入 null 表示 finish 后回到之前的 Activity
   */
  fun startLoginActivity(
    context: Context,
    successActivity: Class<out Activity>?,
    intent: (Intent.() -> Unit)? = null
  ) {
    // 游客模式时跳转到主界面启动的 Activity
    val launchActivityIntent =
      context.packageManager.getLaunchIntentForPackage(context.packageName)!!
    val successIntent = if (successActivity == null) null else Intent(context, successActivity)
    startLoginActivity(context, successIntent, launchActivityIntent, intent)
  }
  
  fun startLoginActivity(
    context: Context,
    successIntent: Intent?,
    touristModeIntent: Intent,
    intent: (Intent.() -> Unit)? = null
  )
}