package com.mredrock.cyxbs.init

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BuildConfig

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/4 18:49
 */
object InitARouter : IInit {
    override fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(application)
    }
}