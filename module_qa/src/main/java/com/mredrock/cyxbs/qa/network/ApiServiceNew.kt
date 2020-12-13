package com.mredrock.cyxbs.qa.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.qa.beannew.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created By sandyz987 2020/12/1
 */
interface ApiServiceNew {
    @POST("/wxapi/magipoke-loop/post/getDynamicList")
    @FormUrlEncoded
    fun getDynamicList(@Field("topic")
                       topic: String,
                       @Field("page")
                       page: Int,
                       @Field("size")
                       size: Int = 6): Observable<RedrockApiWrapper<List<Dynamic>>>

    @GET("/wxapi/magipoke-loop/ground/getFollowedTopic")
    fun getFollowedTopic(): Observable<RedrockApiWrapper<List<Topic>>>

    @POST("/wxapi/magipoke-loop/ground/getTopicGround")
    @FormUrlEncoded
    fun getTopicGround(@Field("topic_name")
                       topic_name: String, @Field("instruction")
                       instruction: String): Observable<RedrockApiWrapper<List<Topic>>>

    @POST("/wxapi/magipoke-loop/search/getSearchHotWord")
    @FormUrlEncoded
    fun getSearchHotWord(@Field("topic_name")
                         topic_name: String, @Field("search_content")
                         searchContent: String): Observable<RedrockApiWrapper<List<String>>>

    @POST("/wxapi/magipoke-loop/search/getSearchResult")
    @FormUrlEncoded
    fun getSearchResult(@Field("search_content")
                        searchContent: String): Observable<RedrockApiWrapper<SearchResult>>

    /**
     * params存一个<"id", "帖子id">的键值对MutableMap<String, String>
     *     example:
     *         val params: MutableMap<String, Int> = HashMap()
     *         params["id"] = "帖子id"
     * parts存一个MultipartBody.Part
     *     example:
     *
     */

    @POST("/wxapi/magipoke-loop/post/releaseDynamic")
    @Multipart
    fun releaseDynamic(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiWrapper<DynamicReleaseResult>>


    @POST("/wxapi/magipoke-loop/comment/releaseComment")
    @FormUrlEncoded
    fun releaseComment(@Field("content")
                       content: String,
                       @Field("reply_id")
                       replyId: String
    ): Observable<RedrockApiWrapper<CommentReleaseResult>>

    @POST("/wxapi/magipoke-loop/comment/praise")
    fun praise(@Query("id")
               replyId: String
    ): Observable<RedrockApiStatus>

    @POST("/app/index.php/QA/new/ignoreUid")
    @FormUrlEncoded
    fun ignoreUid(@Field("uid")
                  uid: String
    ): Observable<RedrockApiStatus>

    @POST("/app/index.php/QA/new/cancelIgnoreUid")
    @FormUrlEncoded
    fun cancelIgnoreUid(@Field("uid")
                        uid: String
    ): Observable<RedrockApiStatus>

    @POST("/wxapi/magipoke-loop/ground/followTopicGround")
    @FormUrlEncoded
    fun followTopicGround(@Field("topic_id")
                          topicId: String
    ): Observable<RedrockApiStatus>

    @POST("/wxapi/magipoke-loop/comment/report")
    @FormUrlEncoded
    fun report(@Field("id")
               id: String
    ): Observable<RedrockApiStatus>

    @POST("/wxapi/magipoke-loop/comment/deleteId")
    @FormUrlEncoded
    fun deleteId(@Field("id")
                 id: String
    ): Observable<RedrockApiStatus>
}
