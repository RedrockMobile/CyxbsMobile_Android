package com.mredrock.cyxbs.discover.news.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.news.bean.NewsDetails
import com.mredrock.cyxbs.discover.news.bean.NewsListItem
import io.reactivex.Observable
import okhttp3.Call
import retrofit2.http.*

/**
 * Author: Hosigus
 * Date: 2018/9/23 12:30
 * Description: com.mredrock.cyxbs.discover.news.network
 */
interface ApiService {
    @GET("/api/jwNews/list")
    fun getNewsList(@Query ("page") page: Int): Observable<RedrockApiWrapper<List<NewsListItem>>>

    @GET("/api/jwNews/content")
    fun getNewsDetails(@Query("id") id: Int): Observable<RedrockApiWrapper<NewsDetails>>

    @Streaming
    @GET("/api/file")
    fun download(@Query("id") id: String): Call

}