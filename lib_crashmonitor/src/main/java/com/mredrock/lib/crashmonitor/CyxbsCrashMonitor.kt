package com.mredrock.lib.crashmonitor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.utils.LogLocal
import com.mredrock.lib.crashmonitor.interceptor.*
import com.mredrock.lib.crashmonitor.util.noOpDelegate
import com.mredrock.lib.crashmonitor.util.reStartApp
import com.tencent.bugly.crashreport.CrashReport
import java.lang.reflect.Field


/**
 *
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/5
 * @Description: 全局Crash监控
 * 发生异常的种类(如果不全请补充)：
 * 1.handler处理普通的message异常，例如onClick ----不finish掉activity，弹窗提示
 * 2.子线程异常 ----不finish掉activity，弹窗提示
 * 3.Activity生命周期异常，例如在onCreate的里面发生异常  ----finish掉activity，弹窗提示
 * 4.view绘制异常导致ViewRootImpl挂掉或者Rv的onCreateViewHolder和onBindViewHolder异常   ----finish掉activity，弹窗提示
 * 5.主线程异常也就是UI线程，直接重启app ----弹窗提示
 */
object CyxbsCrashMonitor : Thread.UncaughtExceptionHandler {
    private val activities = mutableListOf<Activity>()
    private lateinit var mainThread: Thread
    fun install(application: Application) {
        mainThread = Thread.currentThread()
        initListener(application)
    }

    private fun initListener(application: Application) {
        //接管其他线程的异常监听
        Thread.setDefaultUncaughtExceptionHandler(this)
        //记录activity的启动
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks by noOpDelegate() {
            override fun onActivityResumed(activity: Activity) {//显示的activity才会记录，没显示过肯定有问题
                if (activities.contains(activity)) {
                    activities.remove(activity)
                }
                activities.add(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                activities.remove(activity)
            }
        })
        //接管activity生命周期异常
        initActivityLifecycleCrash()
        //用于保存时间
        var lastExceptionTime = 0L
        // loop 的第一次报错上传至 bugly
        var isFirstError = true
        //接管message异常的监听
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    if (isFirstError) {
                        CrashReport.postCatchedException(e)
                        LogLocal.log(
                            tag = "CrashMonitor",
                            msg = "第一次 loop 报错",
                            e
                        )
                        isFirstError = false
                    }
                    val currentTime = System.currentTimeMillis()
                    //设定一秒只内只处理一个错误，如果一秒内发送了多个错误将重启
                    if (lastExceptionTime != 0L && currentTime - lastExceptionTime < 1000) {
                        reStartApp("哇~短时间收到的错误太多！重启！")
                    } else lastExceptionTime = currentTime
                    try {//这里再抓取处理异常可能发生的异常，防止重复调用进入死循环
                        handleException(e = e)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * 线程的异常会走这里
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        if (t != Looper.getMainLooper().thread) {
            CrashReport.postCatchedException(e)
            LogLocal.log(
                tag = "CrashMonitor",
                msg = "非主线程异常",
                e
            )
        }
        handleException(t, e)
    }

    /**
     * 处理异常，使用拦截链模式
     */
    private fun handleException(t: Thread? = null, e: Throwable, message: Message? = null) {
        val interceptors = mutableListOf(//这可添加自定义拦截器
            CommonMessageCrashInterceptor(),
            ChildThreadCrashInterceptor(),
            LifecycleCrashInterceptor(),
            ViewDrawCrashInterceptor(),
            MainThreadCrashInterceptor()
        )
        val realChain = RealInterceptChain(
            interceptors,
            activities = activities,
            t = t, e = e, message = message
        )
        if (!realChain.proceed())
            toast("未知异常！！！")
    }

    /**
     * 替换ActivityThread.mH.mCallback，实现try catch Activity生命周期，直接忽略生命周期的异常的话会导致黑屏，目前
     * 会调用ActivityManager的finishActivity结束掉生命周期抛出异常的Activity
     */
    private fun initActivityLifecycleCrash() {
        try {
            hookmH()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 替换不同版本的mH.mCallback，反射会爆黄，它提醒我们会影响发布，但我们只是try catch，不做其他事情
     */
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun hookmH() {
        //ActivityThread里面启动activity的一些启动码
        val LAUNCH_ACTIVITY = 100
        val PAUSE_ACTIVITY = 101
        val PAUSE_ACTIVITY_FINISHING = 102
        val STOP_ACTIVITY_HIDE = 104
        val RESUME_ACTIVITY = 107
        val DESTROY_ACTIVITY = 109
        val NEW_INTENT = 112
        val RELAUNCH_ACTIVITY = 126
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val activityThread =
            activityThreadClass.getDeclaredMethod("currentActivityThread").invoke(null)
        val mhField: Field = activityThreadClass.getDeclaredField("mH")
        mhField.isAccessible = true
        val mhHandler = mhField.get(activityThread) as Handler
        val callbackField: Field = Handler::class.java.getDeclaredField("mCallback")
        callbackField.isAccessible = true
        callbackField.set(mhHandler, Handler.Callback { msg ->
            if (Build.VERSION.SDK_INT >= 28) { //android P 生命周期全部走这
                val EXECUTE_TRANSACTION = 159
                if (msg.what == EXECUTE_TRANSACTION) {
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        handleException(e = throwable, message = msg)
                    }
                    return@Callback true
                }
                return@Callback false
            }
            //android P之前的版本
            when (msg.what) {
                LAUNCH_ACTIVITY, RESUME_ACTIVITY, PAUSE_ACTIVITY_FINISHING, PAUSE_ACTIVITY, STOP_ACTIVITY_HIDE, DESTROY_ACTIVITY, NEW_INTENT, RELAUNCH_ACTIVITY -> {
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        handleException(e = throwable, message = msg)
                    }
                    return@Callback true
                }
            }
            false
        })
    }
}