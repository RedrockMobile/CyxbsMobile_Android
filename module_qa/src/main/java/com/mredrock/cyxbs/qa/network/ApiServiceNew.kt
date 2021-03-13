package com.mredrock.cyxbs.qa.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.qa.beannew.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created By zhangzhe 2020/12/1
 */
interface ApiServiceNew {
    //圈子详情里面的最新和热门帖子的接口
    @GET("/wxapi/magipoke-loop/post/getLoopPage")
    fun getCircleDynamicList(@Query("loop")
                             loop: Int,
                             @Query("page")
                             page: Int,
                             @Query("size")
                             size: Int = 6,
                             @Query("type")
                             type: String):Observable<RedrockApiWrapper<List<Dynamic>>>

    @GET("/wxapi/magipoke-loop/post/getMainPage")
    fun getDynamicList(@Query("type")
                       type: String,
                       @Query("page")
                       page: Int,
                       @Query("size")
                       size: Int = 6): Observable<RedrockApiWrapper<List<Dynamic>>>

    @GET("/wxapi/magipoke-loop/ground/getFollowedTopic")
    fun getFollowedTopic(): Observable<RedrockApiWrapper<List<Topic>>>

    @POST("/wxapi/magipoke-loop/ground/getTopicGround")
    @FormUrlEncoded
    fun getTopicGround(@Field("topic_name")
                       topic_name: String, @Field("instruction")
                       instruction: String): Observable<RedrockApiWrapper<List<Topic>>>

    @GET("/wxapi/magipoke-loop/search/getSearchHotWord")
    fun getSearchHotWord(): Observable<RedrockApiWrapper<SearchHotWord>>

    @GET("/wxapi/magipoke-loop/search/searchPost")
    fun getSearchResult(@Query("key")
                        searchContent: String,
                        @Query("page") page: Int,
                        @Query("size") size: Int): Observable<RedrockApiWrapper<List<Dynamic>>>

    @GET("/wxapi/magipoke-loop/search/searchKnowledge")
    fun getSearchKnowledge(@Query("key") searchKey: String,
                           @Query("page") page: Int,
                           @Query("size") size: Int): Observable<RedrockApiWrapper<List<Knowledge>>>

    @GET("/wxapi/magipoke-loop/ground/getUnreadCount")
    fun getTopicMessage(@Query("last") last: String): Observable<RedrockApiWrapper<List<TopicMessage>>>

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
    @Multipart
    fun releaseComment(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiWrapper<CommentReleaseResult>>

    @POST("/wxapi/magipoke-loop/comment/releaseComment")
    @FormUrlEncoded
    fun releaseComment(@Field("content")
                       content: String,
                       @Field("post_id")
                       postId: String,
                       @Field("reply_id")
                       replyId: String
    ): Observable<RedrockApiWrapper<CommentReleaseResult>>

    @POST("/wxapi/magipoke-loop/comment/praise")
    @FormUrlEncoded
    fun praise(@Field("id")
               replyId: String, @Field("model") model: String
    ): Observable<RedrockApiStatus>

    @POST("/wxapi/magipoke-loop/ignore/addIgnoreUid")
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
    fun followTopicGround(@Field("topic_name") topicName: String
    ): Observable<RedrockApiStatus>

    @POST("/wxapi/magipoke-loop/comment/report")
    @FormUrlEncoded
    fun report(@Field("id")
               id: String, @Field("model") model: String, @Field("content") content: String
    ): Observable<RedrockApiStatus>

    @POST("/wxapi/magipoke-loop/comment/deleteId")
    @FormUrlEncoded
    fun deleteId(@Field("id")
                 id: String,
                 @Field("model")
                 model: String
    ): Observable<RedrockApiStatus>

    @GET("/wxapi/magipoke-loop/comment/getallcomment")
    fun getComment(@Query("post_id")
                   postId: String
    ): Observable<RedrockApiWrapper<List<Comment>>>

    @GET("/wxapi/magipoke-loop/post/getPostInfo")
    fun getPostInfo(@Query("id")
                    postId: String
    ): Observable<RedrockApiWrapper<Dynamic>>

    /**
     * 获取用户收到的回复
     */
    @FormUrlEncoded
    @POST("/wxapi/magipoke-loop/user/replyme")
    fun getUserReplay(
            @Field("page") page: Int,
            @Field("size") size: Int,
            @Field("type") type: Int
    ): Observable<RedrockApiWrapper<List<CommentWrapper>>>

    /**
     * 获取用户收到的点赞
     */
    @FormUrlEncoded
    @POST("/wxapi/magipoke-loop/user/praisedme")
    fun getUserPraise(
            @Field("page") page: Int,
            @Field("size") size: Int,
            @Field("type") type: Int
    ): Observable<RedrockApiWrapper<List<Praise>>>

    /**
     * 获取被屏蔽的用户
     */
    @FormUrlEncoded
    @POST("/wxapi/magipoke-loop/user/getIgnoreUid")
    fun getIgnoreUid(
            @Field("page") page: Int,
            @Field("size") size: Int
    ): Observable<RedrockApiWrapper<List<Ignore>>>

    /**
     * 获取用户动态
     */
    @GET("/wxapi/magipoke-loop/user/getUserPostList")
    fun getUserDynamic(
            @Query("page") page: Int,
            @Query("size") size: Int
    ): Observable<RedrockApiWrapper<List<Dynamic>>>

}
