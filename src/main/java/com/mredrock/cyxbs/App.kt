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
        AppInitService.init(this)
    }

}