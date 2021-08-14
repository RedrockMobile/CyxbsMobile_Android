package com.mredrock.cyxbs.discover.news.network.download

import java.io.File

/**
 * Author: Hosigus
 * Date: 2018/9/23 18:06
 * Description: 下载进度监听
 */
interface RedDownloadListener {
    fun onDownloadStart()
    fun onProgress(currentBytes: Long, contentLength: Long)
    fun onSuccess(file: File)
    fun onFail(e: Throwable)
}