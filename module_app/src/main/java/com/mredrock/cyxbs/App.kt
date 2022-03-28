package com.mredrock.cyxbs

import android.app.ActivityManager
import android.app.Application
import android.os.Process
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.spi.SdkService
import com.mredrock.cyxbs.spi.SdkManager
import java.util.*

/**
 * Created By jay68 on 2018/8/8.
 */
class App : BaseApp(), SdkManager {

    private val loader = ServiceLoader.load(SdkService::class.java)

    override fun onCreate() {
        super.onCreate()

        loader.takeIf {
            isMainProcess()
        }?.forEach {
            it.onMainProcess(this)
        } ?: loader.forEach {
            it.onSdkProcess(this)
        }


        /*initTasks()
        //若以后还会有这种非必须在application启动时初始化的第三方SDK请写在InitTask中然后添加到这里的just里面
        Observable.just(
            initBugly(appContext)
        ).subscribeOn(Schedulers.computation()).doOnNext { it() }.subscribe()
        SophixManager.getInstance().queryAndLoadNewPatch()*/
    }


    override fun isMainProcess(): Boolean = getApplicationName() == applicationId()

    private fun getApplicationName(): String? {
        val am = applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return am.runningAppProcesses
            .firstOrNull {
                it.pid == Process.myPid()
            }?.processName
    }

    override val application: Application
        get() = this


    /*private fun initTasks() {
        mInitTasks.forEach {
            it.init(this)
        }
    }

    private val mInitTasks = listOf(
        InitARouter,
        InitUMeng,
        InitCrash,
        InitBugly
    )*/

}
