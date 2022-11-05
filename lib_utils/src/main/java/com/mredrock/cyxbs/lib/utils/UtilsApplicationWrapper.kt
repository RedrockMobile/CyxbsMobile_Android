package com.mredrock.cyxbs.lib.utils

import android.app.Application

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/1 12:50
 */
object UtilsApplicationWrapper {
  
  internal lateinit var application: Application
    private set
  
  /**
   * 为了不反向依赖 lib_base，所以单独设置 lib_utils 模块的 appContext
   */
  fun initialize(application: Application) {
    this.application = application
  }
}