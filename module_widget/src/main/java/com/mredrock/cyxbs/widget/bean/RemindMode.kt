package com.mredrock.cyxbs.widget.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 */
data class RemindMode(
    @SerializedName("repeat_mode")
    val repeatMode: Int,
    @SerializedName("date")
    var date: List<String>,
    @SerializedName("week")
    var week: List<Int>,
    @SerializedName("day")
    var day: List<Int>,
    @SerializedName("time")
    val time: String
): Serializable {
    companion object{
        const val NONE = 0
        const val DAY = 1
        const val WEEK = 2
        const val MONTH = 3
        const val YEAR = 4
    }
}