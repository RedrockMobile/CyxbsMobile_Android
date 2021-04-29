package com.mredrock.cyxbs.volunteer.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.volunteer.bean.*
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("/volunteer-message/binding")
    fun volunteerLogin(@Field("account") account: String,
                       @Field("password") password: String): Observable<VolunteerBase>

    @POST("/volunteer-message/select")
    fun getVolunteerRecord(): Observable<VolunteerTime>

    @GET("/cyb-volunteer/volunteer/activity/info/new")
    fun getVolunteerAffair(): Observable<RedrockApiWrapper<List<VolunteerAffair>>>

    @POST("/volunteer-message/judge")
    fun judgeBind(): Observable<VolunteerJudge>

    @POST("/volunteer-message/unbinding")
    fun unbindVolunteerAccount(): Observable<VolunteerBase>
}