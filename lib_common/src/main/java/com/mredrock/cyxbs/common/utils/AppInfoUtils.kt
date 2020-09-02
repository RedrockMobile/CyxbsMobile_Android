package com.mredrock.cyxbs.common.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


/**
 * 获取app名字、进程名字等信息
 * Created By jay68 on 2018/8/14.
 */

fun getProcessName(pid: Int): String? {
    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
        var processName = reader.readLine()
        if (!TextUtils.isEmpty(processName)) {
            processName = processName.trim { it <= ' ' }
        }
        return processName
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
    } finally {
        try {
            reader?.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

    }
    return null
}

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