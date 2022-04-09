package com.mredrock.cyxbs.discover.noclass.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface Services {
    @GET(APT_SEARCH_STUDENT)
    fun getStudent(@Query("stu") stu: String): Observable<RedrockApiWrapper<List<Student>>>
}
