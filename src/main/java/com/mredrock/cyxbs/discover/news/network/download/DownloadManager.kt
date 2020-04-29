package com.mredrock.cyxbs.discover.news.network.download

import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.discover.news.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * Author: Hosigus
 * Date: 2018/9/24 16:18
 * Description: 下载的入口
 */
object DownloadManager {

    fun download(listener: RedDownloadListener, id: String) {
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
                                    decodeFileName(response.headers()["Content-Disposition"]))
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

    // todo 后端文件名GBK编码太扯了，啥时候让后端改了
    private fun decodeFileName(string: String?) = string?.let {
        java.lang.String(it.substring(it.indexOf("filename=") + 9).toByteArray(StandardCharsets.ISO_8859_1), "GBK") as String
    }

}