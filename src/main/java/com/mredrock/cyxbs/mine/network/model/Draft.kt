package com.mredrock.cyxbs.mine.network.model

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.common.utils.LogUtils
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by jay86.
 */
data class Draft(@SerializedName("created_at")
                 val createdAt: String = "",
                 @SerializedName("target_id")
                 val targetId: String = "",
                 @SerializedName("id")
                 val id: String = "",
                 @SerializedName("type")
                 val type: String = "",
                 @SerializedName("title_content")
                 val titleContent: String = "",
                 @SerializedName("content")
                 val content: String = "",
                 var question: Question?) {

    companion object {
        const val TYPE_QUESTION = "question"
        const val TYPE_ANSWER = "answer"
        const val TYPE_COMMENT = "remark"

        @JvmStatic
        val CONTENT_TYPE = mapOf(TYPE_QUESTION to "提问：", TYPE_ANSWER to "帮助：", TYPE_COMMENT to "评论：")
        @JvmStatic
        val TITLE_TYPE = mapOf(TYPE_QUESTION to "", TYPE_ANSWER to "提问：", TYPE_COMMENT to "帮助：")
    }

    val isQuestion = type == "question"
    val titleDisplay get() = TITLE_TYPE[type] + titleContent
    val contentDisplay
        get() = CONTENT_TYPE[type] + if (isQuestion) question?.title
                ?: "null" else question?.title
    val timeDisplay: String
        get() {
            val today = Date()
            val time = createdAt.split("\\s")
            return if (SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today) == time[0]) {
                time[1]
            } else {
                time[0]
            }
        }

    fun parseQuestion() {
        try {
            question = Gson().fromJson(String(Base64.decode(content, Base64.DEFAULT)), Question::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
//            if (BuildConfig.DEBUG) {
//                throw e
//            }
        }
    }
}