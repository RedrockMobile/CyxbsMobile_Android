package com.mredrock.cyxbs.freshman.interfaces.network

import com.mredrock.cyxbs.freshman.bean.CollegeGroupBean
import com.mredrock.cyxbs.freshman.bean.FellowTownsmanGroupBean
import com.mredrock.cyxbs.freshman.bean.OnlineActivityBean
import com.mredrock.cyxbs.freshman.config.*
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Create by yuanbing
 * on 2019/8/3
 */
interface OnlineActivityService {
    @GET(API_ONLINE_COMMUNICATION_ONLINE_ACTIVITY)
    fun requestOnlineActivityActivity(): Observable<OnlineActivityBean>
}

interface CollegeGroupService {
    @GET(API_COLLEGE_GROUP)
    fun requestCollegeGroup(): Observable<CollegeGroupBean>

    @POST(API_SEARCH_COLLEGE)
    @FormUrlEncoded
    fun search(@Field(API_COLLEGE) college: String): Observable<CollegeGroupBean>
}

interface FellowTownsmanGroupService {
    @GET(API_FELLOW_TOWNSMAN_GROUP)
    fun requestFellowTownsmanGroupService(): Observable<FellowTownsmanGroupBean>

    @POST(API_SEARCH_PROVINCE)
    @FormUrlEncoded
    fun search(@Field(API_PROVINCE) province: String): Observable<FellowTownsmanGroupBean>
}