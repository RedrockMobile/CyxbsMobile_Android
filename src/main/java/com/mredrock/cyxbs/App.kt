package com.mredrock.cyxbs

import android.os.Process
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.common.utils.getProcessName
import com.tencent.bugly.crashreport.CrashReport

/**
 * Created By jay68 on 2018/8/8.
 */
class App : BaseApp() {
    override fun onCreate() {
        super.onCreate()
        initBugly()
    }

    private fun initBugly() {
        val packageName = applicationContext.packageName
        val processName = getProcessName(Process.myPid())
        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.appVersion = getAppVersionName(applicationContext)
        strategy.isUploadProcess = processName == null || processName == packageName
        CrashReport.initCrashReport(applicationContext, BuildConfig.BUGLY_APP_ID, BuildConfig.DEBUG, strategy)

        if (BuildConfig.DEBUG) {
            CrashReport.setUserSceneTag(applicationContext, 83913)
        }
    }
}