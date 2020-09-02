package com.mredrock.cyxbs.volunteer.network

import com.mredrock.cyxbs.common.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class VolunteerRetrofit {
    companion object {
        private val DEFAULT_TIME_OUT = 5

        private fun configureOkHttp(builder: OkHttpClient.Builder): OkHttpClient {
            builder.connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(logging)
            }
            return builder.build()
        }
    }
}