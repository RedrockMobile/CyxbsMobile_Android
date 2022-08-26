package com.mredrock.cyxbs.sdks

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.lib.utils.BuildConfig
import com.mredrock.cyxbs.ui.ExceptionActivity
import com.mredrock.cyxbs.lib.base.spi.InitialManager
import com.mredrock.cyxbs.lib.base.spi.InitialService
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/4 21:39
 */
@AutoService(InitialService::class)
class CrashInitialService : Thread.UncaughtExceptionHandler, InitialService {
    
    override fun onMainProcess(manager: InitialManager) {
        super.onMainProcess(manager)
        init(manager.application)
    }
    
    private fun init(application: Application) {
        // 针对于 debug 与 release 采用不同的崩溃处理方法
        
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        if (defaultHandler == this) {
            return
        }
        context = application
        this.defaultHandler = defaultHandler
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
    
    override fun uncaughtException(p0: Thread, p1: Throwable) {
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler)
        handleException(p0.name, p1)
        defaultHandler?.uncaughtException(p0, p1)
        exitProcess(0)
    }
    
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

    private fun handleException(threadName: String, throwable: Throwable?) {
        if (throwable == null) {
            return
        }
        collectCrashInfo(throwable)
        collectDeviceInfo(context)

//        LogUtils.e(javaClass.simpleName, "Exception in thread [$threadName]:")
//        LogUtils.e(javaClass.simpleName, "deviceInfo:\n$deviceInfo")
//        LogUtils.e(javaClass.simpleName, "stackInfo:\n$stackInfo")
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