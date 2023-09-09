package com.mredrock.lib.crash.core

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.*
import com.google.gson.Gson
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.utils.LogLocal
import com.mredrock.lib.crash.core.interceptor.*
import com.mredrock.lib.crash.util.noOpDelegate
import com.mredrock.lib.crash.util.reStartApp
import com.tencent.bugly.crashreport.CrashReport
import java.lang.reflect.Field

/**
 *
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/5
 * @Description: 全局Crash监控
 * 发生异常的种类(如果不全请补充)：
 * 1.handler处理普通的message异常，例如onClick ----不finish掉activity，屏蔽异常
 * 2.子线程异常 ----不finish掉activity，屏蔽异常
 * 3.Activity生命周期异常，例如在onCreate的里面发生异常，此类异常和绘制异常不销毁activity都会导致黑屏  ----finish掉activity，屏蔽异常
 * 4.view绘制异常导致ViewRootImpl挂掉或者Rv的onCreateViewHolder和onBindViewHolder异常   ----finish掉activity，屏蔽异常
 * 5.主线程异常也就是UI线程，直接重启app
 * 6.如果出现了新的异常类型请补充，并给出解决方案添加在拦截链里面
 */

object CyxbsCrashMonitor : Thread.UncaughtExceptionHandler {

    /**
     * 如果后人发现了没有囊括的异常类型，请将新的拦截链添加在此处，添加顺序决定优先级。
     */
    private val newInterceptors = mutableListOf<Interceptor>(

    )

    //已启动的activity
    private val activities = mutableListOf<Activity>()

    /**
     *
     * 在这里此变量用于记录activity在被finish的时候是否移出了activities，因为在拦截链用反射销毁生命周期异常的activity的
     * 时候，是不会走下面注册的监听的onActivityDestroyed方法的，但是onCreate异常是会走onActivityCreated方法的
     * 所以在此用个变量记录生命周期异常时是否已移出activities
     */
    private var isActivityRemovedAfterFinished = false

    //记录上一次捕捉异常的时间
    private var lastExceptionTime = 0L

    //默认异常处理拦截链
    private val interceptors = mutableListOf(//请不要随意修改添加顺序，顺序决定异常处理的优先级
        CommonMessageCrashInterceptor(),
        ChildThreadCrashInterceptor(),
        LifecycleCrashInterceptor(),
        ViewDrawCrashInterceptor(),
        MainThreadCrashInterceptor()
    )

    fun install(application: Application) {
        initListener(application)
        //在完全启动之后才检查
        Handler(Looper.getMainLooper()).post {
            checkIfHasCrash()
        }
    }

    /**
     * 检查是否是因为异常而重启
     */
    private fun checkIfHasCrash() {
        val sp = appContext.getSp("crashMonitor")
        val stackInfo = sp.getString("throwable", "")
        val reason = sp.getString("reason", "")
        if (stackInfo != null && stackInfo != "" && reason != null && reason != "") {
            //因为异常重启上报一次异常，因为重启之前来不及上传异常应用进程已经结束了
            val e = Gson().fromJson(stackInfo, Throwable::class.java)
            CrashReport.postCatchedException(e)
            LogLocal.log(
                tag = "CrashMonitor",
                msg = "异常拦截链异常!!!",
                e
            )
        }
        sp.edit().run {
            putString("throwable", "")
            putString("reason", "")
            apply()
        }//用完清空
    }

    private fun initListener(application: Application) {
        //接管其他线程的异常监听
        Thread.setDefaultUncaughtExceptionHandler(this)
        //记录activity的启动
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks by noOpDelegate() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activities.contains(activity)) {
                    activities.remove(activity)
                }
                activities.add(activity)
                isActivityRemovedAfterFinished = false
            }

            override fun onActivityDestroyed(activity: Activity) {
                activities.remove(activity)
                isActivityRemovedAfterFinished = true
            }
        })
        //接管activity生命周期异常
        initActivityLifecycleCrash()

    }

    /**
     * 线程的异常会走这里
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        //再次try-catch防止异常责任链异常，导致进入死循环
        try {
            handleException(t, e)
        } catch (e: Throwable) {
            CrashReport.postCatchedException(e)
            LogLocal.log(
                tag = "CrashMonitor",
                msg = "异常拦截链异常!!!",
                e
            )
            //防止继续递归调用
            return
        }
        //子线程可能会出现Looper为null情况
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
        try {
            //兜底保持线程运行
            Looper.loop()
        } catch (e: Throwable) {
            val currentTime = System.currentTimeMillis()
            //设定一秒只内只处理一个错误，如果一秒内发送了多个错误将重启
            if (lastExceptionTime != 0L && currentTime - lastExceptionTime < 1000) {
                if (!reStartApp("普通Message错误太多", e)) {
                    activities.forEach { it.finish() }
                }
            } else lastExceptionTime = currentTime
            //捕捉的异常
            uncaughtException(t, e)
        }
    }

    /**
     * 处理异常，使用拦截链模式
     */
    private fun handleException(t: Thread? = null, e: Throwable, message: Message? = null) {
        //检测到异常上报bugly并保存日志
        CrashReport.postCatchedException(e)
        LogLocal.log(
            tag = "CrashMonitor",
            msg = "CrashMonitor检测到异常",
            e
        )

        val realChain = RealInterceptChain(
            interceptors.apply { addAll(newInterceptors) },
            activities = activities,
            t = t, e = e, message = message
        )
        if (!realChain.proceed()) {
            toast("未知异常！！！")
        }
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
     * 替换不同版本的mH.mCallback，我们只是try catch，不做其他事情
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
                        if (!isActivityRemovedAfterFinished) {
                            activities.removeLast()
                            isActivityRemovedAfterFinished = true
                        }
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
                        if (!isActivityRemovedAfterFinished) {
                            activities.removeLast()
                            isActivityRemovedAfterFinished = true
                        }
                    }
                    return@Callback true
                }
            }
            false
        })
    }
}