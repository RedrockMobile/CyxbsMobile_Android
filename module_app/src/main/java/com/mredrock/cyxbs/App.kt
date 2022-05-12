package com.mredrock.cyxbs

import android.app.Application
import androidx.annotation.Keep
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.PRIVACY_AGREED
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
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
        //由于android每开辟进程都会访问application的生命周期方法,所以为了保证sdk初始化无措，最好对其进行过滤。
        //因为有些sdk的初始化不是幂等的，即多次初始化会导致进程的crash。这样就会导致一些未知的问题。
        //所以解决方案就是对当前进程进程判断，只在main进程初始化sdk，其余进程默认不进行sdk的初始化。
        // (不排除某些sdk需要，比如友盟推送就需要在新开辟的:channel进行进行初始化)
        if (isMainProcess()){
            onMainProcess()
        }else {
            onOtherProcess()
        }
    }

    //非主进程
    private fun onOtherProcess() {
        loader.forEach {
            it.onSdkProcess(this)
        }
    }

    //主进程
    private fun onMainProcess() {
        //不管是否同意隐私策略都调用
        loader.forEach {
            it.onMainProcess(this)
        }
        //同意了隐私策略
        if (defaultSharedPreferences.getBoolean(PRIVACY_AGREED,false) && isMainProcess()) {
            loader.forEach {
                it.onPrivacyAgreed(this)
            }
        }
    }

    //隐私策略同意了
    override fun privacyAgree(){
        loader.forEach {
            it.onPrivacyAgreed(this)
        }
    }

    //没同意
    override fun privacyDenied(){
        loader.forEach {
            it.onPrivacyDenied(this)
        }
    }

    override val application: Application
        get() = this


}
