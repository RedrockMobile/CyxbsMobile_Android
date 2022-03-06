package com.mredrock.cyxbs.widget.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-08-19
 * @author Sca RayleighZ
 */
data class DateBeen(
    @SerializedName("month")
    val month: Int,
    @SerializedName("day")
    val day: Int,
    @SerializedName("week")
    val week: Int
): Serializable
