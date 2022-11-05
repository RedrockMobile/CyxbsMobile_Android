package com.mredrock.cyxbs.common

import android.app.Application
import android.content.Context

/**
 * Created By jay68 on 2018/8/7.
 */
@Deprecated("common 模块的 BaseApp 已被废弃，请使用最新的 lib_base 模块")
object BaseApp {
    lateinit var appContext: Context
        private set
    
    lateinit var version: String
        private set
    
    /**
     * 因为 lib_common 模块已经被废弃，所以为了兼容以前的代码，所以设置了 onCreate 来初始化 Application 和一些其他东西
     */
    fun onCreate(app: Application) {
        appContext = app
        appContext.packageManager.getPackageInfo(appContext.packageName, 0).let {
            version = it.versionName
        }
    }
}