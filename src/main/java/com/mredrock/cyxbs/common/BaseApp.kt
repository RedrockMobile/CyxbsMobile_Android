package com.mredrock.cyxbs.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.config.SP_KEY_USER
import com.mredrock.cyxbs.common.utils.CrashHandler
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.encrypt.UserInfoEncryption
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
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

        var user: User? = null
            set(value) {
                field = value
                val encryptedJson = userInfoEncryption.encrypt(value?.toJson())
                context.defaultSharedPreferences.editor {
                    putString(SP_KEY_USER, encryptedJson)
                }
            }
            get() {
                if (field == null) {
                    val encryptedJson = context.defaultSharedPreferences.getString(SP_KEY_USER, "")
                    val json = userInfoEncryption.decrypt(encryptedJson)
                    LogUtils.d("userinfo", json)
                    try {
                        field = Gson().fromJson(json, User::class.java)
                    } catch (e: Throwable) {
                        LogUtils.d("userinfo", "parse user json failed")
                    }
                }
                return field
            }

        val isLogin get() = (user != null)
        val hasNickname get() = (user != null && user?.nickname != null)

        private lateinit var userInfoEncryption: UserInfoEncryption
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
    }

    override fun onCreate() {
        super.onCreate()
        CrashHandler.init(applicationContext)
        userInfoEncryption = UserInfoEncryption()
        initRouter()
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
        val channel = WalleChannelReader.getChannel(this, "debug")
        UMConfigure.init(applicationContext, BuildConfig.UM_APP_KEY, channel, UMConfigure.DEVICE_TYPE_PHONE,
                BuildConfig.UM_PUSH_SECRET)
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL)
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
                Log.i("heiheihei", "注册成功：deviceToken：-------->  $deviceToken")
            }
            override fun onFailure(s: String, s1: String) {
                Log.e("heiheihei", "注册失败：-------->  s:$s,s1:$s1")
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