package com.mredrock.cyxbs.ufield.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * author : QTwawa
 * date : 2024/8/25 16:36
 */
data class RemindMode(
    @SerializedName("repeat_mode")
    var repeatMode: Int,
    @SerializedName("date")
    var date: ArrayList<String>,
    @SerializedName("week")
    var week: ArrayList<Int>,
    @SerializedName("day")
    var day: ArrayList<Int>,
    @SerializedName("notify_datetime")
    var notifyDateTime: String?
) : Serializable {
    companion object {
        const val NONE = 0
        const val DAY = 1
        const val WEEK = 2
        const val MONTH = 3
        const val YEAR = 4

        fun generateDefaultRemindMode(): RemindMode {
            return RemindMode(
                NONE,
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                ""
            )
        }
    }
}
