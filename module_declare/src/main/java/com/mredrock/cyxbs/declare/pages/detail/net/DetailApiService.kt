package com.mredrock.cyxbs.declare.pages.detail.net

import com.mredrock.cyxbs.declare.pages.detail.bean.CancelChoiceBean
import com.mredrock.cyxbs.declare.pages.detail.bean.DetailBean
import com.mredrock.cyxbs.declare.pages.detail.bean.VotedBean
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * ... 投票详细页面的Api
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/13
 * @Description:
 */
interface DetailApiService : IApi {
    /**
     * 获取详细投票的数据
     */
    @GET("/magipoke-attitude/declare")
    fun getDetailDeclareData(
        @Query("id") id: Int
    ): Single<ApiWrapper<DetailBean>>

    /**
     * 投票
     */
    @FormUrlEncoded
    @PUT("/magipoke-attitude/declare")
    fun putChoice(
        @Field("id") id: Int,
        @Field("choice") choice: String
    ): Single<ApiWrapper<VotedBean>>

    /**
     * 取消投票
     */
    @DELETE("/magipoke-attitude/declare")
    fun cancelChoice(
        @Query("id") id: Int
    ): Single<ApiWrapper<CancelChoiceBean>>
}
