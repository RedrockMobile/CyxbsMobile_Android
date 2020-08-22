package com.mredrock.cyxbs.common

import android.content.Context
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.debug
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.statistics.common.DeviceConfig
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.inapp.InAppMessageManager


/**
 * Created by Jovines on 2020/06/09 15:19
 * description : 一些第三方库的初始化操作写在这
 */

fun initUMeng(context: Context) {
    val channel = WalleChannelReader.getChannel(context, "debug")
    UMConfigure.init(context, BuildConfig.UM_APP_KEY, channel, UMConfigure.DEVICE_TYPE_PHONE,
            BuildConfig.UM_PUSH_SECRET)
//    MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL)
//    MobclickAgent.openActivityDurationTrack(false)
    // 选用AUTO页面采集模式,则不用在activity中调用MobclickAgent.onResume(this)|MobclickAgent.onPause(this)
    MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    //调试模式（推荐到umeng注册测试机，避免数据污染）
    UMConfigure.setLogEnabled(BuildConfig.DEBUG)
    //友盟推送服务的接入
    val mPushAgent = PushAgent.getInstance(context)
    //注册推送服务，每次调用register方法都会回调该接口
    mPushAgent.register(object : IUmengRegisterCallback {
        override fun onSuccess(deviceToken: String) {
            //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
            LogUtils.i("友盟推送注册", "注册成功：deviceToken：-------->  $deviceToken")
        }

        override fun onFailure(s: String, s1: String) {
            LogUtils.e("友盟推送注册", "注册失败：-------->  s:$s,s1:$s1")
        }
    })
    InAppMessageManager.getInstance(BaseApp.context).setInAppMsgDebugMode(true)
    debug {
        val deviceInfo = arrayOfNulls<String>(2)
        deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(BaseApp.context)
        deviceInfo[1] = DeviceConfig.getMac(BaseApp.context)
        LogUtils.d("UM设备测试信息：", """{"device_id":"${deviceInfo[0]}","mac":"${deviceInfo[1]}"}""")
    }

}