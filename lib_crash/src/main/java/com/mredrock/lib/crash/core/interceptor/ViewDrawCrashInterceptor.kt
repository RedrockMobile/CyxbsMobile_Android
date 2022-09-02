package com.mredrock.lib.crash.core.interceptor

import android.util.Log
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.lib.crash.util.ErrorDialog
import com.mredrock.lib.crash.util.collectCrashInfo
import com.mredrock.lib.crash.util.isViewDrawException
import com.mredrock.lib.crash.util.reStartApp

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/8
 * @Description: view绘制异常的拦截器，包括onMeasure,onLayout,onDraw和RV的onBindViewHolder的异常
 */
class ViewDrawCrashInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        val realChain = chain as RealInterceptChain
        val isHandled =
            if (isViewDrawException(realChain.e)) {
                Log.e("RQ", "intercept: 捕获view绘制异常：${collectCrashInfo(realChain.e)}")
                //如果是最后一个Activity直接重启
                if (realChain.activities.size == 1)
                    if (!reStartApp("最后的activity的view绘制或Rv异常！", realChain.e))
                        realChain.activities.forEach { it.finish() }
                else realChain.activities.last().finish()//不finish掉这个activity会黑屏
                ErrorDialog.showCrashDialog(realChain.activities.last(),"view绘制或Rv异常！", collectCrashInfo(realChain.e))
                true
            } else realChain.proceed()
        return isHandled
    }
}