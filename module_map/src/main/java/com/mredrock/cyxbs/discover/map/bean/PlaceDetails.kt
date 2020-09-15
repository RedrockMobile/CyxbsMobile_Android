package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@author zhangzhe
 *@date 2020/8/8
 *@description 地点详细信息bean
 */

/**
 * "place_name":"中心食堂"
 * "place_attribute":"食堂"
 * "is_collected":"true/false"
 * "tags":
 * [
 * "新生报到",
 * "开馆时间：10:00-22:00",
 * ...
 * ]
 * "images":
 * [
 * "url",
 * "url",
 * ...
 * ]
 */

data class PlaceDetails(
        @SerializedName("place_name")
        var placeName: String,
        @SerializedName("place_attribute")
        var placeAttribute: List<String>?,
        var tags: List<String>?,
        var images: List<String>?
) : Serializable