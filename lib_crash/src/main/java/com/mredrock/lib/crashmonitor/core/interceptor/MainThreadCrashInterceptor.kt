package com.mredrock.lib.crashmonitor.core.interceptor

import android.util.Log
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.lib.crashmonitor.util.collectCrashInfo
import com.mredrock.lib.crashmonitor.util.reStartApp

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description: 主线程异常的拦截器
 */
class MainThreadCrashInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        val realChain = chain as RealInterceptChain
        val isHandled =
            if (realChain.t != null && realChain.t.name == "main") {
                Log.e("RQ", "intercept: 捕获了主线程异常：${collectCrashInfo(realChain.e)}")
                toast("主线程异常！")
                reStartApp("主线程异常！应用重启！")//直接重启应用
                true
            } else realChain.proceed()
        return isHandled
    }
}