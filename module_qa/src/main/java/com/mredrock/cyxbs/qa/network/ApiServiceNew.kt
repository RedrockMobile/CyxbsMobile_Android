package com.mredrock.cyxbs.qa.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.mine.network.model.DynamicDraft
import com.mredrock.cyxbs.qa.beannew.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created By zhangzhe 2020/12/1
 */
interface ApiServiceNew {
    //获取单个动态信息
    @GET("/magipoke-loop/post")
    fun getPostInfo(
        @Query("id")
        postId: String
    ): Observable<RedrockApiWrapper<Dynamic>>

    /**
     * 发布动态
     * params存一个<"id", "帖子id">的键值对MutableMap<String, String>
     *     example:
     *         val params: MutableMap<String, Int> = HashMap()
     *         params["id"] = "帖子id"
     * parts存一个MultipartBody.Part
     *     example:
     *
     */
    @POST("/magipoke-loop/post")
    @Multipart
    fun releaseDynamic(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiWrapper<DynamicReleaseResult>>

//    //更新动态信息
//    @PUT("/magipoke-loop/post")
//    @FormUrlEncoded

    //获取首页动态
    @GET("/magipoke-loop/post/homepage")
    fun getDynamicList(
        @Query("page")
        page: Int,
        @Query("size")
        size: Int = 6,
        @Header("App-Version")
        versionCode: Long = 0L
    ): Observable<RedrockApiWrapper<Array<MessageWrapper>>>

    //获取关注的人的动态
    @GET("/magipoke-loop/post/focus")
    fun getFocusDynamicList(
        @Query("page")
        page: Int,
        @Query("size")
        size: Int = 6
    ): Observable<RedrockApiWrapper<List<Dynamic>>>

    //获取指定用户的动态
    @GET("/magipoke-loop/post/user")
    fun getPersoalDynamic(
        @Query("redid") redid:String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Observable<RedrockApiWrapper<List<Dynamic>>>

    //获取某个圈子详情里面的最新和热门动态
    @GET("/magipoke-loop/post/ground")
    fun getCircleDynamicList(
        @Query("loop")
        loop: Int,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int = 6,
        @Query("type")
        type: String
    ): Observable<RedrockApiWrapper<List<Dynamic>>>

    //举报动态
    @POST("/magipoke-loop/report/post")
    @FormUrlEncoded
    fun reportDynamic(
        @Field("id")
        id: String,
        @Field("content") content: String
    ): Observable<RedrockApiStatus>

    //删除动态
    @DELETE("/magipoke-loop/post")
    fun deleteDynamic(@Query("id") id: String): Observable<RedrockApiStatus>

    //获取某个动态的评论(旧)
    @GET("/magipoke-loop/comment/getallcomment")
    fun getComment(
        @Query("post_id")
        postId: String
    ): Observable<RedrockApiWrapper<List<Comment>>>

    // TODO: 获取 某个动态的评论 或 某个评论的评论(分页)
    @GET("/magipoke-loop/comment")
    fun getComment(
        @Query("target_id")
        postId: String,
        @Query("comment_type")
        commentType: Int,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): Observable<RedrockApiWrapper<List<Comment>>>

    //发布评论
    @POST("/magipoke-loop/comment")
    @Multipart
    fun releaseComment(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiWrapper<CommentReleaseResult>>

    //删除某个动态的评论
    @DELETE("/magipoke-loop/comment")
    fun deleteComment(@Query("id") id: String): Observable<RedrockApiStatus>

    //举报评论
    @POST("/magipoke-loop/report/comment")
    @FormUrlEncoded
    fun reportComment(
        @Field("id")
        id: String,
        @Field("content") content: String
    ): Observable<RedrockApiStatus>

    //获取所有圈子广场
    @GET("/magipoke-loop/ground/all")
    fun getTopicGround(
        @Query("topic_name")
        topic_name: String,
        @Query("instruction")
        instruction: String
    ): Observable<RedrockApiWrapper<List<Topic>>>

    //获取所有关注的圈子广场
    @GET("/magipoke-loop/ground/follow")
    fun getFollowedTopic(): Observable<RedrockApiWrapper<List<Topic>>>

    //关注/取关一个圈子广场
    @POST("/magipoke-loop/ground/follow")
    @FormUrlEncoded
    fun followTopicGround(@Field("topic_name") topicName: String): Observable<RedrockApiStatus>

    //获取关注的圈子广场的未读信息
    @GET("/magipoke-loop/ground/unreadCount")
    fun getTopicMessage(@Query("last") last: String): Observable<RedrockApiWrapper<List<TopicMessage>>>

    //获取全部搜索热词
    @GET("/magipoke-loop/search/hotWord")
    fun getSearchHotWord(): Observable<RedrockApiWrapper<SearchHotWord>>

    //搜索帖子
    @GET("/magipoke-loop/search/post")
    fun getSearchResult(
        @Query("key")
        searchContent: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Observable<RedrockApiWrapper<List<Dynamic>>>

    //搜索知识库
    @GET("/magipoke-loop/search/knowledge")
    fun getSearchKnowledge(
        @Query("key") searchKey: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Observable<RedrockApiWrapper<List<Knowledge>>>

    //搜索用户
    @GET("/magipoke-loop/search/user")
    fun getSearchUsers(@Query("key") key: String):Observable<RedrockApiWrapper<List<UserBrief>>>

    //搜索所有的东西


    @FormUrlEncoded
    @POST("/magipoke-loop/user/focus")
    fun changeFocusStatus(@Field("redid") redid: String):Observable<RedrockApiStatus>

    @PUT("/magipoke-loop/post/dynamic")
    @Multipart
    fun modificationDynamic(@Part parts: List<MultipartBody.Part>): Observable<DynamicReleaseResult>

    // 发布评论
    @POST("/magipoke-loop/comment")
    @FormUrlEncoded
    fun releaseComment(
        @Field("content")
        content: String,
        @Field("post_id")
        postId: String,
        @Field("reply_id")
        replyId: String
    ): Observable<RedrockApiWrapper<CommentReleaseResult>>

    // 点赞动态
    @POST("/magipoke-loop/praise/post")
    @FormUrlEncoded
    fun praiseDynamic(
        @Field("id")
        replyId: String
    ): Observable<RedrockApiStatus>

    // 点赞评论
    @POST("/magipoke-loop/praise/comment")
    @FormUrlEncoded
    fun praiseComment(
        @Field("id")
        replyId: String
    ): Observable<RedrockApiStatus>

    // 屏蔽用户
    @POST("/magipoke-loop/ignore")
    @FormUrlEncoded
    fun ignoreUid(@Field("uid") uid: String): Observable<RedrockApiStatus>

    // 取消屏蔽用户
    @DELETE("/magipoke-loop/ignore")
    fun cancelIgnoreUid(
        @Query("uid")
        uid: String
    ): Observable<RedrockApiStatus>

    // 获取全部被屏蔽的用户
    @GET("/magipoke-loop/ignore")
    fun getIgnoreUid(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Observable<RedrockApiWrapper<List<Ignore>>>

    // 获取用户收到的评论回复
    @FormUrlEncoded
    @POST("/magipoke-loop/user/comment/me")
    fun getUserReplay(
        @Field("page") page: Int,
        @Field("size") size: Int
    ): Observable<RedrockApiWrapper<List<CommentWrapper>>>

    // 获取用户收到的点赞
    @FormUrlEncoded
    @POST("/magipoke-loop/user/praise/me")
    fun getUserPraise(
        @Field("page") page: Int,
        @Field("size") size: Int
    ): Observable<RedrockApiWrapper<List<Praise>>>



    /**
     * 获取用户动态
     */
    @GET("/magipoke-loop/user/getUserPostList")
    fun getUserDynamic(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Observable<RedrockApiWrapper<List<Dynamic>>>

    /*
    获取草稿
     */
    @POST("/magipoke-draft/new/getDraft")
    fun getDraft(): Observable<RedrockApiWrapper<DynamicDraft>>

    /*
    上传或者更新草稿
     */
    @POST("/magipoke-draft/new/addDraft")
    @Multipart
    fun updateDraft(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiStatus>

    /*
    删除草稿
     */
    @POST("/magipoke-draft/new/delDraft")
    fun deleteDraft(): Observable<RedrockApiStatus>


    //获取图片压缩的上限
    @GET("/magipoke-loop/config")
    fun getImageMaxSize(): Observable<RedrockApiWrapper<ImageConfig>>
}