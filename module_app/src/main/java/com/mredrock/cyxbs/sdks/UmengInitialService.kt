package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.BuildConfig
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.utils.LogUtils
import com.mredrock.cyxbs.lib.base.spi.InitialService
import com.mredrock.cyxbs.lib.base.spi.InitialManager
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent
import com.umeng.message.api.UPushRegisterCallback
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  19:42
 *@signature 我将追寻并获取我想要的答案
 */
@AutoService(InitialService::class)
class UmengInitialService : InitialService {

    private val UMENG_CHANNEL = "official"

    override fun onMainProcess(manager: InitialManager) {
        //debug包开启log
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true)
        }
        //预初始化 等待隐私策略同意后才进行真正的初始化
        UMConfigure.preInit(appContext, BuildConfig.UM_APP_KEY, UMENG_CHANNEL)
    }

    override fun onOtherProcess(manager: InitialManager) {
        LogUtils.e("TAG", "onSdkProcess: \ncurrentProcess = ${manager.currentProcessName()}\n")
        initUmengPush(manager)
    }

    override fun isOtherProcess(manager: InitialManager): Boolean =
        manager.currentProcessName()?.endsWith(":channel") ?: false

    override fun onPrivacyAgreed(manager: InitialManager) {
        Observable.just(Unit)
            .subscribeOn(Schedulers.io())
            .unsafeSubscribeBy {
                initUmengPush(manager)
                initUmengAnalyse(manager)
            }
    }

    //注意sdk进程和main进程均需要初始化
    private fun initUmengPush(manager: InitialManager) {
        val context = manager.application.applicationContext
        val agent = PushAgent.getInstance(context)
        agent.register(
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

    private fun initUmengAnalyse(manager: InitialManager) {
        val context = manager.application.applicationContext

        //这段代码理应再用户隐私协议同意以后才执行的
        UMConfigure.init(
            context,
            BuildConfig.UM_APP_KEY,
            UMENG_CHANNEL,
            UMConfigure.DEVICE_TYPE_PHONE,
            BuildConfig.UM_PUSH_SECRET
        )

    }

}