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
        var percent: String,
        @SerializedName("can_check_in")
        var canCheckIn: Boolean

) : Serializable {
    /**
         "integral": 1410,    积分
        "check_in_days": 10,   连续签到天数
        "is_check_today": 1,   是否今天已签到，1为是，0为否
        "week_info": "0011111",
                                本周签到信息，从左到右依次为周日，周六....周一。1为签到，0为未签到
        "rank": 8,              改天第几个签到
        "percent": "100%"       连续签到天数超过了100%的人，   分母为连续签到天数大于1的人
     */
    val isChecked get() = checkSign == 1
}

