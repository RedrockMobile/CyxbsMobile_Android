package com.mredrock.cyxbs.discover.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.API_ROLLER_VIEW
import com.mredrock.cyxbs.discover.bean.NewsListItem
import com.mredrock.cyxbs.discover.bean.UnreadData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by zxzhu
 *   2018/9/7.
 *   enjoy it !!
 */
interface Services {

    @GET(API_ROLLER_VIEW)
    fun getRollerViewInfo(): Observable<RedrockApiWrapper<List<RollerViewInfo>>>

    @GET("/magipoke-jwzx/jwNews/list")
    fun getNewsList(@Query("page") page: Int): Observable<RedrockApiWrapper<List<NewsListItem>>>

    /**
     * 是否有未读信息
     */
    @GET("/message-system/user/msgHasRead")
    fun getHashUnreadMsg() : Observable<RedrockApiWrapper<UnreadData>>
}