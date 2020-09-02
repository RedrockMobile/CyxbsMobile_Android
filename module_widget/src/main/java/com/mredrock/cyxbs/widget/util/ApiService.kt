package com.mredrock.cyxbs.widget.util

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by zia on 2018/10/13.
 */
interface ApiService {

    @FormUrlEncoded
    @POST("/redapi2/api/kebiao")
    fun getCourse(@Field("stuNum") stuNum: String): Call<String>
}