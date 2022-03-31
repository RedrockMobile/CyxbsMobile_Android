package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.BuildConfig
import com.mredrock.cyxbs.spi.SdkManager
import com.mredrock.cyxbs.spi.SdkService
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport

/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  19:40
 *@signature 我将追寻并获取我想要的答案
 */
@AutoService(SdkService::class)
class BuglyInitialService : SdkService {
    override fun onMainProcess(manager: SdkManager) {

        val appContext = manager.application.applicationContext

        if (BuildConfig.DEBUG) {
            CrashReport.setUserSceneTag(appContext, 83913)
        } else {
            CrashReport.setUserSceneTag(appContext, 202291)
        }

        //设置上报进程
        val strategy = CrashReport.UserStrategy(appContext).apply {
            isUploadProcess = true
            appVersion = BuildConfig.VERSION_NAME
            appPackageName = BuildConfig.APPLICATION_ID
        }

        //初始化bugly
        Bugly.init(appContext,BuildConfig.BUGLY_APP_ID,BuildConfig.DEBUG,strategy)
    }
}