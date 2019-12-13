package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created By jay68 on 2018/06/03.
 */
data class ScoreStatus(
        @SerializedName("integral")
        var integral: Int,
        @SerializedName("check_in_days")
        var serialDays: Int,
        @SerializedName("is_check_today")
        var checkSign: Int,
        @SerializedName("week_info")
        var weekInfo: String,
        @SerializedName("rank")
        var rank: Int,
        @SerializedName("percent")
        var percent: String
) : Serializable {
    val isChecked get() = checkSign == 1
}