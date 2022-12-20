package com.mredrock.cyxbs.lib.utils.utils.get

import android.content.pm.PackageManager
import android.os.Build
import com.mredrock.cyxbs.lib.utils.extensions.appContext

/**
 * getAppVersionCode
 */
fun getAppVersionCode(): Long {
    return try {
        val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> packageInfo.longVersionCode
            else -> packageInfo.versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        0
    }
}

fun getAppVersionName(): String? {
    return try {
        val packageManager = appContext.packageManager
        val info = packageManager.getPackageInfo(appContext.packageName, 0)
        info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}