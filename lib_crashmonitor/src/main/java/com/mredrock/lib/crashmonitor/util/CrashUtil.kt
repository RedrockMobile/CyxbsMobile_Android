package com.mredrock.lib.crashmonitor.util

import android.content.Intent
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.toast
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * 重启应用，所有拦截器都能使用的功能
 */
internal fun reStartApp(reason:String) {
    val intent: Intent =
        appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)!!
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)//清除任务栈
    appContext.startActivity(intent)
    toast(reason)
    exitProcess(0)
}

/**
 * 获取异常信息，所有拦截器都能使用的功能
 */
internal fun collectCrashInfo(throwable: Throwable): String {
    val writer = StringWriter()
    val printWriter = PrintWriter(writer)
    throwable.printStackTrace(printWriter)
    var cause = throwable.cause
    while (cause != null) {
        cause.printStackTrace(printWriter)
        cause = cause.cause
    }
    printWriter.close()
    writer.close()
    return writer.toString()
}

/**
 * RV的onBindView 和 view measure layout draw时抛出异常不处理会导致屏幕黑屏
 * 我将此类异常统一归为view 的绘制异常，即ViewDrawCrash
 * 建议直接只关闭黑屏的Activity
 */
internal fun isViewDrawException(e: Throwable): Boolean {
    val elements = e.stackTrace ?: return false
    for (i in elements.indices.reversed()) {
        if (elements.size - i > 20) {//从Zygote的main方法开始遍历，超过20个方法栈异常那肯定不是Choreographer挂掉了
            return false
        }
        val element = elements[i]
        if (("android.view.Choreographer" == element.className &&//前面是view的三大方法发生异常
                    "Choreographer.java" == element.fileName &&
                    "doFrame" == element.methodName) ||
            ("androidx.recyclerview.widget.GapWorker" == element.className &&//后面是rv的异常
                    "GapWorker.java" == element.fileName &&
                    "run" == element.methodName)
        ) {
            return true
        }
    }
    return false
}