package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.api.crash.ICrashService
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.spi.SdkManager
import com.mredrock.cyxbs.spi.SdkService

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/13
 * @Description:
 */
@AutoService(SdkService::class)
class CrashInitialService2 : SdkService {
    override fun onMainProcess(manager: SdkManager) {
        super.onMainProcess(manager)
        ServiceManager(ICrashService::class).initCrashMonitor(manager.application)
    }
}