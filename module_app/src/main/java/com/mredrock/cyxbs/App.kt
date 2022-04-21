package com.mredrock.cyxbs

import android.app.Application
import androidx.annotation.Keep
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.spi.SdkService
import com.mredrock.cyxbs.spi.SdkManager
import com.mredrock.cyxbs.spi.TaskService
import java.util.*

/**
 * Created By jay68 on 2018/8/8.
 */
@Keep
class App : BaseApp(), SdkManager {

    private val sdkLoader = ServiceLoader.load(SdkService::class.java)
    private val taskLoader = ServiceLoader.load(TaskService::class.java)

    override fun onCreate() {
        super.onCreate()
        sdks()
        tasks()
    }

    private fun tasks() {
        taskLoader.forEach {
            it.work()
        }
    }

    private fun sdks() {
        //如果是在主进程，遍历所有的SdkService实现类的onMainProcess
        sdkLoader.takeIf {
            isMainProcess()
        }?.forEach {
            it.onMainProcess(this)
        } ?:
        //如果是在子进程，通过isSdkProcess匹配对应SdkService实现类，最后调用onSdkProcess方法。
        sdkLoader.firstOrNull {
            it.isSdkProcess(this)
        }?.onSdkProcess(this)
    }

    override val application: Application
        get() = this


}
