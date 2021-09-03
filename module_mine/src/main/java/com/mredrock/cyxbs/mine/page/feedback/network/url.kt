package com.mredrock.cyxbs.mine.page.feedback.network

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
   val url = "eyJEYXRhIjp7ImdlbmRlciI6IueUtyIsInN0dV9udW0iOiIyMDIwMjEwMjY3In0sIkRvbWFpbiI6Im1hZ2lwb2tlIiwiUmVkaWQiOiJmNDQyZDM5ZWJhYmQzZDNiMTNmNjVhYWU1Zjc2Mzk0ZGYxOWMyZWVhIiwiZXhwIjoiNzQwMDQxMDE5OSIsImlhdCI6IjE2MzA1OTg5NDYiLCJzdWIiOiJ3ZWIifQ==.ZJjuCZ9M8QpJcW97iO+srRWEXbTowGs93A1hAASAUJ2w4yCXh94l0mSTuCpnO7+SIulTkPA7fSzJPEmZP1fHaU1iiLpex0/3yukIvV1yAl/np3asEx47WptAHNsLn5kLT0yKFvgBmRC7rVY1cK63T6glTO3rMDWHkD6fmvBNIMi15X9yQTezzlvmuKPPi471yqDt5WhAyLQoHptIdp0641K+hlJCi+SlV1903n/TReZ60A7Q2qdjfRzP+c+SwSRxKzwO2oSjLfAC1Trsm5SsiFYMVjfSuvYrH8Ob8vfMhmrjaOrkIpsPATs5hOX5BQGtRNbxjjlG7yqBx098d+DZLQ=="
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