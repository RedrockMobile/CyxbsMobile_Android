package com.mredrock.cyxbs.config

import android.app.Application

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/8/8 22:37
 */
object ConfigApplicationWrapper {
  
  internal lateinit var application: Application
    private set
  
  /**
   * 为了不反向依赖 lib_base，所以单独设置 lib_config 模块的 appContext
   */
  fun initialize(application: Application) {
    this.application = application
  }
}