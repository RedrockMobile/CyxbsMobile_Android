package com.mredrock.cyxbs.spi

import android.app.ActivityManager
import android.app.Application
import android.os.Process
import com.mredrock.cyxbs.BuildConfig
import com.mredrock.cyxbs.common.utils.getProcessName

/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  12:46
 *@signature 我将追寻并获取我想要的答案
 */
interface SdkManager {
    val application: Application
    //currentProcessName中还包含部分不可见字符。所有不可使用equals
    fun isMainProcess(): Boolean  = currentProcessName().contains(applicationId())

    fun currentProcessName(): String {
        //不允许通过getRunningAppProcess获取当前线程号，因为有可能触发隐私条例
        /*val applicationContext = application.applicationContext
        val am = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        am.runningAppProcesses
            .firstOrNull {
                it.pid == Process.myPid()
            }?.processName!!*/
        return getProcessName(Process.myPid()) ?: ""
    }


    fun applicationId() = BuildConfig.APPLICATION_ID
    fun applicationVersion() = BuildConfig.VERSION_NAME
    fun applicationCode() = BuildConfig.VERSION_CODE
}