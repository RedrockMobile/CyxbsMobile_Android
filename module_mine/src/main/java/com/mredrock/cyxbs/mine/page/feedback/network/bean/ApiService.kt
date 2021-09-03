package com.mredrock.cyxbs.mine.page.feedback.network.bean

import HistoryFeedback
import com.google.gson.JsonObject
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

/**
 * @Date : 2021/9/2   20:46
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
interface ApiService {

    @GET("/feedback-center/question/list")
    fun getNormalFeedback(@Query("product_id") productId: String): Observable<RedrockApiWrapper<NormalFeedback>>

    @GET("/feedback-center/feedback/list")
    fun getHistoryFeedback(@Query("product_id") productId: String):Observable<HistoryFeedback>

    @GET("/feedback-center/feedback/view")
    fun getDetailFeedback(@Query("product_id")productId: String,@Query("feedback_id") feedbackId:String):Observable<HistoryDetail>

    @POST("/feedback-center/feedback/create")
    fun postFeedbackInfo(@PartMap() partMap:Map<String,RequestBody>
    ,@Part file: MultipartBody.Part):Observable<RedrockApiWrapper<FeedbackResponse>>

}