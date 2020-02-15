package com.mredrock.cyxbs.common.network

import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.common.network.converter.QualifiedTypeConverterFactory
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by AceMurder on 2018/1/24.
 */
object ApiGenerator {
    private const val DEFAULT_TIME_OUT = 30

    private var retrofit: Retrofit
    private var commonRetrofit: Retrofit
    private var okHttpClient: OkHttpClient
    @Volatile
    var token = ""
    @Volatile
    var refreshToken = ""

    init {
        token = ServiceManager.getService(IAccountService::class.java)?.getUserTokenService()?.getToken()
                ?: ""
        refreshToken = ServiceManager.getService(IAccountService::class.java)?.getUserTokenService()?.getRefreshToken()
                ?: ""
        okHttpClient = configureOkHttp(OkHttpClient.Builder())
        retrofit = Retrofit.Builder()
                .baseUrl(END_POINT_REDROCK)
                .client(okHttpClient)
                .addConverterFactory(QualifiedTypeConverterFactory(GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        commonRetrofit = commonApiService()
    }

    private fun configureOkHttp(builder: OkHttpClient.Builder): OkHttpClient {
        builder.apply {
            connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
            interceptors().add(Interceptor {
                /**
                 * 所有请求添加token到header，如果有更好方式再改改，或者另外加一个不需要token的ApiGenerator
                 */
                val response = it.proceed(it.request().newBuilder().header("Authorization", "Bearer $token").build())
                checkRefresh(response, it)
            })
        }
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        return builder.build()
    }

    @Synchronized
    private fun checkRefresh(response: Response, chain: Interceptor.Chain): Response? {
        var newResponse = response
        /**
         * 刷新token条件设置为，已有refreshToken，并且已经过期，也可以后端返回特定到token失效code
         */
        if (refreshToken.isNotEmpty() && isTokenExpired()) {
            ServiceManager.getService(IAccountService::class.java).getVerifyService().refresh(
                    onError = {
                        response.close()
                        newResponse = response
                    },
                    action = { s: String, s1: String ->
                        response.close()
                        token = s
                        refreshToken = s1
                        newResponse = chain.run { proceed(chain.request().newBuilder().header("Authorization", "Bearer $s").build()) }

                    }
            )
        }
        return newResponse
    }

    private fun commonApiService(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(END_POINT_REDROCK)
                .client(OkHttpClient().newBuilder().apply {
                    connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.level = HttpLoggingInterceptor.Level.BODY
                        addInterceptor(logging)
                    }
                }.build())
                .addConverterFactory(QualifiedTypeConverterFactory(GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private fun isTokenExpired() = ServiceManager.getService(IAccountService::class.java).getVerifyService().isExpired()
    fun <T> getApiService(clazz: Class<T>) = retrofit.create(clazz)

    fun <T> getApiService(retrofit: Retrofit, clazz: Class<T>) = retrofit.create(clazz)

    fun <T> getCommonApiService(clazz: Class<T>) = commonRetrofit.create(clazz)

}