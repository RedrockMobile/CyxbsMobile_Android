package com.mredrock.cyxbs.calendar.network
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {

    @GET("http://wx.yyeke.com/api/search/picture/xiaoli?year=2018")
    fun getCalendar(): Observable<RedrockApiWrapper<List<Calendar>>>
}