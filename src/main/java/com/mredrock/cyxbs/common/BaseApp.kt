package com.mredrock.cyxbs.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.inapp.InAppMessageManager
import com.umeng.socialize.PlatformConfig


/**
 * Created By jay68 on 2018/8/7.
 */
open class BaseApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak", "CI_StaticFieldLeak")
        lateinit var context: Context
            private set

        @Deprecated(message = "已废弃该实现，请使用IAccountService", replaceWith = ReplaceWith("ServiceManager.getService(IAccountService::class.java).getUserService()", "com.mredrock.cyxbs.common.service.ServiceManager", "com.mredrock.cyxbs.common.service.account.IAccountService"), level = DeprecationLevel.WARNING)
        var user: User = User()

        @Deprecated(message = "已废弃该实现，请使用IAccountService", replaceWith = ReplaceWith("ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()", "com.mredrock.cyxbs.common.service.ServiceManager", "com.mredrock.cyxbs.common.service.account.IAccountService"), level = DeprecationLevel.WARNING)
        val isLogin
            get() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()

        var startTime: Long = 0
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
        startTime = System.currentTimeMillis()
    }

    override fun onCreate() {
        super.onCreate()
        BaseAppInitService.init(applicationContext)
        initRouter()//ARouter放在子线程会影响使用
        initUMeng()
    }

    private fun initRouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(this)
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
        initShare()
    }

    private fun initShare() {
        PlatformConfig.setSinaWeibo(BuildConfig.UM_SHARE_SINA_APP_KEY, BuildConfig.UM_SHARE_SINA_APP_SECRET, "http://hongyan.cqupt.edu.cn/app/")
        PlatformConfig.setQQZone(BuildConfig.UM_SHARE_QQ_ZONE_APP_ID, BuildConfig.UM_SHARE_QQ_ZONE_APP_SECRET)
    }
}