package com.mredrock.cyxbs.common.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * getAppVersionCode
 */
@Deprecated("使用 lib_utils 中的 getAppVersionName() 代替")
fun getAppVersionName(context: Context): String? {
    return try {
        val packageManager = context.packageManager
        val info = packageManager.getPackageInfo(context.packageName, 0)
        info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}