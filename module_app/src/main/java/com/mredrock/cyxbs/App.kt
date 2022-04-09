package com.mredrock.cyxbs

import android.app.Application
import androidx.annotation.Keep
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.spi.SdkService
import com.mredrock.cyxbs.spi.SdkManager
import java.util.*

/**
 * Created By jay68 on 2018/8/8.
 */
@Keep
class App : BaseApp(), SdkManager {

    private val loader = ServiceLoader.load(SdkService::class.java)

    override fun onCreate() {
        super.onCreate()
        //如果是在主进程，遍历所有的SdkService实现类的onMainProcess
        loader.takeIf {
            isMainProcess()
        }?.forEach {
            it.onMainProcess(this)
        } ?:
        //如果是在子进程，通过isSdkProcess匹配对应SdkService实现类，最后调用onSdkProcess方法。
        loader.firstOrNull {
            it.isSdkProcess(this)
        }?.onSdkProcess(this)

    }

    override val application: Application
        get() = this


}
