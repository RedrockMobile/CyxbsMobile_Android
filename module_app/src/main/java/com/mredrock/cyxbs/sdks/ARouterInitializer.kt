package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.spi.SdkInitializer
import com.mredrock.cyxbs.spi.SdkManager
import com.mredrock.cyxbs.init.InitARouter

/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  13:50
 *@signature 我将追寻并获取我想要的答案
 */
@AutoService(SdkInitializer::class)
class ARouterInitializer : SdkInitializer {

    override fun initialWithoutConstraint(manager: SdkManager) {
        InitARouter.init(manager.application)
    }

}