package com.mredrock.cyxbs.sdks

import android.os.Process
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.BuildConfig
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.spi.SdkService
import com.mredrock.cyxbs.spi.SdkManager
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent
import com.umeng.message.api.UPushRegisterCallback
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  19:42
 *@signature 我将追寻并获取我想要的答案
 */
@AutoService(SdkService::class)
class UmengInitialService : SdkService {

    override fun onMainProcess(manager: SdkManager) {
        //debug包开启log
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true)
        }

        initUmengAnalyse(manager)
        initUmengPush(manager)


    }

    override fun onSdkProcess(manager: SdkManager) {
        val context = manager.application.applicationContext

    }

    private fun initUmengPush(manager: SdkManager) {
        //预初始化
        val context = manager.application.applicationContext
        with(PushAgent.getInstance(context)) {
            register(
                object : UPushRegisterCallback {
                    override fun onSuccess(deviceToken: String?) {
                        //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                        LogUtils.e(msg = "注册成功 deviceToken:$deviceToken")
                    }

                    override fun onFailure(errCode: String?, errDesc: String?) {
                        LogUtils.e(msg = "注册失败 code:$errCode, desc:$errDesc")
                    }

                }
            )
        }


    }

    private fun initUmengAnalyse(manager: SdkManager) {
        val context = manager.application.applicationContext
        //预初始化
        UMConfigure.preInit(context, BuildConfig.UM_APP_KEY, "official")

        //这段代码理应再用户隐私协议同意以后才执行的。现暂时搁置。
        listOf(Unit).toObservable()
            .map {
                UMConfigure.init(
                    context,
                    BuildConfig.UM_APP_KEY,
                    "official",
                    UMConfigure.DEVICE_TYPE_PHONE,
                    BuildConfig.UM_PUSH_SECRET
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe()
    }

}