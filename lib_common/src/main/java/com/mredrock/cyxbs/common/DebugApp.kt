package com.mredrock.cyxbs.common

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

/**
 *@author ZhiQiang Tu
 *@time 2022/4/2  0:31
 *@signature 我将追寻并获取我想要的答案
 */

/**
 * 创建这个Application的理由很是无奈，所有模块和ARouter已经高度耦合，而sdk的接入目前是全部写在module_app模块。
 * 而单模块调试时需要初始化ARouter的。罢了就初始化一下吧。
 * 值得注意的时这个Application只使用于单模块调试。所以不比担心其他。
 */
class DebugApp : Application() {
    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(this)
        super.onCreate()
    }
}