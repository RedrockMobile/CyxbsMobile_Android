package com.mredrock.cyxbs.lib.utils.utils.get

import com.mredrock.cyxbs.lib.utils.BuildConfig

/**
 * 获取版本号
 */
fun getAppVersionCode(): Long {
    return BuildConfig.VERSION_CODE
}

/**
 * 获取版本名字
 */
fun getAppVersionName(): String {
    return BuildConfig.VERSION_NAME
}