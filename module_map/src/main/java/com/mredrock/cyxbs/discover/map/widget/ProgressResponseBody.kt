package com.mredrock.cyxbs.discover.map.widget

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class ProgressResponseBody(url: String?, private val responseBody: ResponseBody) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    private var listener: ProgressListener?
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = ProgressSource(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private inner class ProgressSource internal constructor(source: Source?) : ForwardingSource(source!!) {
        var totalBytesRead: Long = 0
        var currentProgress = 0

        @Throws(IOException::class)
        override fun read(sink: Buffer, byteCount: Long): Long {
            val bytesRead = super.read(sink, byteCount)
            val fullLength = responseBody.contentLength()
            if (bytesRead == -1L) {
                totalBytesRead = fullLength
            } else {
                totalBytesRead += bytesRead
            }
            val progress = (100f * totalBytesRead / fullLength).toInt()
            if (listener != null && progress != currentProgress) {
                listener!!.onProgress(progress)
            }
            if (listener != null && totalBytesRead == fullLength) {
                listener = null
            }
            currentProgress = progress
            return bytesRead
        }
    }

    companion object {
        private const val TAG = "ProgressResponseBody"
    }

    init {
        listener = ProgressInterceptor.LISTENER_MAP[url]
    }
}