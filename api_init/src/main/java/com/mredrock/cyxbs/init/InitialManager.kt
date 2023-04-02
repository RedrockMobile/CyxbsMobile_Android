package com.mredrock.cyxbs.init

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.os.Process

/**
 * @author ZhiQiang Tu
 * @time 2022/3/24  12:46
 * @signature 我将追寻并获取我想要的答案
 */
interface InitialManager {
    val application: Application
    fun isMainProcess(): Boolean  = currentProcessName()?.equals(applicationId()) ?: false

    fun currentProcessName(): String? {
        //不允许通过getRunningAppProcess获取当前线程号，因为有可能触发隐私条例
        val applicationContext = application.applicationContext
        val am = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        return am.runningAppProcesses
            .firstOrNull {
                it.pid == Process.myPid()
            }?.processName
    }


    fun applicationId() = application.packageName
    fun applicationVersion() = application.packageManager.getPackageInfo(application.packageName, 0).versionName
    fun applicationCode() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        application.packageManager.getPackageInfo(application.packageName, 0).longVersionCode
    } else {
        application.packageManager.getPackageInfo(application.packageName, 0).versionCode.toLong()
    }

}