package com.mredrock.cyxbs.init

import android.app.Application
import android.os.Process
import com.mredrock.cyxbs.BuildConfig
//import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.common.utils.getProcessName
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport

/**
 * 配置的话可以先看看官方文档：https://bugly.qq.com/docs/
 * 官方文档讲的有点不详细，可以看看这篇文章：https://blog.csdn.net/RungBy/article/details/88794875
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/4 22:49
 */
object BuglyInitializer : SdkInitializer {
    override fun init(application: Application) {
        val packageName = application.packageName
        val processName = getProcessName(Process.myPid())
        val strategy = CrashReport.UserStrategy(application)
        strategy.appVersion = getAppVersionName(application)
        strategy.isUploadProcess = processName == null || processName == packageName
        //strategy.appChannel = WalleChannelReader.getChannel(application, "debug")
        //CrashReport.setUserId(ServiceManager.getService<IAccountService>().getUserService().getStuNum())
        Bugly.init(application, BuildConfig.BUGLY_APP_ID, false, strategy)
        if (BuildConfig.DEBUG) {
            CrashReport.setUserSceneTag(application, 83913)
        } else {
            CrashReport.setUserSceneTag(application, 202291)
        }
    }
}