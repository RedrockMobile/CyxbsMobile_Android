package com.mredrock.cyxbs.common

import android.content.Context
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService

/**
 * Created By jay68 on 2018/8/7.
 */
@Deprecated("common 模块的 BaseApp 已被废弃，请使用最新的 lib_base 模块")
@AutoService(InitialService::class)
class BaseApp : InitialService {
    
    companion object {
        lateinit var appContext: Context
            private set
    
        lateinit var version: String
            private set
    }
    
    /**
     * 因为 lib_common 模块已经被废弃，所以为了兼容以前的代码，这里采用依赖注入来初始化 Application
     */
    override fun onAllProcess(manager: InitialManager) {
        appContext = manager.application
        appContext.packageManager.getPackageInfo(appContext.packageName, 0).let {
            version = it.versionName
        }
    }
}