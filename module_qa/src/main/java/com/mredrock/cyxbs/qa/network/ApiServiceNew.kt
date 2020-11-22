package com.mredrock.cyxbs.qa.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.qa.bean.*
import com.mredrock.cyxbs.qa.beannew.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created By jay68 on 2018/8/26.
 */
interface ApiServiceNew {
    @POST("/app/index.php/QA/new/getDynamicList")
    @FormUrlEncoded
    fun getDynamicList(@Field("topic")
                       topic: String,
                       @Field("page")
                       page: Int,
                       @Field("size")
                       size: Int = 6): Observable<RedrockApiWrapper<List<Dynamic>>>

    @POST("/app/index.php/QA/new/getFollowedTopic")
    @FormUrlEncoded
    fun getFollowedTopic(): Observable<RedrockApiWrapper<List<Topic>>>

    @POST("/app/index.php/QA/new/getTopicGround")
    @FormUrlEncoded
    fun getTopicGround(): Observable<RedrockApiWrapper<List<Topic>>>

    @POST("/app/index.php/QA/new/getSearchHotWord")
    @FormUrlEncoded
    fun getSearchHotWord(): Observable<RedrockApiWrapper<List<String>>>

    @POST("/app/index.php/QA/new/getSearchResult")
    @FormUrlEncoded
    fun getSearchResult(@Field("search_content")
                        searchContent: String): Observable<RedrockApiWrapper<SearchResult>>

    @POST("/app/index.php/QA/new/releaseDynamic")
    @FormUrlEncoded
    fun releaseDynamic(@Field("content")
                       content: String,
                       @Field("topic_id")
                       topicId: String
    ): Observable<RedrockApiWrapper<DynamicReleaseResult>>

    /**
     * params存一个<"id", "帖子id">的键值对MutableMap<String, String>
     *     example:
     *         val params: MutableMap<String, Int> = HashMap()
     *         params["id"] = "帖子id"
     * parts存一个MultipartBody.Part
     *     example:
     *
     */
    @POST("app/index.php/QA/new/Question/uploadPicture")
    @Multipart
    fun uploadQuestionPic(@Part parts: List<MultipartBody.Part>, @PartMap params: Map<String, String>): Observable<RedrockApiStatus>


    @POST("/app/index.php/QA/new/releaseComment")
    @FormUrlEncoded
    fun releaseComment(@Field("content")
                       content: String,
                       @Field("reply_id")
                       replyId: String
    ): Observable<RedrockApiWrapper<CommentReleaseResult>>

    @POST("/app/index.php/QA/new/praise")
    @FormUrlEncoded
    fun praise(@Field("id")
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

    @POST("/app/index.php/QA/new/report")
    @FormUrlEncoded
    fun report(@Field("id")
               id: String
    ): Observable<RedrockApiStatus>

    @POST("/app/index.php/QA/new/deleteId")
    @FormUrlEncoded
    fun deleteId(@Field("id")
                 id: String
    ): Observable<RedrockApiStatus>

}
