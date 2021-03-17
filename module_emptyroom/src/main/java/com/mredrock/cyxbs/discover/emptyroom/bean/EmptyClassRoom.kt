package com.mredrock.cyxbs.discover.emptyroom.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-03-17 15:21
 */
class EmptyClassRoom(
        @SerializedName("Build")
        val build: String,
        @SerializedName("Rooms")
        val rooms: List<String>
): Serializable