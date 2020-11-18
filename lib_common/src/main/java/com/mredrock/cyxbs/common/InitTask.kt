package com.mredrock.cyxbs.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.google.gson.JsonParser
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.config.DebugDataModel
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.account.IUserStateService
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.debug
import com.mredrock.cyxbs.common.utils.extensions.runOnUiThread
import com.mredrock.cyxbs.common.utils.jump.JumpProtocol
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.statistics.common.DeviceConfig
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage
import com.umeng.message.inapp.InAppMessageManager


/**
 * Created by Jovines on 2020/06/09 15:19
 * description : 一些第三方库的初始化操作写在这
 */

fun initUMeng(context: Context) {
    // 这里在小部件的进程初始化的时候注册友盟推送会概率性抛异常，这里这个其实不用初始化友盟推送
    // 所以可以直接捕获异常并不做任何处理
    try {
        val channel = WalleChannelReader.getChannel(context, "debug")
        UMConfigure.init(context, BuildConfig.UM_APP_KEY, channel, UMConfigure.DEVICE_TYPE_PHONE,
                BuildConfig.UM_PUSH_SECRET)
        // 选用AUTO页面采集模式,则不用在activity中调用MobclickAgent.onResume(this)|MobclickAgent.onPause(this)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        //调试模式（推荐到umeng注册测试机，避免数据污染）
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        //友盟推送服务的接入
        val mPushAgent = PushAgent.getInstance(context)
        mPushAgent.setNotificaitonOnForeground(true)
        mPushAgent.setEnableForground(context, true)
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object : IUmengRegisterCallback {
            // 这些回调是在子线程
            override fun onSuccess(deviceToken: String) {
                debug {
                    context.runOnUiThread {
                        DebugDataModel.umPushDeviceId.value = deviceToken
                    }
                    //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                    LogUtils.i("友盟推送注册", "注册成功：deviceToken：-------->  $deviceToken")
                }
            }

            override fun onFailure(s: String, s1: String) {
                debug {
                    LogUtils.e("友盟推送注册", "注册失败：-------->  s:$s,s1:$s1")
                }
            }
        })
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            (context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)?.let { notificationManager ->
                val channelId = "qa_channel"
                val channelName: CharSequence = "邮问消息"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(channelId, channelName, importance)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 500)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        val messageHandler: UmengMessageHandler = object : UmengMessageHandler() {
            override fun getNotification(context: Context, msg: UMessage): Notification {
                return when (msg.builder_id) {
                    1 -> {
                        val builder = NotificationCompat.Builder(BaseApp.context, "qa_channel")
                        builder.setContentTitle(msg.title)
                                .setContentText(msg.text)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setTicker(msg.ticker)
                                .setAutoCancel(true)
                        builder.build()
                    }
                    else -> super.getNotification(context, msg)
                }
            }
        }
        val notificationClickHandler: UmengNotificationClickHandler = object : UmengNotificationClickHandler() {
            override fun dealWithCustomAction(context: Context, msg: UMessage) {
                val data = JsonParser.parseString(msg.custom).asJsonObject
                data.get("uri")?.let {
                    JumpProtocol.start(it.asString)
                }
            }
        }
        mPushAgent.notificationClickHandler = notificationClickHandler

        var studentID = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
        mPushAgent.addAlias(studentID, "cyxbs") { _, _ -> }
        ServiceManager.getService(IAccountService::class.java).getVerifyService().addOnStateChangedListener {
            when (it) {
                IUserStateService.UserState.LOGIN -> {
                    studentID = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
                    mPushAgent.addAlias(studentID, "cyxbs") { _, _ -> }
                }
                IUserStateService.UserState.NOT_LOGIN -> {
                    mPushAgent.deleteAlias(studentID, "cyxbs") { _, _ -> }
                }
                else -> {
                }
            }
        }

        mPushAgent.messageHandler = messageHandler
        InAppMessageManager.getInstance(BaseApp.context).setInAppMsgDebugMode(true)
        debug {
            val deviceInfo = arrayOfNulls<String>(2)
            deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(BaseApp.context)
            deviceInfo[1] = DeviceConfig.getMac(BaseApp.context)
            val msg = """{"device_id":"${deviceInfo[0]}","mac":"${deviceInfo[1]}"}"""
            DebugDataModel.umAnalyzeDeviceData.value = msg
            LogUtils.d("UM设备测试信息：", msg)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}