package com.mredrock.cyxbs.discover.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author by OkAndGreat
 * Date on 2022/5/1 10:50.
 *
 */
data class UnreadData(
    @SerializedName("has")
    val has: Boolean
) :Serializable