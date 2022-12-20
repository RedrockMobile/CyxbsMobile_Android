package com.mredrock.cyxbs.config

import android.app.Application
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/8/8 22:37
 */
@AutoService(InitialService::class)
class ConfigApplicationWrapper : InitialService {
  
  companion object {
    internal lateinit var application: Application
      private set
  }
  
  override fun onAllProcess(manager: InitialManager) {
    application = manager.application
  }
}