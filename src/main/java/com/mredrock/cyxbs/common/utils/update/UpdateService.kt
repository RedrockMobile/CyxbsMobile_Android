package com.mredrock.cyxbs.common.utils.update

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
import com.mredrock.cyxbs.common.utils.extensions.uri

import org.jetbrains.anko.toast

import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class UpdateService : Service() {

    private var notificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    private val notificationId = 1003
    private var task: DownloadAsyncTask? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.extras == null) {
            if (task != null) {
                task!!.stop()
                task!!.cancel(true)
                task = null
                toast("取消更新")
            }
            return START_NOT_STICKY
        }
        val url = intent.extras!!.getString("url")
        val filepath = intent.extras!!.getString("path")
        val filename = intent.extras!!.getString("name")

        if (url != null && isNetWorkAvailable()) {
            createNotification()
            task = null
            task = DownloadAsyncTask()
            task!!.execute(url, filepath, filename)
            toast("新版掌上重邮开始下载了...")
        }
        return START_NOT_STICKY
    }

    private fun isNetWorkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return !(activeNetInfo == null || !activeNetInfo.isAvailable)
    }

    override fun onDestroy() {
        task!!.cancel(true)
        task = null
        super.onDestroy()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_app_logo)
                .setContentTitle("掌上重邮更新中...")
                .setContentText("0%")
        builder!!.setProgress(100, 0, false)

        val stopIntent = Intent(this, UpdateService::class.java)
        builder!!.setDeleteIntent(PendingIntent.getService(this, 0, stopIntent, 0))
        builder!!.setAutoCancel(true)
        builder!!.setTicker("新版掌上重邮开始下载了...")
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(progress: Int) {
        builder!!.setProgress(100, progress, false)
        builder!!.setContentText("$progress%")
        notificationManager!!.notify(notificationId, builder!!.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class DownloadAsyncTask : AsyncTask<String, Int, Boolean>() {
        private var path: String? = null
        private var name: String? = null
        private var cancelUpdate: Boolean = false

        fun stop() {
            cancelUpdate = true
        }

        override fun doInBackground(vararg params: String): Boolean? {
            path = params[1]
            name = params[2]
            var finish = false
            try {
                val url = URL(params[0])
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                if (conn.responseCode == 200) {
                    val f = File(params[1])
                    if (!f.isDirectory) {
                        f.mkdirs()
                    }
                    val `is` = conn.inputStream
                    val length = conn.contentLength
                    val file = File(path!! + name!!)
                    if (file.exists()) {
                        file.delete()  // I think maybe the existent file cause the update failure
                    }
                    val fos = FileOutputStream(file)
                    var count = 0
                    val buf = ByteArray(1024)
                    var progress:Int
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
                finish = false
            }

            return finish
        }

        override fun onPostExecute(finish: Boolean?) {
            notificationManager!!.cancel(notificationId)
            val file = File(path!! + name!!)
            if (finish!! && !cancelUpdate && file.exists()) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW)
                            .addFlags(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            } else {
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                            .setDataAndType(file.uri, "application/vnd.android.package-archive"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
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
