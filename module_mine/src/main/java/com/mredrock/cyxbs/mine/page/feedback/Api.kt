package com.mredrock.cyxbs.mine.page.feedback

import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.mine.page.feedback.network.bean.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *@author ZhiQiang Tu
 *@time 2021/9/2  20:04
 *@signature 我们不明前路，却已在路上
 */

val api: ApiService = ApiGenerator.getApiService(getRetrofit(), ApiService::class.java)
var Token = "hello"
/*    get() {
        Log.e("TestTAG", "get value $field", )
        return field
    }
set(value) {
    field = value
    Log.e("TestTAG", "set mToken value == $value")
}*/

fun getRetrofit(): Retrofit {
    val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    Token = getMToken()

//    Log.e("TestTAG", "$Token" )

    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addInterceptor {
//            Log.e("TestTAG", "token in interceptor $Token" )
            val build = it.request()
                .newBuilder()
                .addHeader("Authorization",
                    "Bearer ${getMToken()}")
                .build()

            return@addInterceptor it.proceed(build)
        }
        .build()


    return Retrofit.Builder()
        .baseUrl(getMineBaseUrl())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()
}

fun getMineBaseUrl(): String = "https://be-prod.redrock.cqupt.edu.cn"

fun getMToken(): String = ServiceManager.getService(IAccountService::class.java).getUserTokenService().getToken()
