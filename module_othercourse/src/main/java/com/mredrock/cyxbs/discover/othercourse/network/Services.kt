package com.mredrock.cyxbs.discover.othercourse.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface Services {
    @GET(APT_SEARCH_STUDENT)
    fun getStudent(@Query("stu") stu: String): Observable<RedrockApiWrapper<List<Student>>>


    @POST(APT_SEARCH_TEACHER)
    @FormUrlEncoded
    fun getTeacher(@Field("teaName") teacher: String): Observable<RedrockApiWrapper<List<Teacher>>>
}
