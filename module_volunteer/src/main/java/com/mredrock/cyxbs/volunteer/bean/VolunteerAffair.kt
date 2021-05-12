package com.mredrock.cyxbs.volunteer.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by yyfbe, Date on 2020/9/5.
 */


class VolunteerAffair(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("name")
        val name: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("place")
        val place: String = "",
        @SerializedName("date")
        val date: Long = 0L,
        @SerializedName("last_date")
        val lastDate: Long = 0L,
        @SerializedName("hour")
        val hour: String = ""
) : Serializable