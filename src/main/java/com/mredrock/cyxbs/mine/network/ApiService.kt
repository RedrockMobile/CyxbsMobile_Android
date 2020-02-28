package com.mredrock.cyxbs.mine.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.mine.network.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by zia on 2018/8/15.
 */
interface ApiService {

    /**
     * 获取个人详细资料
     */
    @FormUrlEncoded
    @POST("/app/index.php/Home/Person/search")
    fun getPersonInfo(@Field("stuNum") stuNum: String, @Field("idNum") idNum: String): Observable<RedrockApiWrapper<UserLocal>>

    /**
     * 上传头像
     */
    @Multipart
    @POST("/app/index.php/Home/Photo/uploadArticle")
    fun uploadSocialImg(@Part("stunum") stunum: RequestBody,
                        @Part file: MultipartBody.Part): Observable<RedrockApiWrapper<UploadImgResponse>>

    /**
     * 上传用户信息
     */
    @FormUrlEncoded
    @POST("/app/index.php/Home/Person/setInfo")
    fun updateUserInfo(@Field("stuNum") stuNum: String,
                       @Field("idNum") idNum: String,
                       @Field("nickname") nickname: String,
                       @Field("introduction") introduction: String,
                       @Field("qq") qq: String,
                       @Field("phone") phone: String,
                       @Field("photo_thumbnail_src") photo_thumbnail_src: String,
                       @Field("photo_src") photo_src: String): Observable<RedrockApiStatus>

    /**
     * 上传图片
     */
    @FormUrlEncoded
    @POST("/app/index.php/Home/Person/setInfo")
    fun updateUserImage(@Field("stuNum") stuNum: String,
                        @Field("idNum") idNum: String,
                        @Field("photo_thumbnail_src") photo_thumbnail_src: String,
                        @Field("photo_src") photo_src: String): Observable<RedrockApiStatus>

    /**
     * 签到部分
     */
    @POST("app/index.php/QA/Integral/checkIn")
    fun checkIn(): Observable<RedrockApiStatus>

    @POST("app/index.php/QA/User/getScoreStatus")
    fun getScoreStatus(): Observable<RedrockApiWrapper<ScoreStatus>>

    //兑换商品
    @FormUrlEncoded
    @POST("QA/Integral/order")
    fun exchangeProduct(@Field("name") name: String,
                        @Field("value") value: Int): Observable<RedrockApiStatus>

    //获取商品
    @FormUrlEncoded
    @POST("QA/Integral/getItemList")
    fun getProducts(@Field("page") page: Int,
                    @Field("size") size: Int = 6): Observable<RedrockApiWrapper<List<Product>>>

    //我的商品
    @FormUrlEncoded
    @POST("QA/Integral/myRepertory")
    fun getMyProducts(@Field("page") page: Int,
                      @Field("size") size: Int = 6): Observable<RedrockApiWrapper<List<MyProduct>>>


    /**
     * 我的首页部分
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/mine")
    fun getQANumber(@Field("stunum") stuNum: String,
                    @Field("idnum") idNum: String): Observable<RedrockApiWrapper<QANumber>>

    /**
     * 获取已发布的问题
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/question")
    fun getAskPostedList(@Field("stunum") stuNum: String,
                         @Field("idnum") idNum: String,
                         @Field("page") page: Int,
                         @Field("size") size: Int): Observable<RedrockApiWrapper<List<AskPosted>>>

    /**
     * 获取已发布的回答
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/answer")
    fun getAnswerPostedList(@Field("stunum") stuNum: String,
                            @Field("idnum") idNum: String,
                            @Field("page") page: Int,
                            @Field("size") size: Int): Observable<RedrockApiWrapper<List<AnswerPosted>>>

    /**
     * 获取用户发出的评论
     *
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/comment")
    fun getCommentList(@Field("stunum") stuNum: String,
                       @Field("idnum") idNum: String,
                       @Field("page") page: Int,
                       @Field("size") size: Int): Observable<RedrockApiWrapper<List<Comment>>>

    /**
     * 获取别人对用户的评论
     * 注：即用户的回答，别人在下面评论
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/reComment")
    fun getCommentReceivedList(@Field("stunum") stuNum: String,
                               @Field("idnum") idNum: String,
                               @Field("page") page: Int,
                               @Field("size") size: Int): Observable<RedrockApiWrapper<List<CommentReceived>>>

//    /**
//     * 草稿箱部分
//     */
//    @FormUrlEncoded
//    @POST("app/index.php/QA/User/deleteItemInDraft")
//    fun deleteDraft(@Field("stunum") stuNum: String,
//                    @Field("idnum") idNum: String,
//                    @Field("id") id: String): Observable<RedrockApiStatus>
//
//    @FormUrlEncoded
//    @POST("app/index.php/QA/Answer/remark")
//    fun commentAnswer(@Field("stuNum") stuNum: String,
//                      @Field("idNum") idNum: String,
//                      @Field("answer_id") aid: String,
//                      @Field("description") content: String): Observable<RedrockApiStatus>
//
//    @FormUrlEncoded
//    @POST("app/index.php/QA/User/updateItemInDraft")
//    fun refreshDraft(@Field("stunum") stuNum: String,
//                     @Field("idnum") idNum: String,
//                     @Field("description") content: String,
//                     @Field("id") draftId: String): Observable<RedrockApiStatus>

    /**
     * 提问草稿
     */
    @FormUrlEncoded
    @POST("wxapi/magipoke-draft/User/getDraftQuestionList")
    fun getAskDraftList(@Field("stuNum") stuNum: String,
                        @Field("idNum") idNum: String,
                        @Field("page") page: Int,
                        @Field("size") size: Int): Observable<RedrockApiWrapper<List<AskDraft>>>

    /**
     * 回答草稿
     */
    @FormUrlEncoded
    @POST("wxapi/magipoke-draft/User/getDraftAnswerList")
    fun getAnswerDraftList(@Field("stuNum") stuNum: String,
                           @Field("idNum") idNum: String,
                           @Field("page") page: Int,
                           @Field("size") size: Int): Observable<RedrockApiWrapper<List<AnswerDraft>>>

    /**
     * 根据question的id获取Question，注意，网络请求结果为RequestBody对象，未解析
     * 当跳转qa时，需要传递requestBody的string
     * 因为qa和mine互不通信，故采取此方法传递Question对象
     */
    @FormUrlEncoded
    @POST("/app/index.php/QA/Question/getQuestionInfo")
    fun getQuestion(
            @Field("stunum") stuNum: String,
            @Field("idnum") idNum: String,
            @Field("question_id") qid: String): Observable<ResponseBody>


}
