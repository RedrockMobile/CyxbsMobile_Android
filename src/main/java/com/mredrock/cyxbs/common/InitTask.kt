package com.mredrock.cyxbs.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.meituan.android.walle.WalleChannelReader
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.debug
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.statistics.common.DeviceConfig
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.entity.UMessage
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
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        (context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)?.let { notificationManager ->
            val channelId = "qa_channel"
            val channelName: CharSequence = "邮问消息"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationChannel.vibrationPattern = longArrayOf(0,50,50,120,500,120)
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
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setTicker(msg.ticker)
                            .setAutoCancel(true)
                    builder.build()
                }
                else -> super.getNotification(context,msg)
            }
        }
    }
//    mPushAgent.setNotificaitonOnForeground(true)
//    mPushAgent.setEnableForground(context,true)
    mPushAgent.messageHandler = messageHandler
    InAppMessageManager.getInstance(BaseApp.context).setInAppMsgDebugMode(true)
    debug {
        val deviceInfo = arrayOfNulls<String>(2)
        deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(BaseApp.context)
        deviceInfo[1] = DeviceConfig.getMac(BaseApp.context)
        LogUtils.d("UM设备测试信息：", """{"device_id":"${deviceInfo[0]}","mac":"${deviceInfo[1]}"}""")
    }

}
//
////创建通知渠道
//@RequiresApi(api = Build.VERSION_CODES.O)
//private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
//    val channel = NotificationChannel(channelId, channelName, importance)
//    val notificationManager = getSystemService(
//            NOTIFICATION_SERVICE) as NotificationManager?
//    notificationManager!!.createNotificationChannel(channel)
//}
//
//fun sendUpgradeMsg(view: View?) {
//    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
//    val notification: Notification = Builder(this, "upgrade")
//            .setContentTitle("升级")
//            .setContentText("程序员终于下班了。。")
//            .setWhen(System.currentTimeMillis())
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//            .setAutoCancel(true)
//            .build()
//    manager!!.notify(100, notification)
//}
//
//fun sendComposeMsg(view: View?) {
//    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
//    val notification: Notification = NotificationCompat.Builder(this, "compose")
//            .setContentTitle("私信")
//            .setContentText("有人私信向你提出问题")
//            .setWhen(System.currentTimeMillis())
//            .setSmallIcon(R.drawable.icon)
//            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
//            .build()
//    manager!!.notify(101, notification)
//}