package com.mredrock.cyxbs.common

import android.app.IntentService
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.LogUtils
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.inapp.InAppMessageManager


/**
 * @author  Jon
 * @date  2020/4/6 14:05
 * description：用来做一些初始化一些所有模块都需要的第三方SDK
 */
class InitService : IntentService("BaseAppInitService") {

    companion object {
        const val ACTION_INIT_WHEN_APP_CREATE = "service.action.BASE_INIT"

        fun init(context: Context) {
            val intent = Intent(context, InitService::class.java)
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
            NotificationManagerCompat.from(this).notify(Int.MAX_VALUE, Notification.Builder(context, BaseApp.foregroundService).setSmallIcon(R.drawable.common_app_logo).build())
            startForeground(Int.MAX_VALUE, Notification.Builder(context, BaseApp.foregroundService).build())
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            val action = intent.action
            if (action == ACTION_INIT_WHEN_APP_CREATE) {
                initUMeng()
            }
        }
    }


    private fun initUMeng() {
        val channel = WalleChannelReader.getChannel(applicationContext, "debug")
        UMConfigure.init(applicationContext, BuildConfig.UM_APP_KEY, channel, UMConfigure.DEVICE_TYPE_PHONE,
                BuildConfig.UM_PUSH_SECRET)
        MobclickAgent.setScenarioType(applicationContext, MobclickAgent.EScenarioType.E_UM_NORMAL)
        MobclickAgent.openActivityDurationTrack(false)
        //调试模式（推荐到umeng注册测试机，避免数据污染）
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        //友盟推送服务的接入
        PushAgent.getInstance(context).onAppStart()
        val mPushAgent = PushAgent.getInstance(this)
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object : IUmengRegisterCallback {
            override fun onSuccess(deviceToken: String) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                LogUtils.i("友盟注册", "注册成功：deviceToken：-------->  $deviceToken")
            }

            override fun onFailure(s: String, s1: String) {
                LogUtils.e("友盟注册", "注册失败：-------->  s:$s,s1:$s1")
            }
        })

        InAppMessageManager.getInstance(context).setInAppMsgDebugMode(true)
    }
}