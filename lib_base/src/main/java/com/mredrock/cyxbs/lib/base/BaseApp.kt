package com.mredrock.cyxbs.lib.base

import android.content.Context
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.utils.UtilsApplication

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/26 14:01
 */
open class BaseApp : UtilsApplication() {
  companion object {
    lateinit var appContext: Context
      private set
  }
  
  @CallSuper
  override fun onCreate() {
    super.onCreate()
    appContext = this
  }
}