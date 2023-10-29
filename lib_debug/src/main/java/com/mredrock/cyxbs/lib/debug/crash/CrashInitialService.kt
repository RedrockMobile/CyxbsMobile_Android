package com.mredrock.cyxbs.lib.debug.crash

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService

/**
 * .
 *
 * @author 985892345
 * @date 2022/9/23 15:51
 */
@AutoService(InitialService::class)
class CrashInitialService : InitialService {
  
  override fun onMainProcess(manager: InitialManager) {
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      CrashActivity.start(
        throwable,
        manager.currentProcessName,
        thread.name
      )
    }

  }
  
  override fun onOtherProcess(manager: InitialManager) {
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      CrashActivity.start(
        throwable,
        manager.currentProcessName,
        thread.name
      )
    }
  }
}