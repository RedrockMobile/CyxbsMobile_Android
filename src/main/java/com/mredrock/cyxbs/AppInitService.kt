package com.mredrock.cyxbs


import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.multidex.MultiDex
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.utils.CrashHandler
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.common.utils.getProcessName
import com.taobao.sophix.SophixManager
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import java.lang.RuntimeException

/**
 * author: Fxymine4ever
 * time: 2019/9/28
 */

class AppInitService : IntentService("AppInitService") {

    companion object{
        const val ACTION_INIT_WHEN_APP_CREATE = "service.action.INIT"

        fun init(context: Context){
            val intent = Intent(context,AppInitService::class.java)
            intent.action = ACTION_INIT_WHEN_APP_CREATE
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            val action = intent.action
            if(action == ACTION_INIT_WHEN_APP_CREATE){
                startInit()
            }
        }
    }

    private fun startInit(){
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
        Bugly.init(applicationContext, com.mredrock.cyxbs.BuildConfig.BUGLY_APP_ID, false, strategy)
        if (com.mredrock.cyxbs.BuildConfig.DEBUG) {
            CrashReport.setUserSceneTag(applicationContext, 83913)
        }
    }
}