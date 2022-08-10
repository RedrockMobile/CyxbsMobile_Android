package com.mredrock.lib.crashmonitor.interceptor

import android.os.Build
import android.util.Log
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.lib.crashmonitor.activitykiller.ActivityKillerV21ToV23
import com.mredrock.lib.crashmonitor.activitykiller.ActivityKillerV24ToV25
import com.mredrock.lib.crashmonitor.activitykiller.ActivityKillerV26
import com.mredrock.lib.crashmonitor.activitykiller.ActivityKillerV28
import com.mredrock.lib.crashmonitor.util.collectCrashInfo

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description:  Activity生命周期异常拦截器
 */
class LifecycleCrashInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        val realCain = chain as RealInterceptChain
        val isHandled =
            if (realCain.message != null) {
                Log.e("RQ", "intercept: 捕获生命周期异常：${collectCrashInfo(realCain.e)}")
                toast("Activity生命周期异常！")
                //各版本android的ActivityManager获取方式，finishActivity的参数，token(binder对象)的获取不一样
                val activityKiller = if (Build.VERSION.SDK_INT >= 28) {
                    ActivityKillerV28()
                } else if (Build.VERSION.SDK_INT >= 26) {
                    ActivityKillerV26()
                } else if (Build.VERSION.SDK_INT == 25 || Build.VERSION.SDK_INT == 24) {
                    ActivityKillerV24ToV25()
                } else if (Build.VERSION.SDK_INT <= 23) {
                    ActivityKillerV21ToV23()
                } else null
                activityKiller?.finishActivity(realCain.message)
                true
            } else realCain.proceed()
        return isHandled
    }
}