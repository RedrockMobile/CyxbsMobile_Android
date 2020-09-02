package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */

data class FavoritePlaceSimple(
        @SerializedName("place_id")
        val placeIdList: MutableList<String>
) : Serializable