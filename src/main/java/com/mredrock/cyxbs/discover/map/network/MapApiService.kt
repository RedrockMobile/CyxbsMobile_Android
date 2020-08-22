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
    @GET("wxapi/magipoke-stumap/basic")
    fun getMapInfo(): Observable<RedrockApiWrapper<MapInfo>>

    @FormUrlEncoded
    @POST("wxapi/magipoke-stumap/detailsite")
    fun getPlaceDetails(@Field("place_id") placeId: String): Observable<RedrockApiWrapper<PlaceDetails>>

    @GET("wxapi/magipoke-stumap/button")
    fun getButtonInfo(): Observable<RedrockApiWrapper<ButtonInfo>>

    @FormUrlEncoded
    @POST("wxapi/magipoke-stumap/searchtype")
    fun getSearchType(@Field("code") code: String): Observable<RedrockApiWrapper<MutableList<String>>>

    @GET("wxapi/magipoke-stumap/rockmap/collect")
    fun getCollect(): Observable<RedrockApiWrapper<FavoritePlaceSimple>>

    @FormUrlEncoded
    @PATCH("wxapi/magipoke-stumap/rockmap/addkeep")
    fun addCollect(@Field("place_id") placeId: String): Observable<RedrockApiStatus>

    @Multipart
    @HTTP(method = "DELETE", path = "wxapi/magipoke-stumap/rockmap/deletekeep", hasBody = true)
    fun deleteCollect(@Part("place_id") placeId: Int): Observable<RedrockApiStatus>

    @Multipart
    @POST("wxapi/magipoke-stumap/rockmap/upload")
    fun uploadPicture(@Part photo: MultipartBody.Part, @PartMap params: Map<String, Int>): Observable<RedrockApiStatus>

    @FormUrlEncoded
    @POST("wxapi/magipoke-stumap/placesearch")
    fun placeSearch(@Field("place_search") placeSearch: String): Observable<RedrockApiWrapper<PlaceSearch>>
}