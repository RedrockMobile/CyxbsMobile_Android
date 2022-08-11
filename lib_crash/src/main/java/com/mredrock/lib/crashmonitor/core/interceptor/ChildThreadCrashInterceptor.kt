package com.mredrock.lib.crashmonitor.core.interceptor

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.lib.crashmonitor.util.collectCrashInfo


/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description:  子线程异常的拦截器
 */
class ChildThreadCrashInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        val realChain = chain as RealInterceptChain
        val isHandled =
            if (realChain.t != null && realChain.t.name != "main") {
                Log.e("RQ", "intercept: 捕获了子线程异常：${collectCrashInfo(realChain.e)}")
                Handler(Looper.getMainLooper()).post {//这里用Handler弹toast是防止该方法不在主线程执行
                    toast("子线程异常！")
                }
            } else realChain.proceed()

        return isHandled
    }

}