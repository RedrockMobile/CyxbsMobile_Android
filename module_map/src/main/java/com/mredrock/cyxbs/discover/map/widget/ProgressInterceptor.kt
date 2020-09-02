package com.mredrock.cyxbs.discover.map.widget

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

class ProgressInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val url = request.url.toString()
        val body = response.body
        return response.newBuilder().body(body?.let { ProgressResponseBody(url, it) }).build()
    }

    companion object {
        @JvmField
        val LISTENER_MAP: MutableMap<String, ProgressListener> = HashMap()
        fun addListener(url: String, listener: ProgressListener) {
            LISTENER_MAP[url] = listener
        }

        fun removeListener(url: String) {
            LISTENER_MAP.remove(url)
        }
    }
}