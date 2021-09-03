package com.mredrock.cyxbs.mine.page.feedback.network.bean

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @Date : 2021/9/2   20:46
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
interface ApiService {

    @GET("/feedback-center/question/list")
    fun getNormalFeedback(@Query("product_id") productId: String): Observable<NormalFeedback>

    @GET("/feedback-center/feedback/list")
    fun getHistoryFeedback(@Query("product_id") productId: String): Observable<HistoryFeedback>

    @GET("/feedback-center/feedback/view")
    fun getDetailFeedback(@Query("product_id") productId: String, feedbackId: String)

    @Multipart
    @POST("/feedback-center/feedback/create")
    fun postFeedbackInfo(
        @PartMap() partMap: Map<String, @JvmSuppressWildcards RequestBody>
        ,@Part file: MultipartBody.Part): Observable<FeedbackResponse>
}