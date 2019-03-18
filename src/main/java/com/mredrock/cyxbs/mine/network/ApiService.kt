package com.mredrock.cyxbs.mine.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.mine.network.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    fun getPersonInfo(@Field("stuNum") stuNum: String, @Field("idNum") idNum: String): Observable<RedrockApiWrapper<User>>

    /**
     * 上传头像
     */
    @Multipart
    @POST("/app/index.php/Home/Photo/uploadArticle")
    fun uploadSocialImg(@Part("stunum") stunum: RequestBody,
                        @Part file: MultipartBody.Part): Observable<RedrockApiWrapper<UploadImgResponse>>

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

    @FormUrlEncoded
    @POST("/app/index.php/Home/Person/setInfo")
    fun updateUserImage(@Field("stuNum") stuNum: String,
                        @Field("idNum") idNum: String,
                        @Field("photo_thumbnail_src") photo_thumbnail_src: String,
                        @Field("photo_src") photo_src: String): Observable<RedrockApiStatus>

    /**
     * 签到部分
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/Integral/checkIn")
    fun checkIn(@Field("stunum") stuNum: String,
                @Field("idnum") idNum: String): Observable<RedrockApiStatus>

    @FormUrlEncoded
    @POST("app/index.php/QA/Integral/getCheckInStatus")
    fun getCheckInStatus(@Field("stunum") stuNum: String,
                         @Field("idnum") idNum: String): Observable<RedrockApiWrapper<CheckInStatus>>

    @FormUrlEncoded
    @POST("app/index.php/QA/User/integralRecords")
    fun getIntegralRecords(@Field("stuNum") stuNum: String,
                           @Field("idNum") idNum: String,
                           @Field("page") page: Int,
                           @Field("size") size: Int): Observable<RedrockApiWrapper<List<PointDetail>>>

    /**
     * 草稿箱部分
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/deleteItemInDraft")
    fun deleteDraft(@Field("stunum") stuNum: String,
                    @Field("idnum") idNum: String,
                    @Field("id") id: String): Observable<RedrockApiStatus>

    @FormUrlEncoded
    @POST("app/index.php/QA/Answer/remark")
    fun commentAnswer(@Field("stuNum") stuNum: String,
                      @Field("idNum") idNum: String,
                      @Field("answer_id") aid: String,
                      @Field("content") content: String): Observable<RedrockApiStatus>

    @FormUrlEncoded
    @POST("app/index.php/QA/User/updateItemInDraft")
    fun refreshDraft(@Field("stunum") stuNum: String,
                     @Field("idnum") idNum: String,
                     @Field("content") content: String,
                     @Field("id") draftId: String): Observable<RedrockApiStatus>

    @FormUrlEncoded
    @POST("app/index.php/QA/User/getDraftList")
    fun getDraftList(@Field("stunum") stuNum: String,
                     @Field("idnum") idNum: String,
                     @Field("page") page: Int,
                     @Field("size") size: Int): Observable<RedrockApiWrapper<List<Draft>>>

    /**
     * 问一问
     */
    @FormUrlEncoded
    @POST("/app/index.php/QA/User/ask")
    fun getMyAskOver(@Field("stunum") stuNum: String,
                     @Field("idnum") idNum: String,
                     @Field("page") page: Int,
                     @Field("size") size: Int,
                     @Field("type") type: Int = 1): Observable<RedrockApiWrapper<List<MyQuestion>>>

    @FormUrlEncoded
    @POST("/app/index.php/QA/User/ask")
    fun getMyAskWait(@Field("stunum") stuNum: String,
                     @Field("idnum") idNum: String,
                     @Field("page") page: Int,
                     @Field("size") size: Int,
                     @Field("type") type: Int = 2): Observable<RedrockApiWrapper<List<MyQuestion>>>

    /**
     * 帮一帮
     */
    @FormUrlEncoded
    @POST("/app/index.php/QA/User/help")
    fun getMyHelpOver(@Field("stunum") stuNum: String,
                      @Field("idnum") idNum: String,
                      @Field("page") page: Int,
                      @Field("size") size: Int,
                      @Field("type") type: Int = 1): Observable<RedrockApiWrapper<List<MyQuestion>>>

    @FormUrlEncoded
    @POST("/app/index.php/QA/User/help")
    fun getMyHelpWait(@Field("stunum") stuNum: String,
                      @Field("idnum") idNum: String,
                      @Field("page") page: Int,
                      @Field("size") size: Int,
                      @Field("type") type: Int = 2): Observable<RedrockApiWrapper<List<MyQuestion>>>

    /**
     * 与我相关
     */
    @FormUrlEncoded
    @POST("app/index.php/QA/User/aboutMe")
    fun getRelateMeList(@Field("stunum") stuNum: String,
                        @Field("idnum") idNum: String,
                        @Field("page") page: Int,
                        @Field("size") size: Int,
                        @Field("type") type: Int): Observable<RedrockApiWrapper<List<RelateMeItem>>>
}
