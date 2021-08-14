package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by yyfbe, Date on 2020/8/22.
 */
data class PlaceSearch(
        @SerializedName("place_id")
        var placeId: String
) : Serializable