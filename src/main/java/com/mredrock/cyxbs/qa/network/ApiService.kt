package com.mredrock.cyxbs.qa.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Comment
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.bean.QuizResult
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created By jay68 on 2018/8/26.
 */
interface ApiService {
    @POST("/springtest/cyxbsMobile/index.php/QA/Question/getQuestionList")
    @FormUrlEncoded
    fun getQuestionList(@Field("kind")
                        kind: String,
                        @Field("page")
                        page: Int,
                        @Field("size")
                        size: Int = 6,
                        @Field("stunum")
                        stuNum: String,
                        @Field("idnum")
                        idNum: String): Observable<RedrockApiWrapper<List<Question>>>

    @POST("/springtest/cyxbsMobile/index.php/QA/Answer/getAnswerlist")
    @FormUrlEncoded
    fun getAnswerList(@Field("question_id")
                      qid: String,
                      @Field("page")
                      page: Int,
                      @Field("size")
                      size: Int = 6,
                      @Field("stuNum")
                      stuNum: String,
                      @Field("idNum")
                      idNum: String): Observable<RedrockApiWrapper<List<Answer>>>

    @POST("/springtest/cyxbsMobile/index.php/QA/Answer/getRemarkList")
    @FormUrlEncoded
    fun getCommentList(@Field("answer_id")
                       aid: String,
                       @Field("page")
                       page: Int,
                       @Field("size")
                       size: Int = 6,
                       @Field("stuNum")
                       stuNum: String,
                       @Field("idNum")
                       idNum: String): Observable<RedrockApiWrapper<List<Comment>>>

    @POST("/springtest/cyxbsMobile/index.php/QA/Answer/praise")
    @FormUrlEncoded
    fun praiseAnswer(@Field("answer_id")
                     aid: String,
                     @Field("stuNum")
                     stuNum: String,
                     @Field("idNum")
                     idNum: String): Observable<RedrockApiStatus>

    @POST("/springtest/cyxbsMobile/index.php/QA/Answer/cancelPraise")
    @FormUrlEncoded
    fun cancelPraiseAnswer(@Field("answer_id")
                           aid: String,
                           @Field("stuNum")
                           stuNum: String,
                           @Field("idNum")
                           idNum: String): Observable<RedrockApiStatus>

    @POST("/springtest/cyxbsMobile/index.php/QA/Answer/adopt")
    @FormUrlEncoded
    fun adoptAnswer(@Field("answer_id")
                    aid: String,
                    @Field("question_id")
                    qid: String,
                    @Field("stuNum")
                    stuNum: String,
                    @Field("idNum")
                    idNum: String): Observable<RedrockApiStatus>

    @POST("/springtest/cyxbsMobile/index.php/QA/Answer/remark")
    @FormUrlEncoded
    fun sendComment(@Field("answer_id")
                    aid: String,
                    @Field("content")
                    content: String,
                    @Field("stuNum")
                    stuNum: String,
                    @Field("idNum")
                    idNum: String): Observable<RedrockApiStatus>

    @POST("springtest/cyxbsMobile/index.php/QA/Integral/getDiscountBalance")
    @FormUrlEncoded
    fun getMyRewardCount(@Field("stuNum")
                         stuNum: String,
                         @Field("idNum")
                         idNum: String): Observable<RedrockApiWrapper<Int>>

    @POST("springtest/cyxbsMobile/index.php/QA/Question/add")
    @FormUrlEncoded
    fun quiz(@Field("stuNum")
             stuNum: String,
             @Field("idNum")
             idNum: String,
             @Field("title")
             title: String,
             @Field("description")
             content: String,
             @Field("is_anonymous")
             isAnonymous: Int,
             @Field("kind")
             kind: String,
             @Field("tags")
             tags: String,
             @Field("reward")
             reward: Int,
             @Field("disappear_time")
             disappear: String): Observable<RedrockApiWrapper<QuizResult>>

    @POST("springtest/cyxbsMobile/index.php/QA/Question/uploadPicture")
    @Multipart
    fun uploadQuestionPic(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiStatus>
}