package com.mredrock.lib.crash.core.interceptor

import android.os.Build
import android.util.Log
import com.mredrock.lib.crash.core.activitykiller.ActivityKillerV21ToV23
import com.mredrock.lib.crash.core.activitykiller.ActivityKillerV24ToV25
import com.mredrock.lib.crash.core.activitykiller.ActivityKillerV26
import com.mredrock.lib.crash.core.activitykiller.ActivityKillerV28
import com.mredrock.lib.crash.util.ErrorDialog
import com.mredrock.lib.crash.util.collectCrashInfo
import com.mredrock.lib.crash.util.reStartApp

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description:  Activity生命周期异常拦截器
 */
class LifecycleCrashInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Boolean {
        val realChain = chain as RealInterceptChain
        val isHandled =
            if (realChain.message != null) {
                val stackInfo = collectCrashInfo(realChain.e)
                Log.e("RQ", "intercept: 捕获生命周期异常：$stackInfo")
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
                if (realChain.activities.isEmpty()) {
                    if (!reStartApp("最后的activity生命周期异常",realChain.e)) {
                        activityKiller?.finishActivity(realChain.message!!)
                        realChain.activities.forEach { it.finish() }
                    }
                }
                else {
                    activityKiller?.finishActivity(realChain.message!!)//此处finish是不会走ActivityLifecycleCallbacks的finish
                }
                realChain.message = null//用完就扔掉
                Log.d("RQ", "intercept: ${realChain.activities.last()}")
                ErrorDialog.showCrashDialog(realChain.activities.last(),"Activity生命周期异常！",stackInfo)
                true
            } else realChain.proceed()
        return isHandled
    }
}