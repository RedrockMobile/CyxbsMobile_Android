package com.mredrock.cyxbs

import android.app.Application
import com.mredrock.cyxbs.common.BaseApp
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
        } ?: loader.filter {
            it.isSdkProcess(this)
        }.forEach {
            it.onSdkProcess(this)
        }

    }

    override val application: Application
        get() = this


}
