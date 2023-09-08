package com.mredrock.lib.crash.util

import android.content.Intent
import com.google.gson.Gson
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * 重启应用，所有拦截器都能使用的功能
 */
internal fun reStartApp(reason: String, e: Throwable): Boolean {
    //这里用sp防止短时间内多次重启，短时间判定规则：
    //在3s内重启了2次
    val sp = appContext.getSp("crashMonitor")
    val time: Long
    val count: Int
    sp.run {
        time = getLong("time", 0)
        count = getInt("count", 0)
    }
    if (count > 2 && System.currentTimeMillis() - time < 3000) {//3s内重启三次以上
        sp.edit().run {
            putLong("time", 0)
            putInt("count", 0)
            commit()
        }
        //重启失败
        return false
    } else if (System.currentTimeMillis() - time > 3000) {//距离上一次重启时间超过3s，清空次数记录
        sp.edit().run {
            putLong("time", 0)
            putInt("count", 0)
            commit()
        }
    } else sp.edit().run {//记录重启时间和次数
        if (count == 0) putLong("time", System.currentTimeMillis())
        putInt("count", sp.getInt("count", 0) + 1)
        commit()
    }
    //记录因为异常而重启的相关信息
    sp.edit().run {
        putString("throwable", Gson().toJson(e))
        putString("reason", reason)
        commit()
    }
    val intent: Intent =
        appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)!!
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)//清除任务栈并启动新的任务栈即新进程
    appContext.startActivity(intent)
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
    var position = 1
    while (cause != null) {
        printWriter.println()
        printWriter.print("第 $position 个 Caused By: ")
        position++
        cause.printStackTrace(printWriter)
        cause = cause.cause
    }
    printWriter.close()
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

