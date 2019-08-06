package com.mredrock.cyxbs.mine.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mredrock.cyxbs.mine.R
import org.jetbrains.annotations.Nullable

/**
 * Created by zia on 2018/10/8.
 */
object NotificationUtil {

    fun makeNotification(context: Context, contentText: String, title: String = "掌上重邮", CHANNEL_ID: String = "cyxbs", description: String = "", notificationId: Int = 1,@Nullable pendingIntent: PendingIntent? = null) {
        // 8.0以上创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "掌上重邮"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.mine_ic_launcher_background)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0))

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }

        NotificationManagerCompat.from(context).notify(1, builder.build())
    }
}