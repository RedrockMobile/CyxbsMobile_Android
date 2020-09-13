package com.mredrock.cyxbs.volunteer.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by yyfbe, Date on 2020/9/5.
 */
class VolunteerAffairDetail(
        @SerializedName("name")
        val name: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("role")
        val role: String = "",
        @SerializedName("date")
        val date: Long = 0L,
        @SerializedName("hour")
        val hour: String = "",
        @SerializedName("last_date")
        val lastDate: Long = 0L
)