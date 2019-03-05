package com.mredrock.cyxbs.discover.calendar.network
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {

    @GET("http://wx.yyeke.com/api/search/picture/xiaoli?year=2019")
    fun getCalendar(): Observable<RedrockApiWrapper<List<Calendar>>>
}