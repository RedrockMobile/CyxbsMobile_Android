package com.mredrock.cyxbs.init

import android.app.Application


/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/4 18:50
 */
interface SdkInitializer {
    fun init(application: Application)
}