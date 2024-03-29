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
    fun getEmpyRooms(@Field("week") week: Int,
                      @Field("weekDayNum") weekday: Int,
                      @Field("buildNum") buildNum: Int,
                      @Field("sectionNum") section: String): Observable<RedrockApiWrapper<List<String>>>

}