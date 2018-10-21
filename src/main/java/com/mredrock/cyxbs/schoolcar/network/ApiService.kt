package com.mredrock.cyxbs.schoolcar.network

import com.mredrock.cyxbs.schoolcar.bean.SchoolCarLocation
import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.http.*

/**
 * Created by glossimar on 2018/9/12
 */

interface ApiService{
    @FormUrlEncoded
    @POST("/extension/test")
    fun schoolcar(@Header("Authorization") authorization: String,
                           @Field("s") s: String,
                           @Field("t") t: String,
                           @Field("r") r: String): Observable<SchoolCarLocation>
}