package com.mredrock.cyxbs.schoolcar.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.schoolcar.bean.MapLines
import com.mredrock.cyxbs.schoolcar.bean.MapLinesVersion
import com.mredrock.cyxbs.schoolcar.bean.SchoolCarLocation
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

/**
 * Created by glossimar on 2018/9/12
 */

interface ApiService {
    @FormUrlEncoded
    @POST("/schoolbus/status")
    fun schoolcar(@Header("Authorization") authorization: String,
                  @Field("s") s: String,
                  @Field("t") t: String,
                  @Field("r") r: String): Observable<RedrockApiWrapper<SchoolCarLocation>>


    //获得校车信息版本号
    @GET("/schoolbus/map/version")
    fun schoolSiteVersion(): Observable<RedrockApiWrapper<MapLinesVersion>>

    //获得地图信息
    @GET("/schoolbus/map/line")
    fun schoolSite(): Observable<RedrockApiWrapper<MapLines>>


}