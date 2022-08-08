package com.mredrock.cyxbs.lib.utils

import android.app.Application

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/1 12:50
 */
object UtilsApplicationWrapper {
  
  internal lateinit var appContext: Application
    private set
  
  /**
   * 设置 lib_utils 模块的 appContext
   */
  fun setUtilsApplication(application: Application) {
    appContext = application
  }
}