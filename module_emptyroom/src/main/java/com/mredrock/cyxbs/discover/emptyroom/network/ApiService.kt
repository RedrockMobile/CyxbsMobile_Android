package com.mredrock.cyxbs.discover.emptyroom.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.emptyroom.bean.EmptyClassRoom
import io.reactivex.Observable
import org.json.JSONArray
import retrofit2.http.*

/**
 * Created by Cynthia on 2018/9/22
 */
interface ApiService {
    @GET("/renewapi/roomEmpty")
    fun getEmptyRooms(@Query("week") week: Int,
                      @Query("weekDayNum") weekday: Int,
                      @Query("buildNum") buildNum: Int,
                      @Query("sectionNum") section: String): Observable<RedrockApiWrapper<List<EmptyClassRoom>>>

}