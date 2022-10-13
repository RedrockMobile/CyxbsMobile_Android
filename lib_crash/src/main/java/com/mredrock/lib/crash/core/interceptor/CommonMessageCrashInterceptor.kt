package com.mredrock.lib.crash.core.interceptor

import com.mredrock.lib.crash.util.isViewDrawException

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
                // 普通Message异常！
                true
            } else realChain.proceed()

        return isHandled
    }
}