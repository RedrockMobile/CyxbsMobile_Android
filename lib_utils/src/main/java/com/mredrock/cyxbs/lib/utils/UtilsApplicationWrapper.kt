package com.mredrock.cyxbs.lib.utils

import android.app.Application
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/1 12:50
 */
@AutoService(InitialService::class)
class UtilsApplicationWrapper : InitialService {
  
  companion object {
    internal lateinit var application: Application
      private set
  }
  
  override fun onAllProcess(manager: InitialManager) {
    application = manager.application
  }
}