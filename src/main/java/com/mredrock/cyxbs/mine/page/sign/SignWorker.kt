package com.mredrock.cyxbs.mine.page.sign

import android.app.PendingIntent
import android.content.Intent
import androidx.work.Worker
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.mine.util.NotificationUtil
import java.util.*

/**
 * Created by zia on 2018/10/8.
 * 每日签到提醒的work
 */
class SignWorker : Worker() {

    private val FLAG = "SIGNPUSH"

    override fun doWork(): Worker.Result {

        //读取是否通知过
        val isPush = applicationContext.defaultSharedPreferences.getBoolean(FLAG, false)

        if (compareCurrentHour(7)) {
            if (!isPush) {
                //如果在指定时间段，并且没有推送过通知
                applicationContext.defaultSharedPreferences.editor {
                    //写入已通知
                    putBoolean(FLAG, true)
                }
                //继续后面的推送通知代码
            } else {
                //在指定时间段，已推送过了，则不再推送
                return Result.RETRY
            }
        } else {
            //不在时间段，重置标志位false
            applicationContext.defaultSharedPreferences.editor {
                putBoolean(FLAG, false)
            }
            return Result.RETRY
        }

        val resultIntent = Intent(applicationContext, DailySignActivity::class.java)
        val intent = PendingIntent.getActivity(applicationContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        //推送通知
        NotificationUtil.makeNotification(applicationContext, "赶快去签到领取积分哦~", CHANNEL_ID = "cyxbs_sign", pendingIntent = intent)

        return Worker.Result.SUCCESS
    }

    private fun compareCurrentHour(targetHour: Int): Boolean {
        val current = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return current == targetHour
    }
}
