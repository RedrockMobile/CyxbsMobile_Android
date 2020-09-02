package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@author zhangzhe
 *@date 2020/8/8
 *@description 用于缓存地点信息
 */


data class PlaceDetailsStoreBean(
        @SerializedName("place_details")
        var placeDetails: PlaceDetails,
        var id: String
) : Serializable