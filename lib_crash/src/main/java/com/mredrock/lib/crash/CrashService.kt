package com.mredrock.lib.crash

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.api.crash.CRASH_SERVICE
import com.mredrock.api.crash.ICrashService
import com.mredrock.lib.crash.core.CyxbsCrashMonitor

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/13
 * @Description:
 */
@Route(path = CRASH_SERVICE, name = CRASH_SERVICE)
class CrashService:ICrashService {
    override fun initCrashMonitor(application: Application) {
        CyxbsCrashMonitor.install(application)
    }

    override fun init(context: Context) {}
}