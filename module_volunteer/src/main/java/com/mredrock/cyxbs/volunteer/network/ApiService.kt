package com.mredrock.cyxbs.volunteer.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.volunteer.bean.*
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("/wxapi/volunteer-message/binding")
    fun volunteerLogin(@Field("account") account: String,
                       @Field("password") password: String): Observable<VolunteerLogin>

    @POST("/wxapi/volunteer-message/select")
    fun getVolunteerRecord(): Observable<VolunteerTime>

    @POST("/wxapi/cyb-volunteer/volunteer/activity/info/new")
    fun getVolunteerAffair(): Observable<RedrockApiWrapper<List<VolunteerAffair>>>


    @FormUrlEncoded
    @POST("/wxapi/cyb-volunteer/volunteer/activity/info")
    fun getVolunteerAffairDetail(@Field("id") id: Int): Observable<RedrockApiWrapper<VolunteerAffairDetail>>

    @POST("/wxapi/volunteer-message/judge")
    fun judgeBind(): Observable<VolunteerJudge>
}