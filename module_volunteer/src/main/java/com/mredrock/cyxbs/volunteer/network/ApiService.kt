package com.mredrock.cyxbs.volunteer.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffair
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffairDetail
import com.mredrock.cyxbs.volunteer.bean.VolunteerLogin
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/wxapi/volunteer/binding")
    fun volunteerLogin(@Header("Authorization") authorization: String,
                       @Field("account") account: String,
                       @Field("password") password: String,
                       @Field("uid") uid: String): Observable<VolunteerLogin>

    @FormUrlEncoded
    @POST("/wxapi/volunteer/select")
    fun getVolunteerRecord(@Header("Authorization") authorization: String,
                           @Field("uid") uid: String): Observable<VolunteerTime>

    @GET("/wxapi/cyb-volunteer/volunteer/activities")
    fun getVolunteerAffair(): Observable<RedrockApiWrapper<List<VolunteerAffair>>>


    @FormUrlEncoded
    @POST("/wxapi/cyb-volunteer/volunteer/activity/info")
    fun getVolunteerAffairDetail(@Field("id") id: Int): Observable<RedrockApiWrapper<VolunteerAffairDetail>>
}