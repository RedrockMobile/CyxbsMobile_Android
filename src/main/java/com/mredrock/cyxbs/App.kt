package com.mredrock.cyxbs

import android.os.Process
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.common.utils.getProcessName
import com.taobao.sophix.SophixManager
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport

/**
 * Created By jay68 on 2018/8/8.
 */
class App : BaseApp() {
    override fun onCreate() {
        super.onCreate()
        initBugly()

        SophixManager.getInstance().queryAndLoadNewPatch()
    }

    // 子模块debug不需要bugly
    private fun initBugly() {
        val packageName = applicationContext.packageName
        val processName = getProcessName(Process.myPid())
        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.appVersion = getAppVersionName(applicationContext)
        strategy.isUploadProcess = processName == null || processName == packageName
        strategy.appChannel = WalleChannelReader.getChannel(this, "debug")
        Bugly.init(applicationContext, BuildConfig.BUGLY_APP_ID, false, strategy)
        if (BuildConfig.DEBUG) {
            CrashReport.setUserSceneTag(applicationContext, 83913)
        }
    }
}