package com.mredrock.cyxbs.init.crash

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.ui.ExceptionActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.init.IInit
import java.io.PrintWriter
import java.io.StringWriter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/4 21:39
 */
@SuppressLint("StaticFieldLeak")
object InitCrash : IInit, Thread.UncaughtExceptionHandler {
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private lateinit var context: Context
    private val deviceInfoMap = HashMap<String, String>()

    var stackInfo: String? = null
    val deviceInfo: String
        get() {
            val sb = StringBuilder()
            for ((key, value) in deviceInfoMap) {
                sb.appendln("$key=$value")
            }
            return sb.toString()
        }

    override fun init(application: Application) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        if (defaultHandler == this) {
            return
        }
        this.context = application
        this.defaultHandler = defaultHandler
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(p0: Thread?, p1: Throwable?) {
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler)
        handleException(p0?.name ?: "", p1)
        defaultHandler?.uncaughtException(p0, p1)
        System.exit(0)
    }

    private fun handleException(threadName: String, throwable: Throwable?) {
        if (throwable == null) {
            return
        }
        collectCrashInfo(throwable)
        collectDeviceInfo(context)

        LogUtils.e(javaClass.simpleName, "Exception in thread [$threadName]:")
        LogUtils.e(javaClass.simpleName, "deviceInfo:\n$deviceInfo")
        LogUtils.e(javaClass.simpleName, "stackInfo:\n$stackInfo")
        if (BuildConfig.DEBUG) {
            ExceptionActivity.start(context, stackInfo ?: "", deviceInfo)
        }
        return
    }

    private fun collectDeviceInfo(context: Context) {
        try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            packageInfo?.apply {
                deviceInfoMap["versionName"] = versionName ?: "null"
                deviceInfoMap["versionCode"] = "$versionCode"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val fields = Build::class.java.declaredFields
        fields.forEach { field ->
            try {
                field.isAccessible = true
                deviceInfoMap[field.name] = field.get(null).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun collectCrashInfo(throwable: Throwable) {
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        throwable.printStackTrace(printWriter)
        var cause = throwable.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        stackInfo = writer.toString()
    }
}