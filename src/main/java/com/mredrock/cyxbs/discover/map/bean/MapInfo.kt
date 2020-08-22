package com.mredrock.cyxbs.discover.map.bean

import android.text.style.BackgroundColorSpan
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@author zhangzhe
 *@date 2020/8/8
 *@description 主要接口，用来获取地图的底图，基本信息（包括各个地点的坐标等），完全对应接口文档
 */

data class MapInfo(
        @SerializedName("hot_word")
        var hotWord: String,
        @SerializedName("place_list")
        var placeList: List<PlaceItem>,
        @SerializedName("map_url")
        var mapUrl: String,
        @SerializedName("map_width")
        var mapWidth: String,
        @SerializedName("map_height")
        var mapHeight: String,
        @SerializedName("map_background_color")
        var mapBackgroundColor: String,
        @SerializedName("picture_version")
        var pictureVersion: Long,
        @SerializedName("open_site")
        var openSiteId: Int
) : Serializable