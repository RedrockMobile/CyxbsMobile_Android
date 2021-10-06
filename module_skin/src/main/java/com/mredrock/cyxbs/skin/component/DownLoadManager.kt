package com.mredrock.cyxbs.skin.component

import android.content.Context
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by LinTong on 2021/10/6
 * Description:
 */
object DownLoadManager {
    //    所有的皮肤包id与名字在这里列出
    val allSkinName = mapOf(Pair(1, "国庆专题"))
    private val okHttpClient = OkHttpClient()

    /**
     * @param url 下载连接
     * @param saveDir 储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    fun download(context: Context, url: String, fileName: String, listener: OnDownloadListener) {
        val request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 下载失败
                listener.onDownloadFailed()

            }

            override fun onResponse(call: Call, response: Response) {
                val buf = ByteArray(2048)
                var len = 0
                var input: InputStream? = null
                var fos: FileOutputStream? = null
                // 储存下载文件的目录
                val savePath = context.cacheDir.absolutePath
                try {
                    input = response.body?.byteStream()
                    val total: Long = response.body?.contentLength() ?: 0
                    val file = File(savePath, fileName)
                    fos = FileOutputStream(file)
                    var sum: Long = 0
                    if (input != null) {
                        len = input.read(buf)
                        while (len != -1) {
                            len = input.read(buf)
                            fos.write(buf, 0, len)
                            sum += len
                            val progress = (sum * 1.0f / total * 100).toInt()
                            // 下载中
                            listener.onDownloading(progress)
                        }
                    }
                    fos.flush()
                    // 下载完成
                    listener.onDownloadSuccess()
                } catch (e: Exception) {
                    listener.onDownloadFailed()
                } finally {
                    try {
                        input?.close()
                    } catch (e: IOException) {
                    }
                    try {
                        fos?.close()
                    } catch (e: IOException) {
                    }
                }
            }
        })
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        fun onDownloadSuccess()

        /**
         * @param progress
         * 下载进度
         */
        fun onDownloading(progress: Int)

        /**
         * 下载失败
         */
        fun onDownloadFailed()
    }

    /**
     * 判断文件是否存在
     */
    private fun fileIsExists(strFile: String?): Boolean {
        try {
            if (!File(strFile).exists()) {
                return false
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }
}