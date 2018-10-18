package com.mredrock.cyxbs.electricity.network

import com.mredrock.cyxbs.electricity.bean.ElectricityInfo
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
    @POST("/MagicLoop/index.php?s=/addon/ElectricityQuery/ElectricityQuery/queryElecByRoom")
    fun getElectricityInfo(@Field("building") building: String, @Field("room") room: String): Observable<ElectricityInfo>
}