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
    @POST("magipoke/person/upload/avatar")
    fun uploadSocialImg(@Part("stunum") stunum: RequestBody,
                        @Part file: MultipartBody.Part): Observable<RedrockApiWrapper<UploadImgResponse>>

    /**
     * 上传用户信息
     */
    @FormUrlEncoded
    @POST("/magipoke/Person/SetInfo")
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
    @POST("/magipoke/Person/SetInfo")
    fun updateUserImage(@Field("photo_thumbnail_src") photo_thumbnail_src: String,
                        @Field("photo_src") photo_src: String): Observable<RedrockApiStatus>

    /**
     * 签到部分
     */
    @POST("/magipoke-intergral/QA/Integral/checkIn")
    fun checkIn(): Observable<RedrockApiStatus>

    //获取积分
    @POST("/magipoke-intergral/QA/User/getScoreStatus")
    fun getScoreStatus(): Observable<RedrockApiWrapper<ScoreStatus>>

    //兑换商品
    @FormUrlEncoded
    @POST("/magipoke-intergral/QA/Integral/order")
    fun exchangeProduct(@Field("name") name: String,
                        @Field("value") value: Int): Observable<RedrockApiStatus>

    //获取商品
    @FormUrlEncoded
    @POST("/magipoke-intergral/QA/Integral/getItemList")
    fun getProducts(@Field("page") page: Int,
                    @Field("size") size: Int = 6): Observable<RedrockApiWrapper<List<Product>>>

    //我的商品
    @FormUrlEncoded
    @POST("/magipoke-intergral/QA/Integral/myRepertory")
    fun getMyProducts(@Field("page") page: Int,
                      @Field("size") size: Int = 6): Observable<RedrockApiWrapper<List<MyProduct>>>


    /**
     * 我的首页部分
     */
    @POST("app/index.php/QA/User/mine")
    fun getQANumber(): Observable<RedrockApiWrapper<QANumber>>


    /**
     * 说明：修改密码模块的接口存在两种情况
     * 即来自登陆界面以及来自个人界面
     * 接口方法中以FormLogin结尾的就是自登陆界面时需要调用的接口
     * 反之就是自个人界面调用的接口
     */

    /**
     * 修改密码
     * 调用此方法必须处于登录状态
     * 故不需要传递认证int进来
     */
    @FormUrlEncoded
    @POST("/user-secret/user/password/personal")
    fun resetPassword(
            @Field("origin_password") origin_password: String,
            @Field("new_password") new_password: String): Observable<RedrockApiStatus>

    /**
     * 修改密码
     * 调用此方法时处于登陆界面
     */
    @FormUrlEncoded
    @POST("/user-secret/user/password/valid")
    fun resetPasswordFromLogin(
            @Field("stu_num") stu_num: String,
            @Field("new_password") new_password: String,
            @Field("code") code: Int
    ): Observable<RedrockApiStatus>

    /**
     * 设置密保问题答案
     * 必须在个人界面中调用此方法
     */
    @FormUrlEncoded
    @POST("/user-secret/user/bind/question")
    fun setSecurityQuestionAnswer(
            @Field("id") id: Int, //问题的id
            @Field("content") content: String): Observable<RedrockApiStatus>//答案的主体内容

    /**
     * 获取所有的密保问题
     */
    @GET("/user-secret/user/question")
    fun getAllSecurityQuestions(): Observable<RedrockApiWrapper<List<SecurityQuestion>>>

    /**
     * 获取Email验证码
     * 此接口用于绑定邮箱时向此邮箱发送验证码
     */
    @FormUrlEncoded
    @POST("/user-secret/user/bind/email/code")
    fun getEmailCode(
            @Field("email") email: String): Observable<RedrockApiWrapper<EmailCode>>

    /**
     * 验证Email验证码
     * 此接口用于绑定邮箱时验证验证码是否正确
     */
    @FormUrlEncoded
    @POST("/user-secret/user/bind/email")
    fun confirmEmailCode(
            @Field("email") email: String, //问题的id
            @Field("code") code: String): Observable<RedrockApiStatus>

    /**
     * 向绑定的邮箱发送找回密码用的验证码
     * （自登陆界面，需要传递学号进来）
     */
    @FormUrlEncoded
    @POST("/user-secret/user/valid/email/code")
    fun getEmailFindPasswordCode(
            @Field("stu_num") stu_num: String
    ): Observable<RedrockApiWrapper<ConfirmCode>>

    /**
     * 验证邮箱验证码是否正确
     * 来自登陆界面
     * 此处的返回值中不一定含有data字段
     * 需要根据返回值的status判断是否拥有data字段
     * 邮箱需要通过其他接口获取
     */
    @FormUrlEncoded
    @POST("/user-secret/user/valid/email")
    fun confirmCodeWithoutLogin(
            @Field("stu_num") stu_num: String,
            @Field("email") email: String,
            @Field("code") code: Int
    ): Observable<RedrockApiWrapper<ConfirmQuestion>>

    /**
     * 获取用户邮箱地址
     * 没有进行用户数据的解析
     * 根据不同的status，不一定会有data字段
     */
    @FormUrlEncoded
    @POST("/user-secret/user/bind/email/detail")
    fun getUserEmail(@Field("stu_num") stu_num: String
    ): Observable<RedrockApiWrapper<Email>>

    /**
     * 获取学生的密保问题
     */
    @FormUrlEncoded
    @POST("/user-secret/user/bind/question/detail")
    fun getUserQuestion(@Field("stu_num") stu_num: String
    ): Observable<RedrockApiWrapper<List<SecurityQuestion>>>

    /**
     * 验证密保问题的回答是否正确
     */
    @FormUrlEncoded
    @POST("/user-secret/user/valid/question")
    fun confirmAnswer(
            @Field("stu_num") stu_num: String,
            @Field("question_id") question_id: Int,
            @Field("content") content: String
    ): Observable<RedrockApiWrapper<ConfirmQuestion>>

    /*
     * 判断旧密码是否正确
     */
    @FormUrlEncoded
    @POST("/user-secret/user/judge/password")
    fun originPassWordCheck(
            @Field("password") password: String): Observable<RedrockApiStatus>

    /**
     * 检查是否绑定信息
     */
    @FormUrlEncoded
    @POST("/user-secret/user/bind/is")
    fun checkBinding(@Field("stu_num") stu_num: String): Observable<RedrockApiWrapper<BindingResponse>>

    /**
     * 检查是否为默认密码
     */
    @FormUrlEncoded
    @POST("/user-secret/user/judge/origin")
    fun checkDefaultPassword(@Field("stu_num") stu_num: String): Observable<RedrockApiStatus>

    /**
     * 获取用户的动态、评论、获赞数
     */
    @GET("/magipoke-loop/user/getUserCount")
    fun getUserCount():Observable<RedrockApiWrapper<UserCount>>

    /**
     * 获取未查看的评论和点赞数
     * Tip: 此处是静态请求，不能使用@Field注解
     * type = 1:评论
     * type = 2:点赞
     */
    @GET("/magipoke-loop/user/uncheckedMessage")
    fun getUncheckCount(
            @Query("time") timeStamp: Long,
            @Query("type") type: Int
    ):Observable<RedrockApiWrapper<UserUncheckCount>>

    //仿ping接口，用于检测magipoke系列接口状态
    @GET("magipoke/ping")
    fun pingMagipoke(): Observable<RedrockApiStatus>
}
