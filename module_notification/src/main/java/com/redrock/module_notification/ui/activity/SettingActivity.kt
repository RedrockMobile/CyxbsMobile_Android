package com.redrock.module_notification.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.NOTIFICATION_SETTING
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.redrock.module_notification.R
import com.redrock.module_notification.util.Constant.IS_SWITCH1_SELECT
import com.redrock.module_notification.util.Constant.IS_SWITCH2_SELECT
import com.redrock.module_notification.util.NotificationSp
import kotlinx.android.synthetic.main.activity_setting.*
import kotlin.properties.Delegates

/**
 * Author by OkAndGreat
 * Date on 2022/4/25 22:46.
 *
 */
//考虑到通知设置页以后可能会有其它渠道进入，故配置一个路由
@Route(path = NOTIFICATION_SETTING)
class SettingActivity :BaseActivity() {
    private var switch1Checked by Delegates.notNull<Boolean>()
    private var switch2Checked by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initSwitch()
        initViewClickListener()
    }

    private fun initSwitch(){
        switch1Checked = NotificationSp.getBoolean(IS_SWITCH1_SELECT,false)
        switch2Checked = NotificationSp.getBoolean(IS_SWITCH2_SELECT,false)
        notification_setting_switch_1.isChecked = switch1Checked
        notification_setting_switch_2.isChecked = switch2Checked
    }

    private fun initViewClickListener(){
        notification_setting_test.setOnClickListener {
            val manager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel("CyxbsSignNotification", "签到提醒", NotificationManager.IMPORTANCE_HIGH)
                manager.createNotificationChannel(channel)
            }


            //为了版本兼容  选择V7包下的NotificationCompat进行构造
            val builder = NotificationCompat.Builder(this, "CyxbsSignNotification")
            //Ticker是状态栏显示的提示
            builder.setTicker("打卡咯")
            //第一行内容  通常作为通知栏标题
            builder.setContentTitle("打卡提醒-已经过18点了你还没有打卡噢~~")
            //第二行内容 通常是通知正文
            builder.setContentText("可以在消息设置中关闭打卡呢~")
            //可以点击通知栏的删除按钮删除
            builder.setAutoCancel(true)
            //系统状态栏显示的小图标
            builder.setSmallIcon(R.drawable.common_ic_app_notifacation)
            //下拉显示的大图标
            val intent = Intent(this, this::class.java)
            val pIntent = PendingIntent.getActivity(this, 1, intent, 0)
            builder.setContentIntent(pIntent)
            builder.setDefaults(NotificationCompat.DEFAULT_ALL)
            val notification = builder.build()
            manager.notify(1, notification)
        }
        notification_setting_switch_1.setOnCheckedChangeListener{_, isChecked ->
            NotificationSp.editor {
                if (isChecked) {
                    putBoolean(IS_SWITCH1_SELECT, true)
                } else {
                    putBoolean(IS_SWITCH1_SELECT, false)
                }
            }
        }

        notification_setting_switch_2.setOnCheckedChangeListener{_, isChecked ->
            NotificationSp.editor {
                if (isChecked) {
                    putBoolean(IS_SWITCH2_SELECT, true)
                } else {
                    putBoolean(IS_SWITCH2_SELECT, false)
                }
            }
        }

        notification_rl_setting_back.setOnClickListener {
            finish()
        }
    }
}