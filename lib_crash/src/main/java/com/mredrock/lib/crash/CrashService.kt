package com.mredrock.lib.crash

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.lib.base.spi.InitialManager
import com.mredrock.cyxbs.lib.base.spi.InitialService
import com.mredrock.lib.crash.core.CyxbsCrashMonitor

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/13
 * @Description:
 */
@AutoService(InitialService::class)
class CrashService: InitialService {
    override fun onMainProcess(manager: InitialManager) {
        super.onMainProcess(manager)
        if (!BuildConfig.DEBUG) {
            CyxbsCrashMonitor.install(manager.application)
        }
    }
}