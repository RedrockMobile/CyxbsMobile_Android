package com.mredrock.cyxbs.qa.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by yyfbe, Date on 2020/8/12.
 */
data class HotQA(@SerializedName("id")
                 val id: Int,
                 @SerializedName("created_at")
                 val createTime: String,
                 @SerializedName("update_at")
                 val updateTime: String,
                 @SerializedName("title")
                 val title: String,
                 @SerializedName("description")
                 val description: String,
                 @SerializedName("question_id")
                 val questionId: String

) : Serializable