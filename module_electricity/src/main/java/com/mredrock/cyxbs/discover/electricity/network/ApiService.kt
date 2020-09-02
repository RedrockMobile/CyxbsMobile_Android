package com.mredrock.cyxbs.discover.electricity.network

import com.mredrock.cyxbs.discover.electricity.bean.ElectricityInfo
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Author: Hosigus
 * Date: 2018/9/18 1:12
 * Description: com.mredrock.cyxbs.electricity.network
 */
interface ApiService {
    @FormUrlEncoded
    @POST("/wxapi/magipoke-elecquery/getElectric")
    fun getElectricityInfo(@Field("building") building: String, @Field("room") room: String): Observable<ElectricityInfo>


    @POST("/wxapi/magipoke-elecquery/getElectric")
    fun getElectricityInfo(): Observable<ElectricityInfo>
}