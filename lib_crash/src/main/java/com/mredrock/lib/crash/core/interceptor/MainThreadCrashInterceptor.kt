package com.mredrock.lib.crash.core.interceptor

import android.util.Log
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.lib.crash.util.ErrorDialog
import com.mredrock.lib.crash.util.collectCrashInfo
import com.mredrock.lib.crash.util.reStartApp

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
            if (realChain.t != null && realChain.t?.name == "main") {
                realChain.t = null
                val stackInfo = collectCrashInfo(realChain.e)
                Log.e("RQ", "intercept: 捕获了主线程异常：$stackInfo")
                toast("主线程异常！")
                if (!reStartApp("主线程异常！", realChain.e)) {
                    realChain.activities.forEach { it.finish() }
                }//直接重启应用
                ErrorDialog.showCrashDialog(realChain.activities.last(), "主线程异常！", stackInfo)
                true
            } else realChain.proceed()
        return isHandled
    }
}