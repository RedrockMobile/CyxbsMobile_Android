package com.mredrock.cyxbs.discover.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.API_ROLLER_VIEW
import com.mredrock.cyxbs.discover.news.bean.NewsListItem
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by zxzhu
 *   2018/9/7.
 *   enjoy it !!
 */
interface Services {

    @GET(API_ROLLER_VIEW)
    fun getRollerViewInfo(): Observable<RedrockApiWrapper<List<RollerViewInfo>>>

    @GET("http://api-234.redrock.team/234/newapi/jwNews/list")
    fun getNewsList(@Query("page") page: Int): Observable<RedrockApiWrapper<List<NewsListItem>>>
}