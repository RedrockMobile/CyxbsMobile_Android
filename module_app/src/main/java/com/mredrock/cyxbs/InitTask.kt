package com.mredrock.cyxbs

import android.content.Context
import android.os.Process
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.common.utils.getProcessName
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport

/**
 * Created by Jovines on 2020/06/09 15:18
 * description : 只需要在app模块里面初始化的task
 */

// 子模块debug不需要bugly
fun initBugly(context: Context): () -> Unit = {
    val packageName = context.packageName
    val processName = getProcessName(Process.myPid())
    val strategy = CrashReport.UserStrategy(context)
    strategy.appVersion = getAppVersionName(context)
    strategy.isUploadProcess = processName == null || processName == packageName
    strategy.appChannel = WalleChannelReader.getChannel(context, "debug")
    Bugly.init(context, BuildConfig.BUGLY_APP_ID, false, strategy)
    if (BuildConfig.DEBUG) {
        CrashReport.setUserSceneTag(context, 83913)
    } else {
        CrashReport.setUserSceneTag(context, 202291)
    }
}