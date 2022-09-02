package com.mredrock.api.crash

import android.app.Application
import com.alibaba.android.arouter.facade.template.IProvider
import java.security.Provider

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/13
 * @Description:
 */
interface ICrashService:IProvider {
    fun initCrashMonitor(application: Application)
}