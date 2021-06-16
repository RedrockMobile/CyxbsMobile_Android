package com.mredrock.cyxbs.discover.news.network.download

import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import com.mredrock.cyxbs.common.config.getBaseUrl
import com.mredrock.cyxbs.discover.news.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
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
                .baseUrl(getBaseUrl())
                .client(client)
                .build()
                .create(ApiService::class.java)
                .download(id)
                .enqueue(object : retrofit2.Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        listener.onFail(t)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
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
                            val file = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
                                    "$fileName.${splitFileType(response.headers()["Content-Disposition"])}")
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

    private fun splitFileType(string: String?) = string?.let {
        it.substring(it.indexOf("filename="), it.length).substringAfterLast(".")
    }

}