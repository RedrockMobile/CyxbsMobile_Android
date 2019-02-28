package com.mredrock.cyxbs.discover.calendar.network
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {

    @GET("https://www.showdoc.cc/158569683538010?page_id=934446007248273")
    fun getCalendar(): Observable<RedrockApiWrapper<List<Calendar>>>
}