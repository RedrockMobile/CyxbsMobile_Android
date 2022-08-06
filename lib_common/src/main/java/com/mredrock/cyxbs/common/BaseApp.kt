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
    
    lateinit var privacyDenied: () -> Unit
        private set
    
    lateinit var privacyAgree: () -> Unit
        private set
    
    /**
     * 因为 lib_common 模块以及被废弃，所以为了兼容以前的代码，所以设置了 onCreate 来初始化 Application 和一些其他东西
     *
     * @param privacyAgree 同意用户隐私政策
     * @param privacyDenied 不同于用户隐私政策
     */
    fun onCreate(app: Application, privacyDenied: () -> Unit, privacyAgree: () -> Unit) {
        appContext = app
        appContext.packageManager.getPackageInfo(appContext.packageName, 0).let {
            version = it.versionName
        }
        this.privacyDenied = privacyDenied
        this.privacyAgree = privacyAgree
    }
}