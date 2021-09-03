package com.mredrock.cyxbs.mine.page.feedback.network.bean
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * @Date : 2021/9/2   20:40
 * @By ysh 
 * @Usage :
 * @Request : God bless my code
**/
data class NormalFeedback(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("info")
    val info: String
):Serializable

data class Data(
    @SerializedName("content")
    val content: String,
    @SerializedName("CreatedAt")
    val createdAt: String,
    @SerializedName("DeletedAt")
    val deletedAt: Any,
    @SerializedName("editor")
    val editor: String,
    @SerializedName("ID")
    val iD: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("UpdatedAt")
    val updatedAt: String
):Serializable
