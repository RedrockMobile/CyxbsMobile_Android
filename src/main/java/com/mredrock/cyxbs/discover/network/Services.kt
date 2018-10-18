package com.mredrock.cyxbs.discover.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.API_ROLLER_VIEW
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by zxzhu
 *   2018/9/7.
 *   enjoy it !!
 */
interface Services {

    @FormUrlEncoded
    @POST(API_ROLLER_VIEW)
    fun getRollerViewInfo(@Field("pic_num") pic_num: String): Observable<RedrockApiWrapper<List<RollerViewInfo>>>

}