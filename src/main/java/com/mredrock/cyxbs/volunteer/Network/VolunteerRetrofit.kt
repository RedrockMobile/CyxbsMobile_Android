package com.mredrock.cyxbs.volunteer.Network

import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.network.converter.QualifiedTypeConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class VolunteerRetrofit {
    companion object {
        private val DEFAULT_TIME_OUT = 5
        val volunteerRetrofit : Retrofit = getRetrofit("https://wx.redrock.team")

        private fun getRetrofit(baseUrl : String) : Retrofit {
            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(configureOkHttp(OkHttpClient.Builder()))
                    .addConverterFactory(QualifiedTypeConverterFactory(GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }

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