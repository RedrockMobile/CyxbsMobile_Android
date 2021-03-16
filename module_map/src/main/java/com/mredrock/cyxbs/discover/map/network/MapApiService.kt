package com.mredrock.cyxbs.discover.map.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.map.bean.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 *@author zhangzhe
 *@date 2020/8/8
 *@description 掌邮地图网络请求，需要用getApiService获得而不是getCommonApiService（如果有token）
 */

internal interface MapApiService {
    @GET("/magipoke-stumap/basic")
    fun getMapInfo(): Observable<RedrockApiWrapper<MapInfo>>

    @FormUrlEncoded
    @POST("/magipoke-stumap/detailsite")
    fun getPlaceDetails(@Field("place_id") placeId: String): Observable<RedrockApiWrapper<PlaceDetails>>

    @GET("/magipoke-stumap/button")
    fun getButtonInfo(): Observable<RedrockApiWrapper<ButtonInfo>>

    @FormUrlEncoded
    @POST("/magipoke-stumap/searchtype")
    fun getSearchType(@Field("code") code: String): Observable<RedrockApiWrapper<MutableList<String>>>

    @FormUrlEncoded
    @POST("/magipoke-stumap/addhot")
    fun addHot(@Field("id") placeId: Int): Observable<RedrockApiStatus>

    @GET("/magipoke-stumap/rockmap/collect")
    fun getCollect(): Observable<RedrockApiWrapper<FavoritePlaceSimple>>

    @FormUrlEncoded
    @PATCH("/magipoke-stumap/rockmap/addkeep")
    fun addCollect(@Field("place_id") placeId: String): Observable<RedrockApiStatus>

    @Multipart
    @HTTP(method = "DELETE", path = "/magipoke-stumap/rockmap/deletekeep", hasBody = true)
    fun deleteCollect(@Part("place_id") placeId: Int): Observable<RedrockApiStatus>

    @Multipart
    @POST("/magipoke-stumap/rockmap/upload")
    fun uploadPicture(@Part photo: MultipartBody.Part, @PartMap params: Map<String, Int>): Observable<RedrockApiStatus>

    @FormUrlEncoded
    @POST("/magipoke-stumap/placesearch")
    fun placeSearch(@Field("place_search") placeSearch: String): Observable<RedrockApiWrapper<PlaceSearch>>
}