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
    @POST("/app/index.php/QA/Question/getQuestionList")
    @FormUrlEncoded
    fun getQuestionList(@Field("kind")
                        kind: String,
                        @Field("page")
                        page: Int,
                        @Field("size")
                        size: Int = 6): Observable<RedrockApiWrapper<List<Question>>>

    /**
     * 草稿箱
     */
    @POST("wxapi/magipoke-draft/User/addItemInDraft")
    @FormUrlEncoded
    fun addItemToDraft(@Field("type") type: String,
                       @Field("content") content: String,
                       @Field("target_id") id: String): Observable<RedrockApiStatus>

    @POST("wxapi/magipoke-draft/User/updateItemInDraft")
    @FormUrlEncoded
    fun updateDraft(@Field("content") content: String,
                    @Field("id") id: String): Observable<RedrockApiStatus>

    @POST("wxapi/magipoke-draft/User/deleteItemInDraft")
    @FormUrlEncoded
    fun deleteDraft(@Field("id") id: String): Observable<RedrockApiStatus>


    @FormUrlEncoded
    @POST("app/index.php/QA/Question/getHotQuestion")
    fun getHotQuestion(@Field("page")
                       page: Int,
                       @Field("size")
                       size: Int = 6): Observable<RedrockApiWrapper<List<HotQA>>>


    @FormUrlEncoded
    @POST("/app/index.php/QA/Search/getQuestionList")
    fun getSearchedQuestionList(@Field("search_key")
                                searchKey: String,
                                @Field("page")
                                page: Int,
                                @Field("size")
                                size: Int = 6): Observable<RedrockApiWrapper<List<Question>>>

    @POST("/app/index.php/QA/Search/getHotWords")
    fun getHotWords(): Observable<RedrockApiWrapper<HotText>>
}
