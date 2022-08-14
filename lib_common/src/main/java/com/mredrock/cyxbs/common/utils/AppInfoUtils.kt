package com.mredrock.cyxbs.common.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * getAppVersionCode
 */
fun getAppVersionCode(context: Context): Long {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> packageInfo.longVersionCode
            else -> packageInfo.versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        0
    }
}

fun getAppVersionName(context: Context): String? {
    return try {
        val packageManager = context.packageManager
        val info = packageManager.getPackageInfo(context.packageName, 0)
        info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}