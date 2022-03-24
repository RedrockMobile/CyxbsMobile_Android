package com.mredrock.cyxbs.spi

import android.app.Application
import com.mredrock.cyxbs.BuildConfig

/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  12:46
 *@signature 我将追寻并获取我想要的答案
 */
interface SdkManager {
    val application:Application

    fun isMainProcess():Boolean
    fun applicationId() = BuildConfig.APPLICATION_ID
    fun applicationVersion() = BuildConfig.VERSION_NAME
    fun applicationCode () = BuildConfig.VERSION_CODE
}