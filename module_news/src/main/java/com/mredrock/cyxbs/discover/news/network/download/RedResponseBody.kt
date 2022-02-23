package com.mredrock.cyxbs.discover.news.network.download

import okhttp3.ResponseBody
import okio.*

/**
 * Author: Hosigus
 * Date: 2018/9/23 18:08
 * Description: 重写ForwardingSource的read方法，在read方法中计算百分比，回调进度
 */
class RedResponseBody(private val responseBody: ResponseBody,
                      private val listener: RedDownloadListener
) : ResponseBody() {

    private val source by lazy {
        object : ForwardingSource(responseBody.source()) {
            private var bytesRead = 0L
            override fun read(sink: Buffer, byteCount: Long): Long {
                val read = super.read(sink, byteCount)
                if (read != -1L) {
                    bytesRead += read
                    listener.onProgress(bytesRead, responseBody.contentLength())
                }
                return read
            }
        }.buffer()
    }

    override fun contentLength() = responseBody.contentLength()

    override fun contentType() = responseBody.contentType()

    override fun source(): BufferedSource = source

}