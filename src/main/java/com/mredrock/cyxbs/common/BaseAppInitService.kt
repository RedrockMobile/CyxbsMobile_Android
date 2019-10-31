package com.mredrock.cyxbs.common

import android.app.IntentService
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import com.alibaba.android.arouter.launcher.ARouter
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.utils.CrashHandler
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig

/**
 * author: Fxymine4ever
 * time: 2019/9/28
 */

class BaseAppInitService : IntentService("BaseAppInitService") {

    companion object{
        const val ACTION_INIT_WHEN_APP_CREATE = "service.action.BASE_INIT"

        fun init(context: Context){
            val intent = Intent(context, BaseAppInitService.javaClass)
            intent.action = ACTION_INIT_WHEN_APP_CREATE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(Int.MAX_VALUE, Notification())
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
        CrashHandler.init(applicationContext)
//        initUMeng()
    }


}