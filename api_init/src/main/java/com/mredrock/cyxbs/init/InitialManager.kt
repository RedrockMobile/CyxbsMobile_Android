package com.mredrock.cyxbs.init

import android.app.Application

/**
 * @author ZhiQiang Tu
 * @time 2022/3/24  12:46
 * @signature 我将追寻并获取我想要的答案
 */
interface InitialManager {

    val application: Application

    val currentProcessName: String

    val isMainProcess: Boolean
        get() = currentProcessName == application.packageName
}