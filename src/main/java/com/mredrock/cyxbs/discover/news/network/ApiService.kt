package com.mredrock.cyxbs.discover.news.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.news.bean.NewsDetails
import com.mredrock.cyxbs.discover.news.bean.NewsListItem
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Author: Hosigus
 * Date: 2018/9/23 12:30
 * Description: com.mredrock.cyxbs.discover.news.network
 */
interface ApiService {
    @FormUrlEncoded
    @POST("/api/jwNewsList")
    fun getNewsList(@Field("page") page: Int): Observable<RedrockApiWrapper<List<NewsListItem>>>

    @FormUrlEncoded
    @POST("/api/jwNewsContent")
    fun getNewsDetails(@Field("id") id: Int): Observable<RedrockApiWrapper<NewsDetails>>

}