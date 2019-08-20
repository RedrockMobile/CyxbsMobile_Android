package com.mredrock.cyxbs.freshman.util.network

import android.content.Context
import android.net.ConnectivityManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.converter.QualifiedTypeConverterFactory
import com.mredrock.cyxbs.common.utils.encrypt.md5Encoding
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.freshman.BuildConfig
import com.mredrock.cyxbs.freshman.config.API_BASE_URL
import com.mredrock.cyxbs.freshman.config.XML_OKHTTP_CACHE
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.lang.Exception
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

/**
 * Create by roger
 * on 2019/8/10
 */
object ApiGenerator {
    private const val DEFAULT_TIME_OUT = 30

    private var retrofit: Retrofit
    private var okHttpClient: OkHttpClient

    init {
        okHttpClient = configureOkHttp(OkHttpClient.Builder())
        retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(QualifiedTypeConverterFactory(GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private fun configureOkHttp(builder: OkHttpClient.Builder): OkHttpClient {
        builder.connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor {
                    val connectivityManager = BaseApp.context.getSystemService(Context.CONNECTIVITY_SERVICE)
                            as ConnectivityManager
                    val networkInfo = connectivityManager.activeNetworkInfo
                    var response: Response
                    if (networkInfo == null || !networkInfo.isConnected) {
                        val request = it.request().newBuilder()
                                .cacheControl(CacheControl.FORCE_CACHE)
                                .build()
                        response = it.proceed(request)
                        response.newBuilder()
                                .removeHeader("Pragma")
                                .header("Cache-Control", "public, only-if-cached, max-stale=${Int.MAX_VALUE}")
                                .build()
                    } else {
                        response = it.proceed(it.request())
                    }
                    response
                }
                .addNetworkInterceptor {
                    val response = it.proceed(it.request())
                    response.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, max-age=60")
                            .build()
                }
                .cache(Cache(BaseApp.context.externalCacheDir.absoluteFile, 10*1024*1024))

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        return builder.build()
    }

    fun <T> getApiService(clazz: Class<T>) = retrofit.create(clazz)

    fun <T> getApiService(retrofit: Retrofit, clazz: Class<T>) = retrofit.create(clazz)
}