package com.mredrock.cyxbs.discover.schoolcar.network

import com.mredrock.cyxbs.discover.schoolcar.bean.SchoolCarLocation
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by glossimar on 2018/9/12
 */

interface ApiService {
    @FormUrlEncoded
    @POST("/234/schoolbus/status")
    fun schoolcar(@Header("Authorization") authorization: String,
                  @Field("s") s: String,
                  @Field("t") t: String,
                  @Field("r") r: String): Observable<SchoolCarLocation>
}