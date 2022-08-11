package com.mredrock.lib.crashmonitor.core.interceptor

import android.util.Log
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.lib.crashmonitor.util.collectCrashInfo
import com.mredrock.lib.crashmonitor.util.isViewDrawException

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description:  普通message异常的拦截器，比如点击事件异常
 */
class CommonMessageCrashInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        val realChain = chain as RealInterceptChain
        val isHandled =
            if (realChain.message == null && realChain.t == null && !isViewDrawException(realChain.e)) {
                Log.e("RQ", "intercept: 捕获了Message异常：${collectCrashInfo(realChain.e)}")
                toast("普通Message异常！")
                true
            } else realChain.proceed()

        return isHandled
    }
}