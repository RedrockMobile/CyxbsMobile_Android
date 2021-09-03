package com.mredrock.cyxbs.mine.page.feedback.network.bean

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @Date : 2021/9/2   21:50
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
val url = "eyJEYXRhIjp7ImdlbmRlciI6IueUtyIsInN0dV9udW0iOiIyMDIwMjEwMjY3In0sIkRvbWFpbiI6Im1hZ2lwb2tlIiwiUmVkaWQiOiJmNDQyZDM5ZWJhYmQzZDNiMTNmNjVhYWU1Zjc2Mzk0ZGYxOWMyZWVhIiwiZXhwIjoiNzQwMDQwMTk3NSIsImlhdCI6IjE2MzA1OTA3MjIiLCJzdWIiOiJ3ZWIifQ==.FklRdCopqJVCzHHPwgT8QEStxv03YAeWOjsxB4Y2EyhnLwqxOQB4spPGB8Q7b8aEm8qBj2TSaq+c6A4yBl7sQoL1TrrlSzgl4NtfvKB9GOMWzJeYSWxz6nbWCBb4a4PlaIcauqyVLzHyrAedaLM5MkkOEUDmeYbqbNTSRo4F0yTfFm5Z6ZpRRISsLgHGds+RFQ8j3/EDQ4jAUeBWfLYB/K8fZhBywq0MCDjdYWZLnkj+ReUQwIIIsFvHEHwEJPqqk9bmcaTRQ82jUPgpt303RP2J23uimTDqQ8u/dDxL6cAahO7JJNM7r2+Bmml6bMXhNPYAFZ452omJAtMAMZVMRg=="
fun provideRetrofit(): Retrofit {
    val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addInterceptor{
            val build = it.request()
                .newBuilder()
                .addHeader("Authorization",
                    "Bearer $url")
                .build()
            return@addInterceptor it.proceed(build)
        }
        .build()

    return Retrofit.Builder()
        .baseUrl("https://be-prod.redrock.cqupt.edu.cn")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}