package com.mredrock.cyxbs.lib.utils

import android.app.Application
import androidx.annotation.CallSuper
import com.alibaba.android.arouter.launcher.ARouter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/1 12:50
 */
abstract class UtilsApplication : Application() {
  companion object {
    internal lateinit var appContext: UtilsApplication
      private set
  }
  
  @CallSuper
  override fun onCreate() {
    super.onCreate()
    appContext = this
    initARouter()
  }
  
  private fun initARouter() {
    if (BuildConfig.DEBUG) {
      ARouter.openLog()
      ARouter.openDebug()
    }
    ARouter.init(this)
  }
}