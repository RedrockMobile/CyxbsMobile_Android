package com.mredrock.cyxbs.volunteer.Network

import com.mredrock.cyxbs.volunteer.bean.VolunteerLogin
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/volunteer/binding")
    fun volunteerLogin(@Header("Authorization") authorization: String,
                       @Field("account") account: String,
                       @Field("password") password: String,
                       @Field("uid") uid: String): Observable<VolunteerLogin>

    @FormUrlEncoded
    @POST("/volunteer/select")
    fun getVolunteerRecord(@Header("Authorization") authorization: String,
                           @Field("uid") uid: String): Observable<VolunteerTime>

}