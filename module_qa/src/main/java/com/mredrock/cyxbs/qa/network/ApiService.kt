package com.mredrock.cyxbs.qa.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.qa.bean.*
import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.beannew.Question
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created By jay68 on 2018/8/26.
 */
interface ApiService {
    /**
     * 草稿箱
     */
    @POST("/magipoke-draft/User/addItemInDraft")
    @FormUrlEncoded
    fun addItemToDraft(@Field("type") type: String,
                       @Field("content") content: String,
                       @Field("target_id") id: String): Observable<RedrockApiStatus>

    @POST("/magipoke-draft/User/updateItemInDraft")
    @FormUrlEncoded
    fun updateDraft(@Field("content") content: String,
                    @Field("id") id: String): Observable<RedrockApiStatus>

    @POST("/magipoke-draft/User/deleteItemInDraft")
    @FormUrlEncoded
    fun deleteDraft(@Field("id") id: String): Observable<RedrockApiStatus>

    @POST("/magipoke-loop/search/getSearchHotWord")
    fun getHotWords(): Observable<RedrockApiWrapper<HotText>>
}
