package com.mredrock.cyxbs.qa.bean

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
        private var checkSign: Int) : Serializable {
    val isChecked get() = checkSign == 1
}