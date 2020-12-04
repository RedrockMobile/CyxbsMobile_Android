package com.mredrock.cyxbs.update.component

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.config.updateFile
import com.mredrock.cyxbs.common.config.updateFilePath
import com.mredrock.cyxbs.api.update.AppUpdateStatus
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.update.model.AppUpdateModel
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AppUpdateDownloadService : Service() {

    private var notificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    private val notificationId = 1003
    private var task: DownloadAsyncTask? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.extras == null) {
            val t = task
            if (t != null) {
                t.stop()
                t.cancel(true)
                task = null
                toast("取消更新")
            }
            return START_NOT_STICKY
        }
        val url = intent.extras?.getString("url")
        if (url.isNullOrBlank()) {
            stopSelf()
        } else if (isNetWorkAvailable()) {
            createNotification()
            task = null
            val t = DownloadAsyncTask()
            t.execute(url)
            task = t
            toast("新版掌上重邮开始下载了...")
        }
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun isNetWorkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return activeNetInfo != null && activeNetInfo.isConnected
    }

    override fun onDestroy() {
        task?.stop()
        task?.cancel(true)
        task = null
        super.onDestroy()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val stopIntent = Intent(this, AppUpdateDownloadService::class.java)
        builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_ic_app_logo)
                .setContentTitle("掌上重邮更新中...")
                .setContentText("0%")
                .setProgress(100, 0, false)
                .setDeleteIntent(PendingIntent.getService(this, 0, stopIntent, 0))
                .setAutoCancel(true)
                .setTicker("新版掌上重邮开始下载了...")
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(progress: Int) {
        val n = builder ?: return

        n.setProgress(100, progress, false)
        n.setContentText("$progress%")

        val manager = notificationManager
                ?: getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, n.build())
        if (notificationManager == null) {
            notificationManager = manager
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class DownloadAsyncTask : AsyncTask<String, Int, Boolean>() {
        private var cancelUpdate: Boolean = false

        fun stop() {
            cancelUpdate = true
            AppUpdateModel.status.postValue(AppUpdateStatus.CANCEL)
        }

        override fun doInBackground(vararg params: String): Boolean? {
            var finish = false
            try {
                val url = URL(params[0])
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                if (conn.responseCode == 200) {
                    val f = File(updateFilePath)
                    if (!f.isDirectory) {
                        f.mkdirs()
                    }
                    val `is` = conn.inputStream
                    val length = conn.contentLength
                    val file = updateFile
                    if (file.exists()) {
                        file.delete()  // I think maybe the existent file cause the update failure
                    }
                    val fos = FileOutputStream(file)
                    var count = 0
                    val buf = ByteArray(1024)
                    var progress: Int
                    var progressPre = 0
                    do {
                        val numRead = `is`.read(buf)
                        count += numRead
                        progress = (count.toFloat() / length * 100).toInt()
                        if (progress != progressPre) {
                            publishProgress(progress)
                            progressPre = progress
                        }

                        if (numRead <= 0) {
                            break
                        }
                        fos.write(buf, 0, numRead)
                    } while (!cancelUpdate)
                    fos.flush()
                    fos.close()
                    `is`.close()
                    finish = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                finish = false
                AppUpdateModel.status.postValue(AppUpdateStatus.ERROR)
            }

            return finish
        }

        override fun onPostExecute(finish: Boolean?) {
            notificationManager?.cancel(notificationId)
            val file = updateFile
            if (finish == true && !cancelUpdate && file.exists()) {
                AppUpdateModel.status.postValue(AppUpdateStatus.TO_BE_INSTALLED)
            } else {
                if (AppUpdateModel.status.value != AppUpdateStatus.CANCEL) {
                    AppUpdateModel.status.postValue(AppUpdateStatus.ERROR)
                }
                toast("更新失败")
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            if (values.isEmpty()) {
                return
            }
            values[0]?.let { updateNotification(it) }
        }

    }

    companion object {
        const val CHANNEL_ID = "app_update"
        const val CHANNEL_NAME = "更新消息"
    }
}
