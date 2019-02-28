package com.mredrock.cyxbs.discover.emptyroom.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Cynthia on 2018/9/22
 */
interface ApiService {
    @FormUrlEncoded
    @POST("/api/roomEmpty")
    fun getEmptyRooms(@Field("week") week: Int,
                      @Field("weekdayNum") weekday: Int,
                      @Field("buildNum") buildNum: Int,
                      @Field("sectionNum") section: String): Observable<RedrockApiWrapper<List<String>>>

}