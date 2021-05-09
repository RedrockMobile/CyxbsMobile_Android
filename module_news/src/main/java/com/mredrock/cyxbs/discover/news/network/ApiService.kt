package com.mredrock.cyxbs.discover.news.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.news.bean.NewsDetails
import com.mredrock.cyxbs.discover.news.bean.NewsListItem
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Author: Hosigus
 * Date: 2018/9/23 12:30
 * Description: com.mredrock.cyxbs.discover.news.network
 */
interface ApiService {
    @GET("/magipoke-jwzx/jwNews/list")
    fun getNewsList(@Query ("page") page: Int): Observable<RedrockApiWrapper<List<NewsListItem>>>

    @GET("/magipoke-jwzx/jwNews/content")
    fun getNewsDetails(@Query("id") id: String,
                       @Query("forceFetch") fetch: Boolean = true): Observable<RedrockApiWrapper<NewsDetails>>

    @Streaming
    @GET("/magipoke-jwzx/jwNews/file")
    fun download(@Query("id") id: String): Call<ResponseBody>
}