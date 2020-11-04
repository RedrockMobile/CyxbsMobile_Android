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
    @POST("wxapi/magipoke/Person/SetInfo")
    fun updateUserInfo(@Field("nickname") nickname: String,
                       @Field("introduction") introduction: String,
                       @Field("qq") qq: String,
                       @Field("phone") phone: String,
                       @Field("photo_thumbnail_src") photo_thumbnail_src: String,
                       @Field("photo_src") photo_src: String): Observable<RedrockApiStatus>

    /**
     * 上传图片
     */
    @FormUrlEncoded
    @POST("wxapi/magipoke/Person/SetInfo")
    fun updateUserImage(@Field("photo_thumbnail_src") photo_thumbnail_src: String,
                        @Field("photo_src") photo_src: String): Observable<RedrockApiStatus>

    /**
     * 签到部分
     */
    @POST("app/index.php/QA/Integral/checkIn")
    fun checkIn(): Observable<RedrockApiStatus>

    //获取积分
    @POST("app/index.php/QA/User/getScoreStatus")
    fun getScoreStatus(): Observable<RedrockApiWrapper<ScoreStatus>>

    //兑换商品
    @FormUrlEncoded
    @POST("wxapi/magipoke-intergral/QA/Integral/order")
    fun exchangeProduct(@Field("name") name: String,
                        @Field("value") value: Int): Observable<RedrockApiStatus>

    //获取商品
    @FormUrlEncoded
    @POST("wxapi/magipoke-intergral/QA/Integral/getItemList")
    fun getProducts(@Field("page") page: Int,
                    @Field("size") size: Int = 6): Observable<RedrockApiWrapper<List<Product>>>

    //我的商品
    @FormUrlEncoded
    @POST("wxapi/magipoke-intergral/QA/Integral/myRepertory")
    fun getMyProducts(@Field("page") page: Int,
                      @Field("size") size: Int = 6): Observable<RedrockApiWrapper<List<MyProduct>>>


    /**
     * 我的首页部分
     */
    @POST("app/index.php/QA/User/mine")
    fun getQANumber(): Observable<RedrockApiWrapper<QANumber>>

    /**
     * 获取已发布的问题
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/question")
    fun getAskPostedList(@Field("page") page: Int,
                         @Field("size") size: Int): Observable<RedrockApiWrapper<List<AskPosted>>>

    /**
     * 获取已发布的回答
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/answer")
    fun getAnswerPostedList(@Field("page") page: Int,
                            @Field("size") size: Int): Observable<RedrockApiWrapper<List<AnswerPosted>>>

    /**
     * 获取用户发出的评论
     *
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/comment")
    fun getCommentList(@Field("page") page: Int,
                       @Field("size") size: Int): Observable<RedrockApiWrapper<List<Comment>>>

    /**
     * 获取别人对用户的评论
     * 注：即用户的回答，别人在下面评论
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/reComment")
    fun getCommentReceivedList(@Field("page") page: Int,
                               @Field("size") size: Int): Observable<RedrockApiWrapper<List<CommentReceived>>>


    /**
     * 提问草稿
     */
    @FormUrlEncoded
    @POST("wxapi/magipoke-draft/User/getDraftQuestionList")
    fun getAskDraftList(@Field("page") page: Int,
                        @Field("size") size: Int): Observable<RedrockApiWrapper<List<AskDraft>>>

    /**
     * 回答草稿
     */
    @FormUrlEncoded
    @POST("wxapi/magipoke-draft/User/getDraftAnswerList")
    fun getAnswerDraftList(@Field("page") page: Int,
                           @Field("size") size: Int): Observable<RedrockApiWrapper<List<AnswerDraft>>>


    /**
     * 删除草稿
     */
    @FormUrlEncoded
    @POST("wxapi/magipoke-draft/User/deleteItemInDraft")
    fun deleteDraftById(@Field("id") id: Int): Observable<RedrockApiStatus>


    /**
     * 根据question的id获取Question，注意，网络请求结果为RequestBody对象，未解析
     * 当跳转qa时，需要传递requestBody的string
     * 因为qa和mine互不通信，故采取此方法传递Question对象
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/Question/getDetailedInfo")
    fun getQuestion(
            @Field("question_id") qid: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("app/index.php/QA/Answer/getAnswerInfo")
    fun getAnswer(
            @Field("answer_id") answer_id: String): Observable<ResponseBody>

    /**
     * 修改密码
     * 调用此方法必须处于登录状态
     */
    @FormUrlEncoded
    @POST("/user/password/personal")
    fun changePassword(
            @Field("origin_password") origin_password : String ,
            @Field("new_password") new_password: String): Observable<RedrockApiStatus>

    /**
     * 设置密保问题答案
     */
    @FormUrlEncoded
    @POST("/user/bind/question")
    fun setSecurityQuestionAnswer(
            @Field("id") id: Int , //问题的id
            @Field("content") content : String): Observable<RedrockApiStatus>//答案的主体内容

    /**
     * 获取所有的密保问题
     */
    @FormUrlEncoded
    @GET("/user/question")
    fun getAllSecurityQuestions(): Observable<RedrockApiWrapper<List<SecurityQuestion>>>

    /**
     * 获取Email验证码
     */
    @FormUrlEncoded
    @POST("/user/bind/email/code")
    fun getEmailCode(
            @Field("email") email: String): Observable<RedrockApiWrapper<EmailCode>>

    /**
     * 验证Email验证码
     */
    @FormUrlEncoded
    @POST("/user/bind/email/code")
    fun confirmEmailCode(
            @Field("email") email: String, //问题的id
            @Field("code") code : String): Observable<RedrockApiStatus>
}
