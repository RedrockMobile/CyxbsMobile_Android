package com.mredrock.cyxbs.sdks

import android.content.Context
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.BuildConfig
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService
import com.mredrock.cyxbs.lib.base.utils.Umeng
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.utils.LogUtils
import com.tencent.vasdolly.helper.ChannelReaderUtil
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent
import com.umeng.message.api.UPushRegisterCallback
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 * ## 注意
 * ### debug 下 Umeng 注册失败
 * 由于使用了 debug 下共存的功能，所以会导致 debug 下包名不为 com.mredrock.cyxbs，所以注册会失败
 *
 * 修改 build-logic/core/version 模块中的 Config#getApplicationId() 方法即可解决
 *
 * ### 推送
 * 该注册服务经过了一次大的迁移，由于目前 22年 推送功能无效，所以暂时删除了推送，
 * 如果以后再次接入推送，可以参考下以前的代码：
 * https://github.com/RedrockMobile/CyxbsMobile_Android/blob/b081ae262277990145813678d0d2ed03861b9238/lib_common/src/main/java/com/mredrock/cyxbs/common/InitTask.kt
 *
 * @author ZhiQiang Tu
 * @time 2022/3/24  19:42
 * @signature 我将追寻并获取我想要的答案
 */
@AutoService(InitialService::class)
class UmengInitialService : InitialService {

    override fun onMainProcess(manager: InitialManager) {
        //debug包开启log
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true)
        }
        //预初始化 等待隐私策略同意后才进行真正的初始化
        UMConfigure.preInit(appContext, BuildConfig.UM_APP_KEY, getChannel(manager.application))
        //自动埋点 Activity，Umeng 这个手动埋点不好设置埋点 Fragment，因为它只允许 onPageStart()，onPageEnd() 成对调用
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
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
                override fun onSuccess(deviceId: String?) {
                    // 注册成功会返回 deviceId，是推送消息的唯一标志
                    LogUtils.e(msg = "Umeng 注册成功 deviceToken:$deviceId")
                    Umeng.deviceId = deviceId
                }

                override fun onFailure(errCode: String?, errDesc: String?) {
                    /*
                    * 注意：由于 debug 状态下修改包名，会导致 Umeng 注册失败，如果需要测试 Umeng
                    * 请修改 build-logic/core/version 模块中的 Config#getApplicationId() 方法
                    * */
                    if (BuildConfig.DEBUG && manager.applicationId() != "com.mredrock.cyxbs") {
                        val message = """
                            
                            ┌─────────警告────────────────────────────────────────────────────────
                            │
                            │    如果你不需要测试 Umeng 功能，请忽略该条 log !
                            │
                            │────────────────────────────────────────────────────────────────────
                            │
                            │    检测到你目前包名不为 com.mredrock.cyxbs，会导致 Umeng 注册失败
                            │       如果你需要测试 Umeng 功能，
                            │       请修改 build-logic/core/version 模块中的 Config#getApplicationId() 方法
                            │
                            └────────────────────────────────────────────────────────────────────
                        """.trimIndent()
                        LogUtils.e(msg = message)
                    } else {
                        LogUtils.e(msg = "Umeng 注册失败 code:$errCode, desc:$errDesc")
                    }
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
            getChannel(manager.application),
            UMConfigure.DEVICE_TYPE_PHONE,
            BuildConfig.UM_PUSH_SECRET
        )
    }

    private fun getChannel(context: Context): String {
        return ChannelReaderUtil.getChannel(context) ?: "unknown"
    }
}