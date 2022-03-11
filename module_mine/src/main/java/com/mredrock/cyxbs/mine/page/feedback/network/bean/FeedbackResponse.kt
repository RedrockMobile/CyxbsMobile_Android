package com.mredrock.cyxbs.mine.page.feedback.network.bean
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * @Date : 2021/9/2   20:45
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
data class FeedbackResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("info")
    val info: String,
    @SerializedName("message")
    val message: String
):Serializable