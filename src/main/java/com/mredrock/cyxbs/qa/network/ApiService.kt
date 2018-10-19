package com.mredrock.cyxbs.qa.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Comment
import com.mredrock.cyxbs.qa.bean.Question
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
                        size: Int = 6): Observable<RedrockApiWrapper<List<Question>>>

    @POST("/springtest/cyxbsMobile/index.php/QA/Question/getDetailedInfo")
    @FormUrlEncoded
    fun getQuestionById(@Field("question_id")
                        qid: String,
                        @Field("stuNum")
                        stuNum: String,
                        @Field("idNum")
                        idNum: String): Observable<RedrockApiWrapper<Question>>

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
}