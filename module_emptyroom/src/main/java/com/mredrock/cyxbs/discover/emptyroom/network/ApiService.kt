package com.mredrock.cyxbs.discover.emptyroom.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

/**
 * Created by Cynthia on 2018/9/22
 */
interface ApiService {
    @FormUrlEncoded
    @POST("/magipoke-jwzx/roomEmpty")
    fun getEmpyRooms(
        @Field("weekDayNum") weekday: String,
        @Field("sectionNum") section: String,
        @Field("buildNum") buildNum: String,
        @Field("week") week: String
    ): Observable<RedrockApiWrapper<List<String>>>

}