package com.mredrock.cyxbs.common

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.config.SP_KEY_USER
import com.mredrock.cyxbs.common.utils.CrashHandler
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.encrypt.UserInfoEncryption
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig

/**
 * Created By jay68 on 2018/8/7.
 */
open class BaseApp : MultiDexApplication() {
    companion object {
        @SuppressLint("StaticFieldLeak")
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
        UMConfigure.init(applicationContext, UMConfigure.DEVICE_TYPE_PHONE,
                "123b419248120b9fb91a38260a13e972")
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL)
        MobclickAgent.openActivityDurationTrack(false)
        //调试模式（推荐到umeng注册测试机，避免数据污染）
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)

        initShare()
    }

    private fun initShare() {
        PlatformConfig.setSinaWeibo("197363903", "7700116c567ab2bb28ffec2dcf67851d", "http://hongyan.cqupt.edu.cn/app/")
        PlatformConfig.setQQZone("1106072365", "v9w1F3OSDhkX14gA")
    }
}