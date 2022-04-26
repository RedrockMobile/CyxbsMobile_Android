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

    fun isMainProcess(): Boolean  = currentProcessName() == applicationId()

    fun currentProcessName(): String {
        val applicationContext = application.applicationContext
        val am = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        return am.runningAppProcesses
            .firstOrNull {
                it.pid == Process.myPid()
            }?.processName!!
    }


    fun applicationId() = BuildConfig.APPLICATION_ID
    fun applicationVersion() = BuildConfig.VERSION_NAME
    fun applicationCode() = BuildConfig.VERSION_CODE
}