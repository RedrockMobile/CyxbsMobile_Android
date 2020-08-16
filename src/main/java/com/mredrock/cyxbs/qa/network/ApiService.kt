package com.mredrock.cyxbs.qa.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.qa.bean.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created By jay68 on 2018/8/26.
 */
interface ApiService {
    @POST("/app/index.php/QA/Question/getQuestionList")
    @FormUrlEncoded
    fun getQuestionList(@Field("kind")
                        kind: String,
                        @Field("page")
                        page: Int,
                        @Field("size")
                        size: Int = 6): Observable<RedrockApiWrapper<List<Question>>>

    @POST("/app/index.php/QA/Answer/getAnswerlist")
    @FormUrlEncoded
    fun getAnswerList(@Field("question_id")
                      qid: String,
                      @Field("page")
                      page: Int,
                      @Field("size")
                      size: Int = 6): Observable<RedrockApiWrapper<List<Answer>>>

    //后端没size这个参数（无语），每次都是6个
    @POST("/app/index.php/QA/Answer/getRemarkList")
    @FormUrlEncoded
    fun getCommentList(@Field("answer_id")
                       aid: String,
                       @Field("page")
                       page: Int): Observable<RedrockApiWrapper<List<Comment>>>

    @POST("/app/index.php/QA/Answer/praise")
    @FormUrlEncoded
    fun praiseAnswer(@Field("answer_id")
                     aid: String): Observable<RedrockApiStatus>

    @POST("/app/index.php/QA/Answer/cancelPraise")
    @FormUrlEncoded
    fun cancelPraiseAnswer(@Field("answer_id")
                           aid: String): Observable<RedrockApiStatus>

    @POST("/app/index.php/QA/Answer/adopt")
    @FormUrlEncoded
    fun adoptAnswer(@Field("answer_id")
                    aid: String,
                    @Field("question_id")
                    qid: String): Observable<RedrockApiStatus>

    @POST("/app/index.php/QA/Answer/remark")
    @FormUrlEncoded
    fun sendComment(@Field("answer_id")
                    aid: String,
                    @Field("content")
                    content: String): Observable<RedrockApiStatus>

    @POST("app/index.php/QA/User/getScoreStatus")
    fun getScoreStatus(): Observable<RedrockApiWrapper<ScoreStatus>>

    @POST("app/index.php/QA/Question/add")
    @FormUrlEncoded
    fun quiz(@Field("title")
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

    @POST("app/index.php/QA/Question/uploadPicture")
    @Multipart
    fun uploadQuestionPic(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiStatus>


    @POST("app/index.php/QA/Answer/add")
    @FormUrlEncoded
    fun answer(@Field("question_id")
               qid: String,
               @Field("content")
               content: String): Observable<RedrockApiWrapper<String>>

    @POST("app/index.php/QA/Answer/uploadPicture")
    @Multipart
    fun uploadAnswerPic(@Part parts: List<MultipartBody.Part>): Observable<RedrockApiStatus>

    @POST("app/index.php/QA/Feedback/addReport")
    @FormUrlEncoded
    fun reportQuestion(@Field("question_id") qid: String,
                       @Field("type") type: String): Observable<RedrockApiStatus>

    @POST("app/index.php/QA/Feedback/addReport")
    @FormUrlEncoded
    fun reportAnswer(@Field("answer_id") aid: String,
                     @Field("type") type: String): Observable<RedrockApiStatus>

    @POST("app/index.php/QA/Feedback/addReport")
    @FormUrlEncoded
    fun reportComment(@Field("answer_id") cid: String,
                      @Field("type") type: String): Observable<RedrockApiStatus>

    @POST("app/index.php/QA/Question/ignore")
    @FormUrlEncoded
    fun ignoreQuestion(@Field("question_id")
                       qid: String): Observable<RedrockApiStatus>

    /**
     * 草稿箱
     */
    @POST("wxapi/magipoke-draft/User/addItemInDraft")
    @FormUrlEncoded
    fun addItemToDraft(@Field("type") type: String,
                       @Field("content") content: String,
                       @Field("target_id") id: String): Observable<RedrockApiStatus>

    @POST("wxapi/magipoke-draft/User/updateItemInDraft")
    @FormUrlEncoded
    fun updateDraft(@Field("content") content: String,
                    @Field("id") id: String): Observable<RedrockApiStatus>

    @POST("wxapi/magipoke-draft/User/deleteItemInDraft")
    @FormUrlEncoded
    fun deleteDraft(@Field("id") id: String): Observable<RedrockApiStatus>

    @POST("app/index.php/QA/Question/addView")
    @FormUrlEncoded
    fun addView(@Field("id") qid: String): Observable<RedrockApiStatus>

    @FormUrlEncoded
    @POST("app/index.php/QA/Question/getDetailedInfo")
    fun getQuestion(@Field("question_id") qid: String): Observable<RedrockApiWrapper<Question>>

    @FormUrlEncoded
    @POST("app/index.php/QA/Question/getHotQuestion")
    fun getHotQuestion(@Field("page")
                       page: Int,
                       @Field("size")
                       size: Int = 6): Observable<RedrockApiWrapper<List<HotQA>>>

    @FormUrlEncoded
    @POST("/app/index.php/QA/Search/getKnowledgeBase")
    fun getKnowledge(@Field("search_key")
                     searchKey: String): Observable<RedrockApiWrapper<Knowledge>>


    @FormUrlEncoded
    @POST("/app/index.php/QA/Search/getQuestionList")
    fun getSearchedQuestionList(@Field("search_key")
                                searchKey: String,
                                @Field("page")
                                page: Int,
                                @Field("size")
                                size: Int = 6): Observable<RedrockApiWrapper<List<Question>>>
}
