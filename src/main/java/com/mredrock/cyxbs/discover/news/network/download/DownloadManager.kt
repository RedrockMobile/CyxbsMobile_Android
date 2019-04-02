package com.mredrock.cyxbs.discover.news.network.download

import android.os.Environment
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.discover.news.network.ApiService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Author: Hosigus
 * Date: 2018/9/24 16:18
 * Description: 下载的入口
 */
object DownloadManager {

    fun download(listener: RedDownloadListener, id: String, fileName: String) {
        val client = OkHttpClient.Builder()
                .addNetworkInterceptor(RedDownloadInterceptor(listener))
                .build()
        listener.onDownloadStart()

        Retrofit.Builder()
                .baseUrl(END_POINT_REDROCK)
                .client(client)
                .build()
                .create(ApiService::class.java)
                .download(id)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        listener.onFail(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body() ?: return
                        val state = Environment.getExternalStorageState()
                        if (Environment.MEDIA_MOUNTED != state && Environment.MEDIA_MOUNTED_READ_ONLY != state) {
                            listener.onFail(Exception("permission deny"))
                            return
                        }

                        val ins: InputStream
                        val fos: FileOutputStream
                        try {
                            ins = body.byteStream()
                            val file = File(
                                    Environment.getExternalStorageDirectory().absolutePath
                                            + "/Download/"
                                            + fileName
                                            + "."
                                            + response.headers()["Content-Disposition"]?.let {
                                        it.substring(it.indexOf("filename="), it.length).substringAfterLast(".")
                                    }
                            )
                            fos = FileOutputStream(file)

                            val bytes = ByteArray(1024)
                            var length = ins.read(bytes)
                            while (length != -1) {
                                fos.write(bytes, 0, length)
                                length = ins.read(bytes)
                            }
                            fos.flush()
                            listener.onSuccess(file)
                        } catch (e: Exception) {
                            listener.onFail(e)
                        }
                    }
                })
    }

}