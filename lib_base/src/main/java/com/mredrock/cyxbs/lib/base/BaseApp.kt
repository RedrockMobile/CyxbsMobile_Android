package com.mredrock.cyxbs.lib.base

import android.app.Application
import androidx.annotation.CallSuper
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.lib.utils.UtilsApplicationWrapper

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/26 14:01
 */
open class BaseApp : Application() {
  companion object {
    lateinit var baseApp: BaseApp
      private set
  }
  
  @CallSuper
  override fun onCreate() {
    super.onCreate()
    baseApp = this
    UtilsApplicationWrapper.setUtilsApplication(this)
    com.mredrock.cyxbs.common.BaseApp.onCreate(this, this::privacyDenied, this::privacyAgree)
    initARouter()
  }
  
  open fun privacyAgree() {}
  open fun privacyDenied() {}
  
  /**
   * 在单模块调试时也需要该 ARouter，所以直接在这里初始化
   */
  private fun initARouter() {
    if (BuildConfig.DEBUG) {
      ARouter.openDebug()
      ARouter.openLog()
    }
    ARouter.init(this)
  }
}