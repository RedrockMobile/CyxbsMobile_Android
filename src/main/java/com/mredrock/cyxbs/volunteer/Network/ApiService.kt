package com.mredrock.cyxbs.volunteer.Network

import com.mredrock.cyxbs.volunteer.bean.PasswordEncode
import com.mredrock.cyxbs.volunteer.bean.VolunteerLogin
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("/zycx/binding")
    fun volunteerLogin(@Header("Authorization") authorization: String,
                       @Field("account") account: String,
                       @Field("password") password: String,
                       @Field("uid") uid: String): Observable<VolunteerLogin>

    @FormUrlEncoded
    @POST("/zycx/select")
    fun getVolunteerRecord(@Header("Authorization") authorization: String,
                           @Field("uid") uid: String): Observable<VolunteerTime>

    @FormUrlEncoded
    @POST("/cryptrsapubkey")
    fun getRsaEncode(@Field("data") data: String,
                     @Field("type") type : String,
                     @Field("arg") arg : String) : Observable<PasswordEncode>
}