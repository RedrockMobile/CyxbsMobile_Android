package com.mredrock.cyxbs.common

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.annotation.CallSuper
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Created By jay68 on 2018/8/7.
 */
abstract class BaseApp : Application() {
    companion object {
        lateinit var appContext: Context
            private set
        lateinit var application: BaseApp
            private set
        lateinit var version: String
            private set
    }

    abstract fun privacyAgree()

    abstract fun privacyDenied()


    @CallSuper
    override fun onCreate() {
        super.onCreate()
        appContext = this
        application = this
        appContext.packageManager.getPackageInfo(appContext.packageName, 0).let {
            version = it.versionName
        }
    }
}