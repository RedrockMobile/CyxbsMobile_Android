package com.mredrock.cyxbs.sdks

import com.alibaba.android.arouter.launcher.ARouter
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.spi.SdkService
import com.mredrock.cyxbs.spi.SdkManager

/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  13:50
 *@signature 我将追寻并获取我想要的答案
 */
@AutoService(SdkService::class)
class ARouterInitialService : SdkService {

    override fun onMainProcess(manager: SdkManager) {
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(manager.application)
    }

}